
package moteurrecherche.ParserChaine;

import java.text.Normalizer;

public class TraitementMot {
    private final static int MAX_TRONCATURE = 7;
    
    private String mot;

    /**
     * Constructeur par défaut
     */
    public TraitementMot() {
    }
    
    /**
     * Construit un objet dont la méthode principale formaterMot
     * permet de tronquer et lemmatiser un mot.
     * @param mot le mot à traiter
     */
    public TraitementMot(String mot) {
        this.mot = mot;
    }
    
    public String getMot() {
        return mot;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }
    
    /**
     * Tronque puis lemmatise un mot donné
     */
    public void formaterMot() {
        tronquerMot();
        lemmatiserMot();
    }
    
    /**
     * Tronque un mot donné vers un mot de MAX_TRONCATURE caractères maximum.
     * @param mot le mot à tronquer
     * @return le mot tronqué
     */
    private void tronquerMot() {
        if(mot.length() > MAX_TRONCATURE)
            mot = mot.substring(0, MAX_TRONCATURE);
    }
    
    /**
     * Supprime le pluriel d'un mot. Supprime en réalité la lettre "s" à la fin
     * du mot donné.
     * @param mot le mot pouvant être pluriel
     * @return le mot au singulier
     */
    private void lemmatiserMot() {
        if(mot.substring(mot.length() - 1).compareTo("s") == 0)
            mot = mot.substring(0, mot.length() - 1);
    }

    /**
     * Remplace tous les accents dans une chaîne de caractères.
     * Met également le mot en minuscule.
     * @param mot le mot pouvant contenir des accents
     * @return le mot sans accents
     */
    public void remplacerAccents() {
        mot = Normalizer.normalize(mot, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
    }
}
