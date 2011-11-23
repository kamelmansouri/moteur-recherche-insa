
package moteurrecherche.ParserChaine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class TraitementCollection {
    private final static String STOP_LISTE_PATH = "/resources/stopliste.txt";

    private HashMap<String, TermeCollection> listeTermes; //<mot, <id, nbOccurrence>>
    //private HashMap<Integer, String> listeTermesById; //<id, mot>
    private ArrayList<TermeDansNoeud> listeTermesDansNoeud;
    private ArrayList<TermePosition> listeTermesPosition;
    private ArrayList<String> stopListe;
    private int indexIdTerme; //permet de donner un id à un terme
    
    public TraitementCollection() {
        listeTermes = new HashMap<String, TermeCollection>();
        listeTermesPosition = new ArrayList<TermePosition>();
        listeTermesDansNoeud = new ArrayList<TermeDansNoeud>();
        stopListe   = new ArrayList<String>();
        chargerStopListe();
    }

    public ArrayList<TermeDansNoeud> getListeTermesDansNoeud() {
        return listeTermesDansNoeud;
    }

    public ArrayList<TermePosition> getListeTermesPosition() {
        return listeTermesPosition;
    }

    
    public int getIndexIdTerme() {
        return indexIdTerme;
    }
    
    public void incrementerIdTerme() {
        indexIdTerme++;
    }

    public HashMap<String, TermeCollection> getListeTermes() {
        return listeTermes;
    }

    public void setListeTermes(HashMap<String, TermeCollection> listeTermes) {
        this.listeTermes = listeTermes;
    }
    

    public ArrayList<String> getStopListe() {
        return stopListe;
    }
    
    /**
     * Traite la chaine d'un paragraphe afin d'ajouter les termes
     * formatés issus de celle-ci dans la liste de termes.
     * @param str la chaîne à traiter
     * @param idNoeud le noeud dans lequel la chaîne se trouve
     * @return Un objet ChaineTraitee @see ChaineTraitee
     */
    public ChaineTraitee traiterChaine(String str, int idNoeud) {
        TraitementChaine p = new TraitementChaine(this, str, idNoeud);
        p.traiterChaine();

        return new ChaineTraitee(p.getListeTermesChaine(), p.getTermesDansNoeudCourant());
    }
    
    /**
     * Lit le fichier stopliste.txt et charge les mots formatés (tronqués,
     * lemmatisés, suppression des doublons) dans l'ArrayList stopListe;
     */
    private void chargerStopListe() {
        InputStream ips = TraitementChaine.class.getResourceAsStream(STOP_LISTE_PATH);

        InputStreamReader ipsr = new InputStreamReader(ips);
        BufferedReader reader = new BufferedReader(ipsr);

        String ligne, mot;

        try {
            while ((ligne = reader.readLine()) != null) {
                TraitementMot traitementMot = new TraitementMot(ligne);
                traitementMot.formaterMot();
                traitementMot.remplacerAccents();
                mot = traitementMot.getMot();

                //Ajout du mot s'il n'existe pas déjà dans la liste
                if (!stopListe.contains(mot)) {
                    stopListe.add(mot);
                    //System.out.println("Mot stoplist: "+mot);
                }
            }
        } catch (IOException ex) {
            System.out.println("Lecture de la stop liste : ECHEC.\n" + ex.getMessage());
        }
    }
}
