package moteurrecherche;

import java.util.Arrays;
import java.util.HashMap;

public class MoteurRecherche {

    public static void main(String[] args) {
//        MySQLAccess access = new MySQLAccess();
        
        
        XMLNoeudReader noeudReader = new XMLNoeudReader();
        HashMap<Integer, Noeud> noeudMap = noeudReader.read("/resources/Collection/d001.xml");
        
        
        TraitementCollection traiteCollection = new TraitementCollection();
        
        String str = "La douceur de l'air, un ciel voilé, les cloches qui sonnent, "
                + "cent objets muets préparés pour frapper l'imagination, "
                + "et battre ce briquet éternel et mystérieux du coeur d'où jaillira "
                + "l'éternelle étincelle mystique : c'est Lourdes, où règne "
                + "la Vierge.";

        System.out.println(traiteCollection.traiterChaine(str, 0));
        
        str = "je suis allé tout droit droit droit à la grotte de la révélation. "
                + "L'eau susurre et se froisse en glissant, les cloches tintent "
                + "sur les couvents, et de la prairie voisine s'élève le "
                + "bêlement des troupeaux. Quelque chose d'ineffable flotte "
                + "dans cette solitude.";
        
        System.out.println(traiteCollection.traiterChaine(str, 1));

        System.out.println(traiteCollection.getListeTermes());



    }

    /*
     * OPTIMISATION :
     * Permet de trier la HashMap par clef, par ordre alphabetique
     * Utilisation : permettra d'insérer les données dans la bdd
     * directement par ordre alphabetique, ce qui peut accélérer
     * la recherche en dirigeant la requête à un endroit particulier
     * Ex: on cherche un mot qui commence par z, on part de la fin
     * de la table terms
     * /
    public void trierHashMap() {
        Object[] key = paragraphe.getListeTermes().keySet().toArray();
        Arrays.sort(key);

        for (int i = 0; i < key.length; i++) {
            System.out.println(key[i] + ", " + paragraphe.getListeTermes().get(key[i]));
        }
    } */
}