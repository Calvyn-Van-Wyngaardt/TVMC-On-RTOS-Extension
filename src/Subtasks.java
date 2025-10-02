import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class Subtasks {
    private String taskSetLabel;
    private ArrayList<Task> tasks;
    private ArrayList<Task> subtasks;
    private double timeslice;

    public Subtasks() {
        taskSetLabel = "EMPTY";
        tasks = new ArrayList<>();
        subtasks = new ArrayList<>();
        timeslice = 0;
    }

    public Subtasks(TaskGenerator taskGen, double t) {
        taskSetLabel = taskGen.getLabel();
        tasks = taskGen.getTaskSet();
        subtasks = new ArrayList<>();
        timeslice = t;
    }

    public int calculateNumSubtasks(Task t) {
        return (int) Math.ceil(t.getWCET() / timeslice);
    }

    // Might use these if the task model changes to relative time...
    // public int calculatePeriod(Task t) {
    //     //
    // }

    // public int calculateDeadline(Task t) {
    //     //
    // }

    public int getMaxSubtasks(ArrayList<Integer> tasks) {
        int max = 0;
        Iterator<Integer> it = tasks.iterator();

        while (it.hasNext()) {
            int currVal = it.next();
            max = (currVal >= max ? currVal : max);
        }

        return max;
    }

    public void createSubTasks() {
        int defaultOccurrance = 0;
        double totalDeadline = 0;
        double totalPeriod = 0;
        ArrayList<Integer> numSubtasksPerTask = new ArrayList<>();
        
        for (Task t : tasks) {
            int numSubTasks = calculateNumSubtasks(t);
            numSubtasksPerTask.add(numSubTasks);
        }

        int mostSubtasks = getMaxSubtasks(numSubtasksPerTask);
        int numTasks = tasks.size();

        // ArrayList<Integer> numSubTasksPerTask = getNumSubtasksForTaskset(tasks);

        // Interleaving subtasks in typical Round Robin Fashion
        for (int currSubTask = 0; currSubTask < mostSubtasks; currSubTask++) {
            for (int currTaskIndex = 0; currTaskIndex < numTasks; currTaskIndex++) {
                Task currTask = tasks.get(currTaskIndex);
                
                if (currSubTask < numSubtasksPerTask.get(currTaskIndex)) {
                    System.out.println("Creating task t" + (currTaskIndex+1) + "." + (currSubTask+1));
                    System.out.println("Current Sub Task: " + (currSubTask+1));
                    System.out.println("Current Task: " + (currTaskIndex+1));
                    System.out.println("Number of SubTasksPerTask size: " + numSubtasksPerTask.size());
                    if (currSubTask < numSubtasksPerTask.get(currTaskIndex) - 1) {
                        subtasks.add(new Task(String.format("t%d.%d", currTaskIndex + 1, currSubTask + 1), timeslice, (totalPeriod + timeslice), (totalDeadline + timeslice), defaultOccurrance));
                        totalDeadline += timeslice;
                        totalPeriod += timeslice;
                    } else {
                        // If the WCET cannot be divided without a remainder, the remainder value is assigned to last subtask's WCET...
                        int remainder = (int) (currTask.getWCET() % timeslice);
                        if (remainder > 0) {
                            subtasks.add(new Task(String.format("t%d.%d", currTaskIndex + 1, numSubtasksPerTask.get(currTaskIndex)), remainder, (totalPeriod + remainder), (totalDeadline + remainder), defaultOccurrance));
                            totalDeadline += remainder;
                            totalPeriod += remainder;
                        } else {
                            subtasks.add(new Task(String.format("t%d.%d", currTaskIndex + 1, numSubtasksPerTask.get(currTaskIndex)), timeslice, (totalPeriod + timeslice), (totalDeadline + timeslice), defaultOccurrance));
                            totalDeadline += timeslice;
                            totalPeriod += timeslice;
                        }
                    }
                } else {
                    System.out.println(String.format("Subtasks - [createSubTasks]: Current subtask index (%d) does not exist for task %d", currSubTask, (currTaskIndex + 1)));
                }

            }
        }

        // int subtasksCreated = 0;
        // for (int i = 0; i < tasks.size(); i++) {
        //     int numSubTasks = calculateNumSubtasks(tasks.get(i));
        //     Task currTask = tasks.get(i);
        //     int remainder = (int) (currTask.getWCET() % timeslice);
        //     System.out.println("REMAINDER: " + remainder);
        //     for (int j = 0; j < numSubTasks-1; j++) {
        //         subtasks.add(new Task(String.format("t%d.%d", i + 1, j + 1), timeslice, (totalPeriod + timeslice), (totalDeadline + timeslice), defaultOccurrance));
        //         totalDeadline += timeslice;
        //         totalPeriod += timeslice;
        //     }

        //     // If the WCET cannot be divided without a remainder, the remainder value is assigned to last subtask's WCET...
        //     if (remainder > 0) {
        //         subtasks.add(new Task(String.format("t%d.%d", i + 1, numSubTasks), remainder, (totalPeriod + remainder), (totalDeadline + remainder), defaultOccurrance));
        //         totalDeadline += remainder;
        //         totalPeriod += remainder;
        //     } else {
        //         subtasks.add(new Task(String.format("t%d.%d", i + 1, numSubTasks), timeslice, (totalPeriod + timeslice), (totalDeadline + timeslice), defaultOccurrance));
        //         totalDeadline += timeslice;
        //         totalPeriod += timeslice;
        //     }
        // }
    }

    public ArrayList<Integer> getNumSubtasksForTaskset(ArrayList<Task> ts) {
        ArrayList<Integer> subTasksPerTask = new ArrayList<>();
        for (Task t : tasks) {
            int currSubTasks = calculateNumSubtasks(t);
            subTasksPerTask.add(currSubTasks);
        }

        return subTasksPerTask;
    }

    public void createAndWriteToIntermediateFile() {
        File intermediateFile = new File(taskSetLabel + "-intermediate.txt");
        try {
            if (intermediateFile.createNewFile()) {
                System.out.println("Intermediate file created!");
            } else {
                System.out.println("Subtasks - [createAndWriteToIntermediateFile]: Failed to create intermediate file - already exists");
                // This can be done better... Fix later.
                intermediateFile.delete();
                intermediateFile.createNewFile();
            }
            FileWriter writer = new FileWriter(taskSetLabel + "-intermediate.txt", true);
            
            for (Task st: subtasks) {
                System.out.println("WCET: " + st.getWCET());
                System.out.println("Deadline: " + st.getDeadline());
                System.out.println("Period: " + st.getPeriod());
                writer.write(String.format("%s %d %d %d\n", st.getLabel(), (int) st.getWCET(), (int) st.getDeadline(), (int) st.getPeriod()));
            }

            writer.close();
        } catch (Exception e) {
            System.out.println("Subtasks - [createAndWriteToIntermediateFile]: Could not finish creating/writing file:");
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String out = "";
        for (Task st: subtasks) {
            out += st.toString() + "\n";
        }

        return out;
    }
}
