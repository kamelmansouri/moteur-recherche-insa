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
import moteurrecherche.ParserChaine.TermePosition;
import moteurrecherche.ParserChaine.TermeCollection;
import moteurrecherche.ParserChaine.TermeDansNoeud;

public class Indexation {

    private final static String COLLECTION_PATH = "/resources/Collection/";
    private final static boolean DEBUG = true;
    MySQLAccess access; //accès BDD
    //Pour lire toutes les documents de la collection
    private XMLCollectionReader collectionReader;
    //Pour traiter la collection
    private TraitementCollection collectionTraitee;
    //La hashmap qui represente un document
    private ArrayList<NoeudText> listeNoeuds;
    private File files[]; //collection de fichiers à traiter

    public Indexation() throws ClassNotFoundException {
        access = new MySQLAccess();
        collectionReader = new XMLCollectionReader();
        collectionTraitee = new TraitementCollection();
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
        int idDocCourant=0;

        int limit = 1;
        //Lit et traite tous les fichiers de la collection
        for (File f : files) {
            if (DEBUG)
                System.out.print(f.getName() + " : ");

            if(limit == 0) break;
            
            limit--;

            listeNoeuds = collectionReader.readDocument(f);

            for (NoeudText node : listeNoeuds) {
                if (node.getText() != null) {
                    collectionTraitee.traiterChaine(node.getText(), node.getId());
                    node.setText(""); //evite le heap space out of memory
                }
            }

            if (DEBUG) {
                System.out.print("Parsing XML : OK\n");
            }
        }


        if (DEBUG) {
            System.out.println("\n" + collectionTraitee.getListeTermes().size()
                    + " mots indexés\n");
            //System.out.println(collectionTraitee.getListeTermes());
        }

        //Insertion des termes / noeuds etc dans la base
        insererDansBase();
        
//        System.out.println("Nombre de neouds: "+ listeNoeuds.size());
//        System.out.println("Nombre de termes dans noeud: "+ 
//                collectionTraitee.getListeTermesDansNoeud().size());
//        System.out.println("Nombre de termes position: "+ 
//                collectionTraitee.getListeTermesPosition().size());
//        
//        System.out.println("=== TERMES DANS DOC ===");
//        System.out.println(this.listeTermesDansDoc);
    }

    /**
     * Insère les documents/termes/noeuds issus de la collection traitée
     */
    private void insererDansBase() throws SQLException {
        /* Insertion des documents de la collection */
        int idDoc = 0;
        for (File f : files) {
            access.insertDocument(idDoc++, f.getName());
        }
        if (DEBUG) {
            System.out.println("Insertion des documents : OK\n");
        }
        
        /* Insertion dans la table termes */
        access.insertTerm(collectionTraitee.getListeTermes());
        if (DEBUG) {
            System.out.println("Insertion des termes : OK\n");
        }


        /* Insertion des noeuds */
        int parent;

        for (NoeudText noeud : this.listeNoeuds) {
            if (noeud != null) {
                if (DEBUG) {
                    System.out.print("DocID:" + noeud.getIdDoc() + " ");
                }
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

        
        /* Insertion des term_in_node */
        for (TermeDansNoeud terme : collectionTraitee.getListeTermesDansNoeud()) {
            access.insertTermInNode(
                    terme.getIdTerme(),
                    terme.getIdNoeud(),
                    terme.getFreq());
        }
        if (DEBUG) {
            System.out.println("Insertion des term_in_node : OK\n");
        }
        
        /* Insertion des term_pos */
        for (TermePosition terme : collectionTraitee.getListeTermesPosition()) {
            access.insertTermPos(
                    terme.getIdTerme(),
                    terme.getIdNoeud(),
                    terme.getPos());
        }
        if (DEBUG) {
            System.out.println("Insertion des term_pos : OK\n");
        }

    }
}
