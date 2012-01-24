
package moteurrecherche.ParserChaine;


public class TermeCollection {
    private int idTerme;
    private int frequence;
    
    public TermeCollection(int idTerme) {
        this.idTerme    = idTerme;
        this.frequence   = 1;
    }

    public int getFrequence() {
        return frequence;
    }

    public int getIdTerme() {
        return idTerme;
    }
    
    
    
    public void incrementerFrequence() {
        this.frequence++;
    }
    
    @Override
    public String toString() {
        return "["+ idTerme +"] freq:" + frequence;
    }
}
