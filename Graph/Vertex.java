public class Vertex {
    // "value" represents the value of the current element in set H, 
    // as well as the number of colors used to color Graph G_h
    private int value;

    public Vertex() {
        value = 0;
    }

    public Vertex(int val) {
        value = val;
    }

    public Vertex(Vertex v) {
        value = v.getValue();
    }   

    public int getValue() {
        return value;
    }

    public void setValue(int v) {
        value = v;
    }
}