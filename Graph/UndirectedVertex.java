public class UndirectedVertex extends Vertex {
    private int periodValue;
    private int saturation;
    
    public UndirectedVertex() {
        
    }

    public UndirectedVertex(int val) {
        setValue(val);
    }

    public UndirectedVertex(UndirectedVertex v) {
        setValue(v.getValue());
    }

    public UndirectedVertex(Integer p) {
        periodValue = p;
    }

    public int getPeriodValue() {
        return periodValue;
    }
}
