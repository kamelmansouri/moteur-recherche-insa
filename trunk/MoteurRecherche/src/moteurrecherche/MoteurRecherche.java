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

        //Indexation indexation = new Indexation();
        //indexation.indexer();

        
//        System.out.print("Veuillez entrer votre requête:  ");
//        Scanner sc = new Scanner(System.in);
//
//        String ligneLue = sc.nextLine();
        System.out.print("Veuillez entrer votre requête:  ");
        Scanner sc = new Scanner(System.in);

        String ligneLue = sc.nextLine();


        //TraiterRequete requete = new TraiterRequete(ligneLue, 5);
//         EvaluerPerformance eval = new EvaluerPerformance(25);

//        System.out.println(list);

        //requete.retournerParagraphesReponse(list, 1);

        ParserOntologie ontologieParser = new ParserOntologie();
        ArrayList<String> listeMotsAAjouter = ontologieParser.getMotsAAjouter(ligneLue);

        for(String motAAjouter : listeMotsAAjouter)
            System.out.println(motAAjouter);

        ParserOntologieAvecReasoner parser = new ParserOntologieAvecReasoner();
        ArrayList<String> listeMotsAAjouter2 = parser.getMotsAAjouter(ligneLue);

        for(String motAAjouter : listeMotsAAjouter2)
            System.out.println(motAAjouter);

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
