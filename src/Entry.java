import java.util.ArrayList;

public class Entry {
    private ArrayList <Task[]> permutations;
    private Task[] tasks;

    public Entry() {
        permutations = new ArrayList<>();
        tasks = new Task[0];
    }

    public void setAllPossiblePermutations() {
        //find all possible permutations
        
    }

    public ArrayList<Task[]> getAllPossiblePermutations() {
        return permutations;
    }
}
