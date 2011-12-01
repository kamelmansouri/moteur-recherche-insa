
package moteurrecherche.Recherche;

import java.sql.SQLException;
import java.util.ArrayList;
import moteurrecherche.Database.MySQLAccess;
import moteurrecherche.Database.Term;
import moteurrecherche.Database.TermInNode;
import moteurrecherche.ParserXML.Noeud;

public class ScoredTerm {
    private MySQLAccess db;

    private Term term;
    private double idf;
    private ArrayList<ScoredTermInNode> termNodesList;


//Consider a node containing 100 words wherein the word cow appears 3 times.
//Following the previously defined formulas, the term frequency (TF) for cow is
//then (3 / 100) = 0.03. Now, assume we have 10 million nodes and cow
//appears in one thousand of these. Then, the inverse document frequency is
//calculated as log(10 000 000 / 1 000) = 4. The tf–idf score is the product of
//these quantities: 0.03 × 4 = 0.12.
    public ScoredTerm(Term term, MySQLAccess db) throws ClassNotFoundException, SQLException {
        this.db = db;
        this.term = term;
        this.idf = 0;
        termNodesList = new ArrayList<ScoredTermInNode>();

        calculerIdf();
        calculerTf();
    }

    private void calculerIdf() throws SQLException {
        //Calcul de l'idf = nbTotalNoeuds / nbNoeudsContenantTerme
        idf = new  Double(Math.log10(db.getNumNodes() / db.getNbOfNodesWithTermId(term.getId())));
    }

    private void calculerTf() throws SQLException {

        /* Chercher le triplet <term_id, node_id, frequency> dans la table term_in_node */
        for(TermInNode termInNode : db.getTermInNodeByTermId(term.getId())) {

            Noeud node = db.getNodeByNodeId(termInNode.getNode_id());

            ScoredTermInNode scoredTiN = new ScoredTermInNode(termInNode);

            System.out.println("calcul tf:"+termInNode.getFrequency() +"/"+ node.getNbMots());
            
            scoredTiN.setTf(new Double((double) termInNode.getFrequency() / (double) node.getNbMots()));
            scoredTiN.setTfIdf(scoredTiN.getTf() * idf);
            
            termNodesList.add(scoredTiN);
            
        }
    }

    public double getIdf() {
        return idf;
    }

    public Term getTerm() {
        return term;
    }

    public ArrayList<ScoredTermInNode> getTermNodesList() {
        return termNodesList;
    }

    @Override
    public String toString() {
        String str = term.toString();
        return str + " -- idf = "+idf;
    }
}
