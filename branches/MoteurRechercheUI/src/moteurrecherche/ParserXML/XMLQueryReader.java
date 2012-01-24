
package moteurrecherche.ParserXML;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLQueryReader {
    
    public static String[] readQueryFile(File f) {
        String[] requete = null;
        
        try {
           File dtd = new File(XMLQueryReader.class.getResource("/resources/queries.dtd").getPath());

           //Build sax
           SAXBuilder builder = new SAXBuilder(false);
//           builder.setFeature(dtd.getAbsolutePath(), true);

            builder.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    if (systemId.contains("queries.dtd")) {
                        InputStream dtdStream = XMLQueryReader.class.getResourceAsStream("/resources/queries.dtd");
                        return new InputSource(dtdStream);
                    } else {
                        return null;
                    }
                }
            });

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
