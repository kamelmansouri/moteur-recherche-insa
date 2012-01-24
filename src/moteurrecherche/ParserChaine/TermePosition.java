
package moteurrecherche.ParserChaine;

public class TermePosition {
    private int idTerme;
    private int idNoeud;
    private int position;

    public TermePosition(int idTerme, int idNoeud, int pos) {
        this.idTerme = idTerme;
        this.idNoeud = idNoeud;
        this.position   = pos;
    }
    
    public int getIdTerme() {
        return this.idTerme;
    }

    public int getIdNoeud() {
        return idNoeud;
    }

    public int getPos() {
        return position;
    }
    
    @Override
    public String toString() {
        return "("+idTerme+", "+idNoeud+", "+position+")";
    }

}
