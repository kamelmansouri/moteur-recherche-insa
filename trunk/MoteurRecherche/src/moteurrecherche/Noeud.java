
package moteurrecherche;

public class Noeud {
    private int idDoc;
    private int idParent;
    private String label;

    public Noeud(int idDoc, int idParent, String label){
        this.idDoc    = idDoc;
        this.idParent = idParent;
        this.label    = label;
    }

    public int getDocId() {
        return idDoc;
    }

    public void setDocId(int docId) {
        this.idDoc = docId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getParentId() {
        return idParent;
    }

    public void setParentId(int parentId) {
        this.idParent = parentId;
    }
    
}
