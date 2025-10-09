import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;

public class Entry {
    private Queue<Task[]> permutations;
    private Task[] tasks;

    public Entry() {
        permutations = new LinkedList<>();
        tasks = new Task[0];
    }

    public Entry(Task[] t) {
        permutations = new LinkedList<>();
        tasks = new Task[t.length];
        for (int i = 0; i < t.length; i++) {
            tasks[i] = new Task(t[i]);
        }
    }

    public void setAllPossiblePermutations() {
        //Use a hashmap to store the permutations... Let the key be the permutation itself 
    }

    public void getAllPossiblePermutations() {
        int numPermutations = factorial(tasks.length);
        permutations.add(getPermutations(tasks, 0));
        System.out.println("Expected number of permutations: " + numPermutations);
        System.out.println("Calculated number of permutations: " + permutations.size());

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

    public Task[] getPermutations(Task[] currPerm, int currIndex) {
        int swapPositions = (currPerm.length) - currIndex;  // First one is 3 = size - 1
        int startingIndex = currIndex;

        // Base Case
        if (currIndex >= currPerm.length && swapPositions == 0) {
            return currPerm;
        }

        System.out.println("== Level: " + currIndex);
        System.out.println("\tswapPositions: " + swapPositions);
        System.out.println("\tcurrPerm: " + printTaskArray(currPerm));
        
        //First it's the current perm...
        for (int i = 0; i < currPerm.length; i++) {
            System.out.println(String.format("Swapping i (%d) with currIndex(%d)", i, currIndex));
            Task[] swapped = swapTasks(currPerm, i, currIndex);
            System.out.println("After swapping: " + printTaskArray(swapped));
            
            //Pruning unnecessary branches
            if (sameTaskSet(currPerm, swapped)) {
                if (!permutations.contains(currPerm)) {   
                    permutations.add(getPermutations(currPerm, (currIndex+1)));
                }
            } else {
                if (!permutations.contains(currPerm)) {      //Replace this with a hashmap potentially for faster access? Current operation time = O(n); can be O(1)?
                    permutations.add(getPermutations(currPerm, (currIndex+1)));
                }
    
                if (!permutations.contains(swapped)) {
                    permutations.add(getPermutations(swapped, (currIndex+1)));
                }
            }

        }

        System.out.println("Adding permutation: " + printTaskArray(currPerm));

        return currPerm;
    }

    public boolean sameTaskSet(Task[] t1, Task[] t2) {
        for (Task x: t1) {
            for (Task y: t2) {
                if (!x.getUUID().equals(y.getUUID())) {
                    System.out.println("Not the same task...")
                    return false;
                } else {
                    System.out.println("Same Task");
                }
            }
        }

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
