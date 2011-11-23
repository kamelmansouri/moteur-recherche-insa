
package moteurrecherche.ParserChaine;

public class TermeDansNoeud {
    private int idTerme;
    private int idNoeud;
    private int freq;

    public TermeDansNoeud(int idTerme, int idNoeud, int freq) {
        this.idTerme = idTerme;
        this.idNoeud = idNoeud;
        this.freq   = freq;
    }
    
    public int getIdTerme() {
        return this.idTerme;
    }

    public int getIdNoeud() {
        return idNoeud;
    }

    public int getFreq() {
        return freq;
    }
    
    @Override
    public String toString() {
        return "("+idTerme+", "+idNoeud+", "+freq+")";
    }

}
