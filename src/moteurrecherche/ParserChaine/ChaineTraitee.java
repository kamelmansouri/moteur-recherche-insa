
package moteurrecherche.ParserChaine;

import java.util.ArrayList;
import java.util.HashMap;

public class ChaineTraitee {
    private ArrayList<TermePosition> listeTermesPosition;
    private HashMap<String, TermeDansNoeud> listeTermesDansNoeud;
    private int nbMotsDansNoeud;
    
    public ChaineTraitee(ArrayList<TermePosition> l, 
            HashMap<String, TermeDansNoeud> f, int nbMotsDansNoeud) {
        listeTermesPosition     = l;
        listeTermesDansNoeud   = f;
        this.nbMotsDansNoeud = nbMotsDansNoeud;
         //System.out.println(this.nbMotsDansNoeud);
    }

    public HashMap<String, TermeDansNoeud> getListeTermesDansNoeud() {
        return listeTermesDansNoeud;
    }

    public int getNombreTermesDansNoeud() {
        return this.nbMotsDansNoeud;
    }

    public ArrayList<TermePosition> getListeTermesPosition() {
        return listeTermesPosition;
    }
    
}
