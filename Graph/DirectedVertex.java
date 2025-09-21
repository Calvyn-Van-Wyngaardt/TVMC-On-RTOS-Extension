public class DirectedVertex extends Vertex {
    int remainder;
    UndirectedGraph G_h;

    public DirectedVertex() {
    
    }

    public DirectedVertex(int val) {
        setValue(val);
        G_h = new UndirectedGraph(val);
        G_h.initializeEdges();
        G_h.initializeVertices();
    }

    public DirectedVertex(DirectedVertex v) {
        setValue(v.getValue());
    }

    public int getRemainder() {
        return remainder;
    }

    public void setRemainder(int r) {
        remainder = r;
    }
}
