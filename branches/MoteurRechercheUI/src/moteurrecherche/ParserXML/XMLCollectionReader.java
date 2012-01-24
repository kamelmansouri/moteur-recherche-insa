
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
            root_id = this.putNoeud(root.getName(), -1, null, "/BALADE[1]");

            // Puts the presentation Noeud in the hashtable
            presentation_id = this.putNoeud("PRESENTATION", root_id, null, "/BALADE[1]/PRESENTATION[1]");
            // Reads the 'presentation'
            this.readPresentation(presentation, presentation_id, "/BALADE[1]/PRESENTATION[1]");

            // Puts the recit Noeud in the hashtable
            recit_id = this.putNoeud("RECIT", root_id, null, "/BALADE[1]/RECIT[1]");
            // Reads the 'recit'
            this.readRecit(recit, recit_id, "/BALADE[1]/RECIT[1]");

            if(complements != null){
                // Puts the complements Noeud in the hashtable
//                complement_id = this.putNoeud("COMPLEMENTS", root_id, null, "/BALADE[1]/COMPLEMENTS[1]");
                // Reads the 'complement'
                this.readComplement(complements, recit_id, "/BALADE[1]/COMPLEMENTS[1]");
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
    private Integer putNoeud (String tag, Integer parent, String text, String path){

        // Adds Noeud and increments the Noeud_id
        this.noeuds.add(new Noeud(this.noeud_id, this.doc_id, parent, tag, text, path));
        this.noeud_id++;

        return this.noeud_id -1;
    }


    private void readParagraph(Element paragraph, Integer parent_id, String path){
        Element liste = paragraph.getChild("LISTE");
        if(liste == null)
            putNoeud("P", parent_id, paragraph.getText(), path);

        else{
            Integer p_id = putNoeud("P", parent_id, null, path);
            readListe(liste, p_id, path.concat("/LISTE[1]"));
        }

    }

    private void readListe(Element liste, Integer parent_id, String path){
        Integer liste_id = putNoeud("LISTE", parent_id, null, path);

        //Iterator for each paragraph on DESCRIPTION
        Iterator<Element> listItr = liste.getChildren("ITEM").iterator();

        int index = 1;
        while (listItr.hasNext()){
            putNoeud("ITEM", liste_id, listItr.next().getText(), path.concat("/ITEM["+index+"]"));
            index++;
        }
    }

    private void readInfo(Element info, Integer parent_id, String path){
        Element liste = info.getChild("LISTE");
        if(liste == null)
            putNoeud("INFO", parent_id, info.getText(), path);

        else{
            Integer p_id     = putNoeud("INFO", parent_id, null, path),
                    liste_id = putNoeud("LISTE", p_id, null, path.concat("/LISTE[1]"));

            //Iterator for each info
            Iterator<Element> listItr = liste.getChildren("ITEM").iterator();

            path = path.concat("/LISTE[1]");
            int index = 1;
            while (listItr.hasNext()){
                putNoeud("ITEM", liste_id, listItr.next().getText(), path.concat("/ITEM["+index+"]"));
                index++;
            }

        }

    }

    /**
     * Reads the presentation, putting all the found Noeuds in the hashtable
     * @param presentation the presentation element
     * @param presentation_id the id of the presentation Noeud
     */
    private void readPresentation(Element presentation, Integer presentation_id, String path){
        Element titre       = presentation.getChild("TITRE");
        Element auteur      = presentation.getChild("AUTEUR");
        Element date        = presentation.getChild("DATE");
        Element description = presentation.getChild("DESCRIPTION");
        Integer descr_id;

        // Puts the title Noeud (obligatoire)
        putNoeud("TITRE", presentation_id, presentation.getChildText("TITRE").toString(), path.concat("/TITRE[1]"));

        // Puts the author Noeud
        if(auteur != null)
            putNoeud("AUTEUR", presentation_id, presentation.getChildText("AUTEUR").toString(), path.concat("/AUTEUR[1]"));

        // Puts the author Noeud
        if(date != null)
            putNoeud("DATE", presentation_id, presentation.getChildText("DATE").toString(), path.concat("/DATE[1]"));

        // Puts the description Noeud
        if(description != null){
            path =  path.concat("/DESCRIPTION[1]");
            descr_id = putNoeud("DESCRIPTION", presentation_id, null, path);

            //Iterator for each paragraph on DESCRIPTION
            Iterator<Element> descItr = description.getChildren("P").iterator();

            int index = 1;
            while (descItr.hasNext()) {
                this.readParagraph(descItr.next(), descr_id, path.concat("/P["+ index +"]"));
                index++;
            }

        }
    }


    /**
     * Reads the recit, putting all the found Noeuds in the hashtable
     * @param recit the recit element from de XML file
     * @param recit_id the recit Noeud id
     */
    private void readRecit(Element recit, Integer recit_id, String path){
        //Iterators for each recit and section
        Iterator<Element> recitItr = recit.getChildren().iterator(),
                          secItr;

        Integer section_id;

        int pIndex     = 1,
            photoIndex  = 1,
            secIndex    = 1;

        // Passing trough all the sections
        while (recitItr.hasNext()) {
            Element subRecit = recitItr.next();

            if(subRecit.getName().compareToIgnoreCase("P") == 0){
                readParagraph(subRecit, recit_id, path.concat("/P["+ pIndex +"]"));
                pIndex++;
            }

            else if(subRecit.getName().compareToIgnoreCase("PHOTO") == 0){
                putNoeud("PHOTO", recit_id, subRecit.getText(), path.concat("/PHOTO["+ photoIndex +"]"));
                photoIndex++;
            }

            if(subRecit.getName().compareToIgnoreCase("SEC") == 0){
                String pathSec = path.concat("/SEC["+ secIndex +"]");
                section_id = putNoeud("SEC", recit_id, null, pathSec);

                int pindex          = 1,
                    photoindex      = 1,
                    sousTitreIndex  = 1;

                // Passing trough the section
                secItr = subRecit.getChildren().iterator();
                while (secItr.hasNext()) {
                    //Getting the next element
                    Element elm = secItr.next();

                    //Else, it's a paragraph
                    if(elm.getName().compareToIgnoreCase("P") == 0){
                        readParagraph(elm, section_id, pathSec.concat("/P[" + pindex + "]"));
                        pindex++;
                    }

                    // If it's the subtitle or a photo
                    else if(elm.getName().compareToIgnoreCase("SOUS-TITRE") == 0){
                        putNoeud(elm.getName(), section_id, elm.getText(), pathSec.concat("/SOUS-TITRE[" + sousTitreIndex + "]"));
                        sousTitreIndex++;
                    }

                    else if(elm.getName().compareToIgnoreCase("PHOTO") == 0){
                        putNoeud(elm.getName(), section_id, elm.getText(), pathSec.concat("/PHOTO[" + photoindex + "]"));
                        photoindex++;
                    }
                }
                secIndex++;
            }
        }

    }

    private void readComplement(Element complements, Integer recit_id, String path){
        Element liste = complements.getChild("LISTE");
        if(liste == null)
            putNoeud("COMPLEMENTS", recit_id, complements.getText(), path);

        else{
            Integer cmpl_id = putNoeud("COMPLEMENTS", recit_id, null, path);
            readListe(liste, cmpl_id, path.concat("/LISTE[1]"));
        }

    }
}
