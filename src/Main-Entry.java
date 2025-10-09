public class Main {
    public static void main(String[] args) {
        Task[] t = new Task[4];
        t[0] = new Task("t1", 5, 5, 5, 0);
        t[1] = new Task("t2", 5, 5, 5, 0);
        t[2] = new Task("t3", 5, 5, 5, 0);
        t[3] = new Task("t4", 5, 5, 5, 0);
        Entry e = new Entry(t);
        e.getAllPossiblePermutations();
        System.out.println(e.printAllPermutations());
    }
}
