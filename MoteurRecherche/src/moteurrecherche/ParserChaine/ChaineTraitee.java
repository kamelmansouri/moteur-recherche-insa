
package moteurrecherche.ParserChaine;

import java.util.ArrayList;
import java.util.HashMap;

public class ChaineTraitee {
    ArrayList<TermePosition> listeTermesPosition;
    HashMap<String, TermeDansNoeud> listeTermesDansNoeud;
    
    public ChaineTraitee(ArrayList<TermePosition> l, HashMap<String, TermeDansNoeud> f) {
        listeTermesPosition     = l;
        listeTermesDansNoeud   = f;
    }

    public HashMap<String, TermeDansNoeud> getListeTermesDansNoeud() {
        return listeTermesDansNoeud;
    }

    public ArrayList<TermePosition> getListeTermesPosition() {
        return listeTermesPosition;
    }
    
}
