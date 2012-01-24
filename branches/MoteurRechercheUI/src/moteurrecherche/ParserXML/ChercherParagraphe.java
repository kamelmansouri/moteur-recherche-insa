package moteurrecherche.ParserXML;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import moteurrecherche.TestQrel.Resultat;
import org.jdom.JDOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ChercherParagraphe {

    private final static String COLLECTION_PATH = "/resources/Collection/";


    public static Resultat renvoyerParagraphe(String path, String doc_name) throws JDOMException, IOException {
        String file = ChercherParagraphe.class.getResource(COLLECTION_PATH + doc_name).getPath();
        String paragraph = null;
        try {
            Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression xPathExpression = xPath.compile(path);

            paragraph = xPathExpression.evaluate(xmlDocument);
//            System.out.println("file: " + file + "\n"
//                    + "doc: " + doc_name + "\n"
//                    + "path: " + path + "\n"
//                    + "content: " +  xPathExpression.evaluate(xmlDocument) );
            
        } catch (XPathExpressionException ex) {
            Logger.getLogger(ChercherParagraphe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ChercherParagraphe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ChercherParagraphe.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new Resultat("Collection/"+doc_name, path, paragraph);
    }
}
