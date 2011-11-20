
package moteurrecherche;

public class NoeudText {
    private int id;
    private int idDoc;
    private int idParent;
    private String label;
    private String text;

    public NoeudText(int id, int idDoc, int idParent, String label, String text){
        this.id       = id;
        this.idDoc    = idDoc;
        this.idParent = idParent;
        this.label    = label;
        this.text     = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDoc() {
        return idDoc;
    }

    public void setIdDoc(int idDoc) {
        this.idDoc = idDoc;
    }

    public int getIdParent() {
        return idParent;
    }

    public void setIdParent(int idParent) {
        this.idParent = idParent;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    
}
