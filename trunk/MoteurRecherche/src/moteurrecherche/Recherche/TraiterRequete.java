
package moteurrecherche.Recherche;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import moteurrecherche.Database.MySQLAccess;
import moteurrecherche.Database.Term;
import moteurrecherche.Database.TermInNode;
import moteurrecherche.ParserChaine.TraitementMot;

public class TraiterRequete {
    private final static int MAX_MOTS = 20;
    private final MySQLAccess db;

    private String requete;
    private String[] listeMotsRequete;
    private ArrayList<ScoredTerm> scoredTerms;

    public TraiterRequete(String req) throws ClassNotFoundException {
        requete = req;
        listeMotsRequete = new String[MAX_MOTS];
        db = new MySQLAccess();

        formaterRequeteEntree();
        
    }

    public ArrayList<ScoredTermInNode> getScoredTermsInNodes() throws SQLException, ClassNotFoundException {
        Term term;
        scoredTerms = new ArrayList<ScoredTerm>();
        ArrayList<ScoredTermInNode> scoredTermsInNodes = new ArrayList<ScoredTermInNode>();

        for(String mot : listeMotsRequete) {
            term = db.getTermByTermValue(mot);

            scoredTerms.add(new ScoredTerm(term, db));
        }

        for(ScoredTerm scTerm : scoredTerms) {
            scoredTermsInNodes.addAll(scTerm.getTermNodesList());
        }

        Collections.sort(scoredTermsInNodes, new ComparateurTFIDF());

        return scoredTermsInNodes;
    }

    private void formaterRequeteEntree() {
        /* ToDo: prendre en compte les requêtes avec guillemets */

        /* Mettre la chaine en minuscule et remplacer les accents */
        requete = requete.toLowerCase();

        /* Separer les mots de la chaîne */
        String delimiteur = "[^a-z]"; //On ne garde que les lettres
        listeMotsRequete = requete.split(delimiteur);

        /* Formater chaque mot */
        TraitementMot motTraite = new TraitementMot();
        String mot;

        for(int i=0; i < listeMotsRequete.length; i++) {
            motTraite.setMot(listeMotsRequete[i]);
            motTraite.formaterMot();
            
            listeMotsRequete[i] = motTraite.getMot();
        }
    }

    public String[] getListeMotsRequete() {
        return listeMotsRequete;
    }

    public String getRequete() {
        return requete;
    }

    public void afficherMotsRequete() {
        for(String mot : listeMotsRequete) {
            System.out.print(" "+mot+" ");
        }
        System.out.print("\n");
    }
}
