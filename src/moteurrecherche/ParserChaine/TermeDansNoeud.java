
package moteurrecherche.ParserChaine;

public class TermeDansNoeud {
    private int idTerme;
    private int idNoeud;
    private int freq;

    public TermeDansNoeud(int idTerme, int idNoeud) {
        this.idTerme = idTerme;
        this.idNoeud = idNoeud;
        this.freq    = 1;
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
    
    public void incrementerFreq() {
        freq++;
    }
    
    @Override
    public String toString() {
        return "("+idTerme+", "+idNoeud+", "+freq+")";
    }

}
