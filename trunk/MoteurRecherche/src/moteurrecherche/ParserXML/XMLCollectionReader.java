
package moteurrecherche.ParserXML;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import moteurrecherche.MoteurRecherche;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLCollectionReader {
    
    //Current ids of docs and Noeuds
    private Integer doc_id,
                    noeud_id;
    
    //Hashmap of elements in the document
    ArrayList<Noeud> noeuds;
    
    
    /**
     * Constructs the XML reader setting the Noeud_id and the doc_id as '0'
     */
    public XMLCollectionReader(){
        this.doc_id  = 0;
        this.noeud_id = 0;
        this.noeuds = new ArrayList<Noeud>();
    }
    
    
    /**
     * Constructs the XML reader with the specified doc_id and Noeud_id
     * @param doc_id the id of the doc
     * @param Noeud_id the id for the first Noeud of the XML fle
     */
    public XMLCollectionReader(Integer doc_id, Integer noeud_id){
        this.doc_id   = doc_id;
        this.noeud_id = noeud_id;
        noeuds = new ArrayList<Noeud>();
    }

    public Integer getNoeud_id() {
        return noeud_id;
    }

    public void setNoeud_id(Integer noeud_id) {
        this.noeud_id = noeud_id;
    }

    public Integer getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(Integer doc_id) {
        this.doc_id = doc_id;
    }
    
    /**
     * Reads a XML document representing a 'balade' and creates a hashmap with 
     * the Noeuds of the doc
     * @param f the address of the xml file
     * @return the hashmap representing the document
     */
    public ArrayList<Noeud> readDocument(File f) {
        //increment l'id du doc
        this.doc_id++;

        this.noeuds.clear();
        
        // Tries to read the XML file
        try {
            Integer root_id,
                    presentation_id,
                    recit_id,
                    complement_id;
            
            
            //Build sax
            SAXBuilder builder = new SAXBuilder(true);
            Document doc = builder.build(f.toString());
            
            //Get the root and other important parts of the xml file
            Element root            = doc.getRootElement(),
                    presentation    = root.getChild("PRESENTATION"),
                    recit           = root.getChild("RECIT"),
                    complements     = root.getChild("COMPLEMENTS");
            
            // Puts the root Noeud in the hashtable
            root_id = this.putNoeud(root.getName(), -1, null);
            
            // Puts the presentation Noeud in the hashtable
            presentation_id = this.putNoeud("PRESENTATION", root_id, null);
            // Reads the 'presentation'
            this.readPresentation(presentation, presentation_id);
         
            // Puts the recit Noeud in the hashtable
            recit_id = this.putNoeud("RECIT", root_id, null);
            // Reads the 'recit'
            this.readRecit(recit, recit_id);
            
            if(complements != null){
                // Puts the complements Noeud in the hashtable
                complement_id = this.putNoeud("COMPLEMENTS", root_id, null);
                // Reads the 'complement'
                this.readComplement(complements, recit_id);
            }
            
       
        } catch (JDOMException ex) {
            Logger.getLogger(MoteurRecherche.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MoteurRecherche.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return this.noeuds;
    }
    
    /**
     * Puts a Noeud in the hashmap and returns the id of the added Noeud
     * @param tag the string representing the tag
     * @param parent the id of the parent of the Noeud
     * @return the id of the added Noeud
     */
    private Integer putNoeud (String tag, Integer parent, String text){
        
        // Adds Noeud and increments the Noeud_id
        this.noeuds.add(new Noeud(this.noeud_id, this.doc_id, parent, tag, text));
        this.noeud_id++;

        return this.noeud_id -1;
    }
    
    
    private void readParagraph(Element paragraph, Integer parent_id){
        Element liste = paragraph.getChild("LISTE");
        if(liste == null){
            putNoeud("P", parent_id, paragraph.getText());
        }
        else{
            Integer p_id     = putNoeud("P", parent_id, null),
                    liste_id = putNoeud("LISTE", p_id, null);
                    
            //Iterator for each paragraph on DESCRIPTION
            Iterator<Element> listItr = liste.getChildren("ITEM").iterator();

            while (listItr.hasNext()) {
                putNoeud("ITEM", liste_id, listItr.next().getText());
            }
        }
        
    }
    
    private void readInfo(Element info, Integer parent_id){
        Element liste = info.getChild("LISTE");
        if(liste == null){
            putNoeud("INFO", parent_id, info.getText());
        }
        else{
            Integer p_id     = putNoeud("INFO", parent_id, null),
                    liste_id = putNoeud("LISTE", p_id, null);
                    
            //Iterator for each info
            Iterator<Element> listItr = liste.getChildren("ITEM").iterator();

            while (listItr.hasNext()) {
                putNoeud("ITEM", liste_id, listItr.next().getText());
            }
        }
        
    }
    
    /**
     * Reads the presentation, putting all the found Noeuds in the hashtable
     * @param presentation the presentation element
     * @param presentation_id the id of the presentation Noeud
     */
    private void readPresentation(Element presentation, Integer presentation_id){
        Element titre       = presentation.getChild("TITRE");
        Element auteur      = presentation.getChild("AUTEUR");
        Element date        = presentation.getChild("DATE");
        Element description = presentation.getChild("DESCRIPTION");
        Integer descr_id;
        
        // Puts the title Noeud (obligatoire)
        putNoeud("TITRE", presentation_id, presentation.getChildText("TITRE").toString());
        
        // Puts the author Noeud
        if(auteur != null)
            putNoeud("AUTEUR", presentation_id, presentation.getChildText("AUTEUR").toString());

        // Puts the author Noeud
        if(date != null)
            putNoeud("DATE", presentation_id, presentation.getChildText("DATE").toString());
        
        // Puts the description Noeud
        if(description != null){
            descr_id = putNoeud("DESCRIPTION", presentation_id, null);
        
            //Iterator for each paragraph on DESCRIPTION
            Iterator<Element> descItr = description.getChildren("P").iterator();

            while (descItr.hasNext()) {
                this.readParagraph(descItr.next(), descr_id);
            }
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
            Element subRecit = recitItr.next();
            
            if(subRecit.getName().compareToIgnoreCase("P") == 0){
                readParagraph(subRecit, recit_id);
            }
            else if(subRecit.getName().compareToIgnoreCase("PHOTO") == 0){
                putNoeud("PHOTO", recit_id, subRecit.getText());
            }
            if(subRecit.getName().compareToIgnoreCase("SEC") == 0){
                section_id = putNoeud("SEC", recit_id, null);
                putNoeud("SEC", recit_id, null);
                
                // Passing trough the section
                secItr = subRecit.getChildren().iterator();
                while (secItr.hasNext()) {
                    //Getting the next element
                    Element elm = secItr.next();

                    // If it's the subtitle
                    if(elm.getName().compareToIgnoreCase("SOUS-TITRE") == 0){
                        putNoeud("SOUS-TITRE", section_id, elm.getText());
                    }

                    //Else, it's a paragraph
                    else if(elm.getName().compareToIgnoreCase("P") == 0){
                        readParagraph(elm, section_id);
                    }
                    //Else, it's a paragraph
                    else if(elm.getName().compareToIgnoreCase("PHOTO") == 0){
                        putNoeud("PHOTO", section_id, elm.getText());
                    }
                }
            }
        }
    }
    
    private void readComplement(Element complements, Integer recit_id){
        //Iterators for each info
        Iterator<Element> complItr = complements.getChildren("INFO").iterator(),
                          infoItr;
        Integer info_id;
        
        // Passing trough all the infos
        while (complItr.hasNext()) {
            Element info = complItr.next();
            info_id = putNoeud("INFO", recit_id, null);
            
            // Passing trough the info
            infoItr = info.getChildren().iterator();
            while (infoItr.hasNext()) {
                //Getting the next element
                Element elm = infoItr.next();

                readInfo(elm, info_id);
            }
        }
    }
    
    
    
}
