
package moteurrecherche.ParserChaine;

class Terme {
    private String mot;
    private int idNoeud;
    private int position;

    public Terme(String mot, int idNoeud, int pos) {
        this.mot = mot;
        this.idNoeud = idNoeud;
        this.position = pos;
    }

    public String getMot() {
        return this.mot;
    }

    public int getPositionDansListeNoeuds() {
        return idNoeud;
    }

    public int getPos() {
        return position;
    }
    
    @Override
    public String toString() {
        return "("+mot+", "+idNoeud+", "+position+")";
    }

}
