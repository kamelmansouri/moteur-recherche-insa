
package moteurrecherche.ParserChaine;

import java.text.Normalizer;

public class TraiterRequete {
    private final static int MAX_MOTS = 20;

    private String requete;
    private String[] listeMotsRequete;

    public TraiterRequete(String req) {
        requete = req;
        listeMotsRequete = new String[MAX_MOTS];

        formaterRequeteEntree();
        
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
