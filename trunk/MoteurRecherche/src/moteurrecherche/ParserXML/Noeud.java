
package moteurrecherche.ParserXML;

public class Noeud {
    private int id;
    private int idDoc;
    private int idParent;
    private String label;
    private String text;
    private String path;
    private int nbMots;

    /* Pour le modèle servant au parsing xml */
    public Noeud(int id, int idDoc, int idParent, String label, String text, String path){
        this.id       = id;
        this.idDoc    = idDoc;
        this.idParent = idParent;
        this.label    = label;
        this.text     = text;
        this.path     = path;
    }

    /* Pour la database */
    public Noeud(int id, int idDoc, String label, int idParent, int words, String path){
        this.id       = id;
        this.idDoc    = idDoc;
        this.idParent = idParent;
        this.label    = label;
        this.nbMots   = words;
        this.text     = null;
        this.path     = path;
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

    public int getNbMots() {
        return nbMots;
    }

    public void setNbMots(int nbMots) {
        this.nbMots = nbMots;
    }

    @Override
    public String toString() {
        return id + " / nb mots: "+ nbMots;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
