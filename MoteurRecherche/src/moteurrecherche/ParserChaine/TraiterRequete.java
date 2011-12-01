
package moteurrecherche.ParserChaine;

import java.sql.SQLException;
import java.util.ArrayList;
import moteurrecherche.Database.MySQLAccess;
import moteurrecherche.Database.TermInNode;

public class TraiterRequete {
    private final static int MAX_MOTS = 20;
    private final MySQLAccess db;

    private String requete;
    private String[] listeMotsRequete;

    public TraiterRequete(String req) throws ClassNotFoundException {
        requete = req;
        listeMotsRequete = new String[MAX_MOTS];
        db = new MySQLAccess();

        formaterRequeteEntree();
        
    }

    public ArrayList<TermInNode> getTermesEtNoeuds() throws SQLException {
        int idTerme;
        ArrayList<TermInNode> termesEtNoeuds = new ArrayList<TermInNode>();

        for(String mot : listeMotsRequete) {
            //Chercher l'id pour le terme considéré
            idTerme = db.getTermIdByTermValue(mot);

            /* Si le terme existe dans l'index, chercher le triplet
              <term_id, node_id, frequency> dans la table term_in_node */
            if(idTerme != -1) {
                termesEtNoeuds.addAll(
                    db.getTermInNodeByTermId(idTerme)
                    );
            }
        }

        return termesEtNoeuds;
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
