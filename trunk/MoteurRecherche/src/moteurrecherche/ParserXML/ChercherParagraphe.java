package moteurrecherche.ParserXML;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ChercherParagraphe {

    private final static String COLLECTION_PATH = "/resources/Collection/";

    public static void renvoyerParagraphe(String path, String doc_name) throws JDOMException, IOException {
        File f = new File(ChercherParagraphe.class.getResource(COLLECTION_PATH + doc_name).getPath());
        StringTokenizer st = new StringTokenizer(path, "/");

        //Build sax
        SAXBuilder builder = new SAXBuilder(true);
        Document doc = builder.build(f.toString());

        //Get the root and other important parts of the xml file
        Element root = doc.getRootElement(),
                element = null;
        Iterator<Element> itr = null;


        while(st.hasMoreTokens()) {
            String label = st.nextToken();

            System.out.println(label);

            if(label.compareTo("P") == 0) {
                int compteur=0;
                itr = element.getParent().getDescendants();
                int lvl = Integer.valueOf(st.nextToken());

                while(compteur < lvl-2) {
                    itr.next();
                    compteur++;
                }
                break;
            }

            element = root.getChild(label);
        }

        System.out.println(itr.next());

        
//                presentation = root.getChild("PRESENTATION"),
//                recit = root.getChild("RECIT"),
//                complements = root.getChild("COMPLEMENTS");
//
//        // Puts the root Noeud in the hashtable
//        root_id = this.putNoeud(root.getName(), -1, null);
//
//        // Puts the presentation Noeud in the hashtable
//        presentation_id = this.putNoeud("PRESENTATION", root_id, null);
//        // Reads the 'presentation'
//        this.readPresentation(presentation, presentation_id);
//
//        // Puts the recit Noeud in the hashtable
//        recit_id = this.putNoeud("RECIT", root_id, null);
//        // Reads the 'recit'
//        this.readRecit(recit, recit_id);
    }
}
