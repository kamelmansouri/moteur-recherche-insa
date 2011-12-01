package moteurrecherche;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import moteurrecherche.Database.MySQLAccess;
import moteurrecherche.Database.TermInNode;
import moteurrecherche.ParserXML.Noeud;
import moteurrecherche.Recherche.ScoredTerm;
import moteurrecherche.Recherche.ScoredTermInNode;
import moteurrecherche.Recherche.TraiterRequete;
import org.jdom.JDOMException;

public class MoteurRecherche {

    public static void main(String[] args) throws ClassNotFoundException, 
            SQLException, JDOMException, IOException {

        //Indexation indexation = new Indexation();
        //indexation.indexer();

        System.out.print("Veuillez entrer votre requête:  ");
        Scanner sc = new Scanner(System.in);

        String ligneLue = sc.nextLine();

        TraiterRequete requete = new TraiterRequete(ligneLue);
        ArrayList<ScoredTermInNode> list = requete.getScoredTermsInNodes();

        System.out.println(list);

        requete.retournerParagraphesReponse(list, 1);
        
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
