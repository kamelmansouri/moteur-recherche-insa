
package moteurrecherche.Database;

public class TermInNode {
    private int term_id;
    private int node_id;
    private int frequency;

    public TermInNode(int t, int n, int f) {
        term_id = t;
        node_id = n;
        frequency = f;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getNode_id() {
        return node_id;
    }

    public int getTerm_id() {
        return term_id;
    }

    @Override
    public String toString() {
        return "(" + term_id + ", " + node_id + ", " + frequency + ")";
    }
}
