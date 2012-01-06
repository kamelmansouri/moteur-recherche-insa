package moteurrecherche.Recherche;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import moteurrecherche.Database.MySQLAccess;
import moteurrecherche.Database.Term;
import moteurrecherche.Ontologie.ParserOntologie;
import moteurrecherche.ParserChaine.TraitementMot;
import moteurrecherche.ParserXML.ChercherParagraphe;
import moteurrecherche.ParserXML.Noeud;
import moteurrecherche.TestQrel.Resultat;
import org.jdom.JDOMException;

public class TraiterRequete {

    private final static int MAX_MOTS = 20;
    private final static String STOP_LISTE_PATH = "/resources/stopliste.txt";
    private final MySQLAccess db;
    private ArrayList<String> stopListe;
    private String requete;
    private int maxParagraphes;
    private ArrayList<String> listeMotsRequete;
    private ArrayList<ScoredTerm> scoredTerms;
    private HashMap<Integer, Integer> poidsTermesRequete; //<id_term, poids>
    //<node_id, <term_id, ScoredTermInNode>>
    private HashMap<Integer, ArrayList<ScoredTermInNode>> tableTermesAvecScores;
    private HashMap<Integer, Double> similariteNoeud; //<id_node, similarite>
    private TreeMap<Integer, Double> similariteNoeudTriee;
    private ArrayList<Resultat> listeResultats;

    public TraiterRequete(String req, int maxP) throws ClassNotFoundException, SQLException, JDOMException, IOException {
        requete = req;
        maxParagraphes = maxP;
        stopListe = new ArrayList<String>();
        listeMotsRequete = new ArrayList<String>();
        db = new MySQLAccess();
        poidsTermesRequete = new HashMap<Integer, Integer>();
        //<node_id, <term_id, Item>>
        tableTermesAvecScores = new HashMap<Integer, ArrayList<ScoredTermInNode>>();
        similariteNoeud = new HashMap<Integer, Double>();
        listeResultats = new ArrayList<Resultat>();

        chargerStopListe();
        formaterRequeteAvantOntologie();

        //ToDo intégrer ontologie
        separerMots(ajouterMotsOntologie());
        formaterRequeteApresOntologie();
        computeScoredTermsInNodes();
        calculeSimilarite();
        retournerParagraphesReponse(maxParagraphes);
    }

    public void retournerParagraphesReponse(int maxParagraphes)
            throws SQLException, JDOMException, IOException {
        int id_doc, cptParagraphe = 0;
        Noeud node = null;

        for (Integer node_id : similariteNoeudTriee.keySet()) {
            if (cptParagraphe++ == maxParagraphes) {
                break;


            }
            node = db.getNodeByNodeId(node_id);

            id_doc = node.getIdDoc();

            System.out.println("+++PARAGRAPHE " + cptParagraphe + ":+++");
            listeResultats.add(ChercherParagraphe.renvoyerParagraphe(node.getPath(), db.getDocNameById(id_doc)));

        }
    }

    public ArrayList<ScoredTermInNode> computeScoredTermsInNodes() throws SQLException, ClassNotFoundException {
        Term term;
        scoredTerms = new ArrayList<ScoredTerm>();
        ArrayList<ScoredTermInNode> scoredTermsInNodes = new ArrayList<ScoredTermInNode>();

        for (String mot : listeMotsRequete) {

            term = db.getTermByTermValue(mot);

            if (term != null) {
                poidsTermesRequete.put(term.getId(), 1); //attribution du poids

                scoredTerms.add(new ScoredTerm(term, db));
            }
        }


        int node_id;

        for (ScoredTerm scTerm : scoredTerms) {
            //Pour chaque triplet <term, noeud, freq> contenant term
            for (ScoredTermInNode scTiN : scTerm.getTermNodesList()) {

                //On ne recherche pas dans les noeuds SOUS TITRE
                if (db.getNodeByNodeId(
                        scTiN.getTermInNode().getNode_id()).getLabel().compareTo("SOUS-TITRE") != 0) {
                    //Récupérer l' id noeud
                    node_id = scTiN.getTermInNode().getNode_id();

                    //Si nouveau, créer liste
                    if (tableTermesAvecScores.get(node_id) == null) {
                        ArrayList<ScoredTermInNode> listeNoeuds = new ArrayList<ScoredTermInNode>();

                        listeNoeuds.add(scTiN);

                        tableTermesAvecScores.put(node_id, listeNoeuds);
                    } //Sinon ajouter à la liste déjà existante
                    else {
                        tableTermesAvecScores.get(node_id).add(scTiN);
                    }
                }
            }


            scoredTermsInNodes.addAll(scTerm.getTermNodesList());
        }

        return scoredTermsInNodes;
    }

    private void calculeSimilarite() {
        Double sum1, sum2, sum3, finalResult;

        //Calculer la somme des poids des termes de la requête
        sum2 = 0.0;
        for (Entry<Integer, Integer> termeRequete : poidsTermesRequete.entrySet()) {
            sum2 += termeRequete.getValue() * termeRequete.getValue();
        }

        for (Entry<Integer, ArrayList<ScoredTermInNode>> entry : tableTermesAvecScores.entrySet()) {
            Integer id_node = entry.getKey();
            ArrayList<ScoredTermInNode> listeScTiN = entry.getValue();

            sum1 = 0.0;
            sum3 = 0.0;

            for (ScoredTermInNode scoredNode : listeScTiN) {
                int id_term = scoredNode.getTermInNode().getTerm_id();

                //Mesure du Cosinus : sum(xi*yi) / sqrt(sum(xi^2) * sum(yi^2))
                sum1 += poidsTermesRequete.get(id_term) * scoredNode.getTfIdf();

                sum3 += scoredNode.getTfIdf() * scoredNode.getTfIdf();
            }

            finalResult = sum1 / (Math.sqrt(sum2 * sum3));
            similariteNoeud.put(id_node, finalResult);
        }

        similariteNoeudTriee = new TreeMap(new ValueComparator(similariteNoeud));
        similariteNoeudTriee.putAll(similariteNoeud);
    }

    public TreeMap<Integer, Double> getSimilariteNoeudTriee() {
        return similariteNoeudTriee;
    }

    private void separerMots(ArrayList<String> motsASeparer) {
        ArrayList<String> motsEnPlus = new ArrayList<String>();

        /* Separer les mots de la chaîne */
        String delimiteur = "[^a-z0-9]"; //On ne garde que les lettres et chiffres

        for(String mot : motsASeparer) {
            listeMotsRequete.addAll(Arrays.asList(mot.split(delimiteur)));
        }

        Set set = new HashSet() ;
        set.addAll(listeMotsRequete) ;
        listeMotsRequete.clear();
        listeMotsRequete.addAll(set);
    }

    private void formaterRequeteAvantOntologie() {
        /* ToDo: prendre en compte les requêtes avec guillemets */

        /* Mettre la chaine en minuscule et remplacer les accents */
        requete = requete.toLowerCase();

        TraitementMot requeteTraitee = new TraitementMot(requete);
        requeteTraitee.remplacerAccents();

        /* Separer les mots de la chaîne */
        String delimiteur = "[^a-z0-9]"; //On ne garde que les lettres et chiffres

        listeMotsRequete.addAll(Arrays.asList(requete.split(delimiteur)));
    }

    public void formaterRequeteApresOntologie() {
        /* Formater chaque mot */
        TraitementMot motTraite = new TraitementMot();
        ArrayList<String> toRemove = new ArrayList<String>();

        for (int i = 0; i < listeMotsRequete.size(); i++) {
            motTraite.setMot(listeMotsRequete.get(i));
            motTraite.formaterMot();

            if (!stopListe.contains(motTraite.getMot())) {
                listeMotsRequete.set(i, motTraite.getMot());
            } else {
                toRemove.add(listeMotsRequete.get(i));
            }
        }

        //Supprimer les mots contenus dans la stopListe
        for (String mot : toRemove) {
            listeMotsRequete.remove(mot);
        }
    }

    public ArrayList<String> getListeMotsRequete() {
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

    /**
     * Lit le fichier stopliste.txt et charge les mots formatés (tronqués,
     * lemmatisés, suppression des doublons) dans l'ArrayList stopListe;
     */
    private void chargerStopListe() {
        InputStream ips = TraiterRequete.class.getResourceAsStream(STOP_LISTE_PATH);

        InputStreamReader ipsr = new InputStreamReader(ips);
        BufferedReader reader = new BufferedReader(ipsr);

        String ligne, mot;

        try {
            while ((ligne = reader.readLine()) != null) {
                TraitementMot traitementMot = new TraitementMot(ligne);
                traitementMot.formaterMot();
                traitementMot.remplacerAccents();
                mot = traitementMot.getMot();

                //Ajout du mot s'il n'existe pas déjà dans la liste
                if (!stopListe.contains(mot)) {
                    stopListe.add(mot);
                    //System.out.println("Mot stoplist: "+mot);
                }
            }
        } catch (IOException ex) {
            System.out.println("Lecture de la stop liste : ECHEC.\n" + ex.getMessage());
        }
    }

    public ArrayList<Resultat> getListeResultats() {
        return listeResultats;
    }

    private ArrayList<String> ajouterMotsOntologie() {
        int fenetreDeRecherche = 3;
        int start;
        ArrayList<String> termesEnPlus = new ArrayList<String>();
        ArrayList<String> motsRequetes = (ArrayList<String>) listeMotsRequete.clone();
        ArrayList<String> subList = new ArrayList<String>();
        String chaineCherchee = "";
        ParserOntologie parser = new ParserOntologie();

        while (fenetreDeRecherche > 0) {
            start = 0;

            while (fenetreDeRecherche + start <= motsRequetes.size()) {
                chaineCherchee = "";
                subList.clear();

                    subList.addAll(motsRequetes.subList(start, fenetreDeRecherche + start));


                System.out.println("Sublist avec fenetre" + fenetreDeRecherche + " = "
                            + subList);

                for (int x = 0; x < subList.size(); x++) {
                    if (subList.size() == 1 || x == subList.size() - 1) {
                        chaineCherchee += subList.get(x);
                    } else {
                        chaineCherchee += subList.get(x) + " ";
                    }
                }


                ArrayList<String> motsAAjouter = parser.getMotsAAjouter(chaineCherchee);
                if (motsAAjouter.isEmpty()) {
                    start++;
                } else {
                    termesEnPlus.addAll(motsAAjouter);
                    motsRequetes.removeAll(subList);
                }
            }

            fenetreDeRecherche--;
        }

        return termesEnPlus;
    }
}
