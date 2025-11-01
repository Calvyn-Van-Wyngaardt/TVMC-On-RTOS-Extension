import java.util.LinkedList;
import java.util.Queue;

public class Entry {
    private final Queue<Task[]> permutations;
    private Task[] tasks;
    private int sameTask;
    private int diffTask;
    private Double currTime;

    public Entry() {
        permutations = new LinkedList<>();
        tasks = new Task[0];
        currTime = 0.0;
    }

    public Entry(Task[] t) {
        permutations = new LinkedList<>();
        tasks = new Task[t.length];
        for (int i = 0; i < t.length; i++) {
            tasks[i] = new Task(t[i]);
        }
        currTime = 0.0;
    }

    public void setTasks(Task[] newTaskset) {
        tasks = newTaskset;
    }

    public Task[] getTasks() {
        return tasks;
    }

    public boolean replaceTask(Task t) {
        for (int i = 0; i < tasks.length; i++) {
            if (tasks[i].getUUID().equals(t.getUUID())) {
                tasks[i] = t;
                return true;
            }
        }

        return false;
    }

    public void addTask(Task t) {
        Task[] newTaskArray = new Task[tasks.length + 1];
        for (int i = 0; i < tasks.length; i++) {
            newTaskArray[i] = tasks[i];
        }

        newTaskArray[tasks.length] = t;
        tasks = newTaskArray;
    }

    public Queue<Task[]> getPermutations() {
        return permutations;
    }

    public void getAllPossiblePermutations() {
        sameTask = 0;
        diffTask = 0;
        int numPermutations = factorial(tasks.length);
        getPerm(tasks, 0);
        System.out.println("Expected number of permutations: " + numPermutations);
        System.out.println("Calculated number of permutations: " + permutations.size());
        System.out.println("Same Task count: " + sameTask);
        System.out.println("Different Task count: " + diffTask);
    }

    public void setTime(Double time) {
        currTime = time;
    }

    public Double getTime() {
        return currTime;
    }

    public String printTaskArray(Task[] currPerm) {
        String out = "[";

        if (currPerm.length - 1 > 0) {
            for (int i = 0; i < currPerm.length - 1; i++) {
                out += currPerm[i].getLabel();
            }

            if (currPerm.length - 1 > 0)
                out += currPerm[currPerm.length - 1].getLabel();
        } 

        out += "]";

        return out;
    }

    public void getPerm(Task[] currPerm, int currIndex) {
        int swapPositions = (currPerm.length) - currIndex;
        System.out.println("== Level: " + currIndex);
        System.out.println("\tswapPositions: " + swapPositions);
        System.out.println("\tcurrPerm: " + printTaskArray(currPerm));

        // Base Case
        if (swapPositions <= 1 && currIndex == (currPerm.length - 1)) {
            //Add to permutation queue...
            if (!permutations.contains(currPerm)) {
                permutations.add(currPerm);
                System.out.println("Added permutation: " + printTaskArray(currPerm));
            }
        }
        

        for (int i = currIndex; i < currPerm.length; i++) {
            Task[] swapped = swapTasks(currPerm, i, currIndex);
            if (!permutations.contains(swapped)) {
                getPerm(swapped, (currIndex+1));
            }
        }
    }

    public boolean sameTaskSet(Task[] t1, Task[] t2) {
        for (Task x: t1) {
            for (Task y: t2) {
                if (!x.getUUID().equals(y.getUUID())) {
                    System.out.println("Not the same task...");
                    return false;
                }
            }
        }
        
        System.out.println("Same Task");
        return true;
    }

    public Task[] swapTasks(Task[] t, int index1, int index2) {
        Task[] swappedTasks = new Task[t.length];

        for (int i = 0; i < t.length; i++) {
            swappedTasks[i] = t[i];
        }

        Task temp = swappedTasks[index1];
        swappedTasks[index1] = swappedTasks[index2];
        swappedTasks[index2] = temp;

        return swappedTasks;

    }

    public int factorial(int startIndex) {
        int num = startIndex;
        for (int i = startIndex - 1; i > 0; i--) {
            num *= i;
        }

        return num;
    }

    public String printAllPermutations() {
        String out = "";

        for (Task[] ta: permutations) {
            out += printTaskArray(ta) + "\n";
        }

        return out;
    }
}