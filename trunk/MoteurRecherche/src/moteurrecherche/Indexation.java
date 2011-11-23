package moteurrecherche;

import java.sql.SQLException;
import moteurrecherche.ParserXML.XMLCollectionReader;
import moteurrecherche.ParserXML.NoeudText;
import moteurrecherche.ParserChaine.TraitementCollection;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import moteurrecherche.Database.MySQLAccess;

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

        //Lit et traite tous les fichiers de la collection
        for (File f : files) {
            if (DEBUG)
                System.out.print(f.getName() + " : ");


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
        access.insertDocument(files);
        
        
        /* Insertion dans la table termes */
        access.insertTerm(collectionTraitee.getListeTermes());
        

        /* Insertion des noeuds */
        access.insertNode(listeNoeuds);

        
        /* Insertion des term_in_node */
        access.insertTermInNode(collectionTraitee.getListeTermesDansNoeud());
        
        /* Insertion des term_pos */
        access.insertTermPos(collectionTraitee.getListeTermesPosition());

    }
}
