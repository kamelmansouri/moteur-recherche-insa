
package moteurrecherche.Recherche;

import moteurrecherche.Database.TermInNode;

public class ScoredTermInNode {
    private TermInNode term;
    private double tf;
    private double tfIdf;

    public ScoredTermInNode(TermInNode term) {
        this.term = term;
        tf = 0;
        tfIdf = 0;
    }

    public double getTf() {
        return tf;
    }

    public void setTf(double tf) {
        this.tf = tf;
    }

    

    public TermInNode getTermInNode() {
        return term;
    }

    public double getTfIdf() {
        return tfIdf;
    }

    public void setTfIdf(double tfIdf) {
        this.tfIdf = tfIdf;
    }


    @Override
    public String toString() {
        String str = term.toString();
        return str + " -- tf="+tf+" et Tf.Idf="+tfIdf+"\n";
    }
}
