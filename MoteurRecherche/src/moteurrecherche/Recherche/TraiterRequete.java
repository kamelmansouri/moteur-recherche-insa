package moteurrecherche.Recherche;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import javax.media.j3d.Node;
import moteurrecherche.Database.MySQLAccess;
import moteurrecherche.Database.Term;
import moteurrecherche.Database.TermInNode;
import moteurrecherche.ParserChaine.TraitementMot;
import moteurrecherche.ParserXML.ChercherParagraphe;
import moteurrecherche.ParserXML.Noeud;
import org.jdom.JDOMException;

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

    public void retournerParagraphesReponse(ArrayList<ScoredTermInNode> list, int maxParagraphes) 
            throws SQLException, JDOMException, IOException {
        int node_id, id_doc;
        Noeud node;
        ScoredTermInNode term;

        for(int i=0; i < maxParagraphes; i++) {
            term = list.get(i);

            node_id = term.getTermInNode().getNode_id();

            node = db.getNodeByNodeId(node_id);

            id_doc = node.getIdDoc();
            
            ChercherParagraphe.renvoyerParagraphe(
                    calculerHierarchie(term), db.getDocNameById(id_doc));
        }
    }

    private String calculerHierarchie(ScoredTermInNode term) throws SQLException {
        String path = "";
        int lvl = 0;
        int node_id = term.getTermInNode().getNode_id();

        Noeud node = db.getNodeByNodeId(node_id);


        while (!node.getLabel().equals("BALADE")) {

            if (node.getLabel().equals("P")) {
                lvl = node.getId() - node.getIdParent(); // +/- 1, à vérifier
                path = "P/" + lvl;
            }
            else {
                path = node.getLabel() + "/" + path;
            }

            node_id = node.getIdParent();
            node = db.getNodeByNodeId(node_id);
        }

        return path;
    }

    public ArrayList<ScoredTermInNode> getScoredTermsInNodes() throws SQLException, ClassNotFoundException {
        Term term;
        scoredTerms = new ArrayList<ScoredTerm>();
        ArrayList<ScoredTermInNode> scoredTermsInNodes = new ArrayList<ScoredTermInNode>();

        for (String mot : listeMotsRequete) {
            term = db.getTermByTermValue(mot);

            scoredTerms.add(new ScoredTerm(term, db));
        }

        for (ScoredTerm scTerm : scoredTerms) {
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

        for (int i = 0; i < listeMotsRequete.length; i++) {
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
        for (String mot : listeMotsRequete) {
            System.out.print(" " + mot + " ");
        }
        System.out.print("\n");
    }
}
