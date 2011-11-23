
package moteurrecherche.ParserChaine;

import java.util.ArrayList;
import java.util.HashMap;

public class ChaineTraitee {
    ArrayList<TermePosition> listeTermes;
    HashMap<String, Integer> frequenceTerme;
    
    public ChaineTraitee(ArrayList<TermePosition> l, HashMap<String, Integer> f) {
        listeTermes     = l;
        frequenceTerme  = f;
    }

    public HashMap<String, Integer> getFrequenceTerme() {
        return frequenceTerme;
    }

    public ArrayList<TermePosition> getListeTermes() {
        return listeTermes;
    }
    
}
