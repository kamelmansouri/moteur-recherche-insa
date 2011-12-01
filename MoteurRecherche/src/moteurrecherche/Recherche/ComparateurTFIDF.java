
package moteurrecherche.Recherche;

import java.util.Comparator;

public class ComparateurTFIDF implements Comparator<ScoredTermInNode> {

    @Override
    public int compare(ScoredTermInNode s1, ScoredTermInNode s2) {
        double t1 = s1.getTfIdf();
        double t2 = s2.getTfIdf();

        if(t1 > t2)
            return -1;
        else if(t1 < t2)
            return 1;
        else
            return 0;
    }


}
