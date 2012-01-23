
package moteurrecherche.ParserXML;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLQueryReader {
    
    public static String[] readQueryFile(File f) {
        String[] requete = null;
        
        try {
           //Build sax
           SAXBuilder builder = new SAXBuilder(true);
           Document doc = builder.build(f.toString());

           //Get root element
           Element root = doc.getRootElement();
           
           //Get queries
           List<Element> queries = root.getChildren();
           
           // Instancie la liste de requêtes
           requete = new String[queries.size()];
           
           for(int i = 0; i < queries.size(); i++)
               requete[i] = queries.get(i).getChildText("text");           
           
        } catch (JDOMException ex) {
            Logger.getLogger(XMLQueryReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLQueryReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return requete;
    }
}
