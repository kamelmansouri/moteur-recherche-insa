package moteurrecherche;

import java.sql.SQLException;
import moteurrecherche.ParserXML.XMLCollectionReader;
import moteurrecherche.ParserXML.NoeudText;
import moteurrecherche.ParserChaine.TraitementCollection;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Map.Entry;
import moteurrecherche.Database.MySQLAccess;
import moteurrecherche.ParserChaine.ChaineTraitee;
import moteurrecherche.ParserChaine.Terme;
import moteurrecherche.ParserChaine.TermeCollection;

public class Indexation {

    private final static String COLLECTION_PATH = "/resources/Collection/";
    private final static boolean DEBUG = true;
    MySQLAccess access; //accès BDD
    //Pour lire toutes les documents de la collection
    private XMLCollectionReader collectionReader;
    //Pour traiter la collection
    private TraitementCollection collectionTraitee;
    //Les chaines traitées dans toute la collection
    private ArrayList<ChaineTraitee> listeChainesTraitees;
    //La hashmap qui represente un document
    private ArrayList<NoeudText> listeNoeuds;
    private File files[]; //collection de fichiers à traiter

    public Indexation() throws ClassNotFoundException {
        access = new MySQLAccess();
        collectionReader = new XMLCollectionReader();
        collectionTraitee = new TraitementCollection();
        listeChainesTraitees = new ArrayList<ChaineTraitee>();
        listeNoeuds = null;
    }

    public void indexer() throws SQLException {

        

        // Filtre pour ne traiter que les fichier xml, ce filtre est utilisé quand
        // on lit les fichiers xml dans un dossier specifique
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };

        //Prend tous les fichiers xml dans le dossier specifié
        File directory = new File(Indexation.class.getResource(COLLECTION_PATH).getPath());

        files = directory.listFiles(filter);

        //Lit et traite tous les fichiers de la collection
        for (File f : files) {
                if (DEBUG) {
                    System.out.print(f.getName() + " : ");
                }

                listeNoeuds = collectionReader.readDocument(f);

                for (NoeudText node : listeNoeuds) {
                    if (node.getText() != null) {
                        listeChainesTraitees.add(
                                collectionTraitee.traiterChaine(
                                node.getText(),
                                node.getId()));
                        node.setText("");
                    }
                }

                if (DEBUG) {
                    System.out.print("Parsing XML : OK\n");
                }
            }


        if (DEBUG) {
            System.out.println("\n" + collectionTraitee.getListeTermes().size()
                    + " mots indexés\n");
            System.out.println(collectionTraitee.getListeTermes());
        }

        //Insertion des termes / noeuds etc dans la base
        insererDansBase();

    }

    /**
     * Insère les documents/termes/noeuds issus de la collection traitée
     */
    private void insererDansBase() throws SQLException {
        /* Insertion des documents de la collection */
        for (File f : files) {
            access.insertDocument(f.getName());
        }
        if (DEBUG) {
            System.out.println("Insertion des documents : OK\n");
        }

        /* Insertion dans la table termes */
        for (Entry<String, TermeCollection> entry :
                collectionTraitee.getListeTermes().entrySet()) {

            String mot = entry.getKey();
            TermeCollection termeCollection = entry.getValue();

            access.insertTerm(
                    termeCollection.getIdTerme(), 
                    mot, 
                    termeCollection.getFrequence()
                    );
        }
        if (DEBUG) {
            System.out.println("Insertion des termes : OK\n");
        }


        /* Insertion des noeuds */
        int parent;


        for (NoeudText noeud : this.listeNoeuds) {
            if (noeud != null) {
                if(DEBUG) System.out.print("DocID:" + noeud.getIdDoc() + " ");
                access.insertNode(
                        noeud.getId(),
                        noeud.getIdDoc(),
                        noeud.getLabel(),
                        noeud.getIdParent());
            }
        }
        if (DEBUG) {
            System.out.println("Insertion des noeuds : OK\n");
        }

        /* Insertion des term_in_node et de la position */
        for(ChaineTraitee chaine : listeChainesTraitees) {
            for(Terme terme : chaine.getListeTermes()) {
                access.insertTermInNode(
                        collectionTraitee.getListeTermes().get(terme.getMot()).getIdTerme(), 
                        terme.getIdNoeud(), 
                        -1);
                
                access.insertTermPos(
                        collectionTraitee.getListeTermes().get(terme.getMot()).getIdTerme(), 
                        terme.getIdNoeud(), 
                        terme.getPos());
                
            }
        }
        if (DEBUG) {
            System.out.println("Insertion des term_in_node + position : OK\n");
        }

    }
}
