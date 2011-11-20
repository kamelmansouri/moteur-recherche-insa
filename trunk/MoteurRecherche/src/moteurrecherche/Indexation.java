
package moteurrecherche;

import moteurrecherche.ParserXML.XMLCollectionReader;
import moteurrecherche.ParserXML.NoeudText;
import moteurrecherche.ParserChaine.TraitementCollection;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class Indexation {
    private final static String COLLECTION_PATH = "/resources/Collection/";
    private final static boolean DEBUG = true;
    
    public Indexation(){}
    
    public void indexer(){        
        //Pour lire toutes les documents de la collection
        XMLCollectionReader collectionReader = new XMLCollectionReader();
        //Pour traiter la collection
        TraitementCollection traiteCollection = new TraitementCollection();
        //Le hashmap qui represent un document
        ArrayList<NoeudText> noeudList;
        
        // Filtre pour ne traiter que les fichier xml, ce filtre est utilisé quand
        // on lit les fichiers xml dans un dossier specifique
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };
        
        //Prend tous les fichiers xml dans le dossier specifié
        File directory = new File(Indexation.class.getResource(COLLECTION_PATH).getPath()),
             files[] = directory.listFiles(filter);

        //Lit et traite tous les fichiers de la collection
        for (File f : files) {
            
                if(DEBUG) System.out.print(f.getName() + " : ");
                
                noeudList = collectionReader.readDocument(f);
                for( NoeudText node : noeudList ){
                    if(node.getText() != null){
                        traiteCollection.traiterChaine(node.getText(), node.getId());
                    }
                }
                
                if(DEBUG) System.out.print("OK\n");
        }
        
        if(DEBUG) {
            System.out.println("\n"+traiteCollection.getListeTermes().size() +
                    " mots indexés\n");
            System.out.println(traiteCollection.getListeTermes());
        }
        
    }
    
    
}
