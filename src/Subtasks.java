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
        int subtasksCreated = 0;
        for (int i = 0; i < tasks.size(); i++) {
            int numSubTasks = calculateNumSubtasks(tasks.get(i));
            Task currTask = tasks.get(i);
            int remainder = (int) (currTask.getWCET() % timeslice);
            System.out.println("REMAINDER: " + remainder);
            for (int j = 0; j < numSubTasks; j++) {
                subtasksCreated += 1;
                subtasks.add(new Task(String.format("t%d.%d", i + 1, j + 1), timeslice, (j+1)*timeslice, (j+1)*timeslice, defaultOccurrance));
                System.out.println("Subtask added:\n" + subtasks.get(subtasksCreated-1).toString());
            }

            // If the WCET cannot be divided without a remainder, the remainder value is assigned to last subtask's WCET...
            if (remainder > 0) {
                subtasks.add(new Task(String.format("t%d.%d", i + 1, numSubTasks), remainder, (numSubTasks+1)*timeslice, (numSubTasks+1)*timeslice, defaultOccurrance));
            } else {
                subtasks.add(new Task(String.format("t%d.%d", i + 1, numSubTasks), timeslice, (numSubTasks+1)*timeslice, (numSubTasks+1)*timeslice, defaultOccurrance));
            }
        }
    }

    public void createAndWriteToIntermediateFile() {
        File intermediateFile = new File(taskSetLabel + "-intermediate.txt");
        try {
            if (intermediateFile.createNewFile()) {
                System.out.println("Intermediate file created!");
                FileWriter writer = new FileWriter(taskSetLabel + "-intermediate.txt", true);

                for (Task st: subtasks) {
                    System.out.println("WCET: " + st.getWCET());
                    System.out.println("Deadline: " + st.getDeadline());
                    System.out.println("Period: " + st.getPeriod());
                    writer.write(String.format("t%d.%d %f %f %f\n", st.getWCET(), st.getDeadline(), st.getPeriod()));
                }

                writer.close();
            }
        } catch (Exception e) {
            System.out.println("Subtasks - [createAndWriteToIntermediateFile]: Could not create/write to intermediate file:");
            e.printStackTrace();
        }
    }
}
