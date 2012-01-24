
package moteurrecherche.Database;

public class Term {
    private int id;
    private String value;
    private int frequency;

    public Term(int i, String v, int f) {
        id = i;
        value = v;
        frequency = f;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(" + id + ", " + value + ", " + frequency + ")";
    }
}
