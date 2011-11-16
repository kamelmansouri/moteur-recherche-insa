
package moteurrecherche;

class Terme {
    private String mot;
    private int idNoeud;
    private int nbOccurrences;
    private int position;

    public Terme(String mot, int idNoeud, int pos) {
        this.mot = mot;
        this.idNoeud = idNoeud;
        this.nbOccurrences = 1;
        this.position = pos;
    }

    public String getMot() {
        return this.mot;
    }

    public int getNbOccurrences() {
        return nbOccurrences;
    }

    public int getPositionDansListeNoeuds() {
        return idNoeud;
    }

    public int getPos() {
        return position;
    }

    /**
     * Augmente le nombre d'occurrences du mot de 1
     */
    public void incrementerOccurrence() {
        this.nbOccurrences++;
    }

    

    @Override
    public String toString() {
        return "("+mot+", "+idNoeud+", "+nbOccurrences+", "+position+")";
    }

}
