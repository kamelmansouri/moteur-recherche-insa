
package moteurrecherche.ParserChaine;

import moteurrecherche.ParserChaine.Terme;
import java.util.ArrayList;
import java.util.HashMap;

public class ChaineTraitee {
    ArrayList<Terme> listeTermes;
    HashMap<String, Integer> frequenceTerme;
    
    public ChaineTraitee(ArrayList<Terme> l, HashMap<String, Integer> f) {
        listeTermes     = l;
        frequenceTerme  = f;
    }

    public HashMap<String, Integer> getFrequenceTerme() {
        return frequenceTerme;
    }

    public ArrayList<Terme> getListeTermes() {
        return listeTermes;
    }
    
}