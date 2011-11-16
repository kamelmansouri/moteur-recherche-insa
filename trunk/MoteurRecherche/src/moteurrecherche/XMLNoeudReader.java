/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moteurrecherche;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Class that provides methods to read a given XML file containing a "balade"
 * built based in a defined structure
 * @author banduk
 */
public class XMLNoeudReader {
    
    //Current ids of docs and Noeuds
    private Integer doc_id,
                    Noeud_id;
    
    //Hashmap of elements in the document
    HashMap <Integer, Noeud> Noeuds = new HashMap<Integer, Noeud>();
    
    
    /**
     * Constructs the XML reader setting the Noeud_id and the doc_id as '0'
     */
    public XMLNoeudReader(){
        this.doc_id  = 0;
        this.Noeud_id = 0;
    }
    
    
    /**
     * Constructs the XML reader with the specified doc_id and Noeud_id
     * @param doc_id the id of the doc
     * @param Noeud_id the id for the first Noeud of the XML fle
     */
    public XMLNoeudReader(Integer doc_id, Integer Noeud_id){
        this.doc_id  = doc_id;
        this.Noeud_id = Noeud_id;
    }
    

    /**
     * Reads a XML document representing a 'balade' and creates a hashmap with 
     * the Noeuds of the doc
     * @param file the name of the xml file
     * @return the hashmap representing the document
     */
    public HashMap<Integer, Noeud> read(String file) {        
        // Tries to read the XMLM file
        try {
            Integer root_id,
                    presentation_id,
                    recit_id;
            
            //Build sax
            SAXBuilder builder = new SAXBuilder(true);
            Document doc = builder.build(XMLNoeudReader.class.getResource(file));

            //Get the root and other important parts of the xml file
            Element root            = doc.getRootElement(),
                    presentation    = root.getChild("PRESENTATION"),
                    recit           = root.getChild("RECIT");
            
            // Puts the root Noeud in the hashtable
            root_id = this.putNoeud(root.getName(), -1);
            
            // Puts the presentation Noeud in the hashtable
            presentation_id = this.putNoeud("PRESENTATION", root_id);
            // Reads the 'presentation'
            this.readPresentation(presentation, presentation_id);
         
            // Puts the recit Noeud in the hashtable
            recit_id = this.putNoeud("RECIT", root_id);
            // Reads the 'recit'
            this.readRecit(recit, recit_id);
       
        } catch (JDOMException ex) {
            Logger.getLogger(MoteurRecherche.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MoteurRecherche.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Noeuds;
    }
    
    
    /**
     * Puts a Noeud in the hashmap and returns the id of the added Noeud
     * @param tag the string representing the tag
     * @param parent the id of the parent of the Noeud
     * @return the id of the added Noeud
     */
    private Integer putNoeud (String tag, Integer parent){
        // Adds Noeud ant increments the Noeud_id
        this.Noeuds.put(this.Noeud_id, new Noeud(this.doc_id, parent, tag));
        this.Noeud_id++;
        
        return this.Noeud_id -1;
    }
    
    
    /**
     * Reads the presentation, putting all the found Noeuds in the hashtable
     * @param presentation the presentation element
     * @param presentation_id the id of the presentation Noeud
     */
    private void readPresentation(Element presentation, Integer presentation_id){
        Element description = presentation.getChild("DESCRIPTION");
        Integer descr_id;
        
        // Puts the title Noeud
        putNoeud("TITRE", presentation_id);
//        presentation.getChildText("TITRE").toString()
        
        // Puts the author Noeud
        putNoeud("AUTEUR", presentation_id);
//      presentation.getChildText("AUTEUR").toString()

        
        // Puts the description Noeud
        descr_id = putNoeud("DESCRIPTION", presentation_id);
        
        //Iterator for each paragraph on DESCRIPTION
        Iterator<Element> descItr = description.getChildren().iterator();

        while (descItr.hasNext()) {
            putNoeud("P", descr_id);
//            descItr.next().getText()
            descItr.next(); //TODO - REMOVE when uncoment the abote code
        }
    }
    
    
    /**
     * Reads the recit, putting all the found Noeuds in the hashtable
     * @param recit the recit element from de XML file
     * @param recit_id the recit Noeud id
     */
    private void readRecit(Element recit, Integer recit_id){
        //Iterators for each recit and section
        Iterator<Element> recitItr = recit.getChildren().iterator(),
                          secItr;
        
        Integer section_id;
        
        // Passing trough all the sections
        while (recitItr.hasNext()) {
            section_id = putNoeud("SEC", recit_id);
            
            // Passing trough the section
            secItr = recitItr.next().getChildren().iterator();
            while (secItr.hasNext()) {
                //Getting the next element
                Element elm = secItr.next();

                // If it's the subtitle
                if(elm.getName().compareToIgnoreCase("SOUS-TITRE") == 0){
                    putNoeud("SOUS-TITRE", section_id);
//                    elm.getText();
                }

                //Else, it's a paragraph
                else{
                    putNoeud("P", section_id);
//                    elm.getText()
                }
            }
        }
    }
}
