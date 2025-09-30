import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

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

    public void createSubTasks() {
        int defaultOccurrance = 0;
        double totalDeadline = 0;
        double totalPeriod = 0;
        // int subtasksCreated = 0;
        for (int i = 0; i < tasks.size(); i++) {
            int numSubTasks = calculateNumSubtasks(tasks.get(i));
            Task currTask = tasks.get(i);
            int remainder = (int) (currTask.getWCET() % timeslice);
            System.out.println("REMAINDER: " + remainder);
            for (int j = 0; j < numSubTasks-1; j++) {
                subtasks.add(new Task(String.format("t%d.%d", i + 1, j + 1), timeslice, (totalPeriod + timeslice), (totalDeadline + timeslice), defaultOccurrance));
                totalDeadline += timeslice;
                totalPeriod += timeslice;
            }

            // If the WCET cannot be divided without a remainder, the remainder value is assigned to last subtask's WCET...
            if (remainder > 0) {
                subtasks.add(new Task(String.format("t%d.%d", i + 1, numSubTasks), remainder, (totalPeriod + remainder), (totalDeadline + remainder), defaultOccurrance));
                totalDeadline += remainder;
                totalPeriod += remainder;
            } else {
                subtasks.add(new Task(String.format("t%d.%d", i + 1, numSubTasks), timeslice, (totalPeriod + timeslice), (totalDeadline + timeslice), defaultOccurrance));
                totalDeadline += timeslice;
                totalPeriod += timeslice;
            }
        }
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

    public String toString() {
        String out = "";
        for (Task st: subtasks) {
            out += st.toString() + "\n";
        }

        return out;
    }
}
