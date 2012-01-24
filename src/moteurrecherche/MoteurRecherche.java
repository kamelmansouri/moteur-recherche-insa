package moteurrecherche;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import moteurrecherche.Ontologie.ParserOntologie;
import moteurrecherche.Ontologie.ParserOntologieAvecReasoner;
import moteurrecherche.Recherche.ScoredTermInNode;
import moteurrecherche.Recherche.TraiterRequete;
import moteurrecherche.TestQrel.EvaluerPerformance;
import org.jdom.JDOMException;

public class MoteurRecherche {

    public static void main(String[] args) throws ClassNotFoundException,
            SQLException, JDOMException, IOException {
        boolean continuer = true;
        EvaluerPerformance eval;

        do {
            System.out.print("\t==== MENU ====\n"
                    + "\t1. Indexation\n"
                    + "\t2. Requete personnalisee\n"
                    + "\t3. Evaluateur qrel (sans raisonneur)\n"
                    + "\t4. Evaluateur qrel (avec raisonneur)\n\n");

            Scanner sc = new Scanner(System.in);
            int choix = sc.nextInt();

            switch(choix) {
                case 1:
                    Indexation indexation = new Indexation();
                    indexation.indexer();
                    break;

                case 2:
                    System.out.print("Veuillez entrer votre requête:  ");

                    Scanner sc2 = new Scanner(System.in);
                    String ligneLue = sc2.nextLine();

                    TraiterRequete requete = new TraiterRequete(ligneLue, 5, false);
                    break;

//                case 3:
//                    System.out.println("Sans raisonneur, Precision à : 5, 10 ou 25? ");
//
//                    eval = new EvaluerPerformance(sc.nextInt(), false);
//                    break;
//
//                case 4:
//                    System.out.println("Avec raisonneur, Precision à : 5, 10 ou 25? ");
//
//                    eval = new EvaluerPerformance(sc.nextInt(), true);
//                    break;

                default:
                    break;
            }
        } while(continuer);

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
