package moteurrecherche.TestQrel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import moteurrecherche.Recherche.TraiterRequete;
import org.jdom.JDOMException;

public class EvaluerPerformance {

    private final static String[] REQUETE = {
        "balade au Mont Blanc",
        "balade montage amérique latine",
        "monuments Afrique",
        "lacs de France",
        "liste des monuments religieux visités",
        "village randonnée montagne asie",
        "GR10 rando pyrénées",
        "animaux pyrénées",
        "paysage montagne",
        "glaciers des alpes",
        "plongée sous marine"
    };
    private int maxParagraphes;

    public EvaluerPerformance(int maxParagraphes, boolean withReasoner) throws ClassNotFoundException, SQLException,
            JDOMException, IOException {

        this.maxParagraphes = maxParagraphes;
        ArrayList<Resultat> resultats;

        FileWriter writer = null;
        try {
            String title = "Résultats des tests avec précision à "+ maxParagraphes +"\n"
                    + "********************************************\n";

            String reasoner = "noReasoner";
            if(withReasoner) reasoner = "reasoner";

            String path  = "resultats_"+maxParagraphes+"_"+reasoner+".data";

            File file = new File(path);

            if(file.exists())
                file.delete();

            writer = new FileWriter(file, true);
            writer.write(title);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        for (int q = 1; q <= REQUETE.length; q++) {

            TraiterRequete requete = new TraiterRequete(REQUETE[q - 1], maxParagraphes, withReasoner);
            System.out.println("Mots traités : " + requete.getListeMotsRequete());
            resultats = requete.getListeResultats();

            System.out.println("Q:" + REQUETE[q - 1]);

            InputStream ips = EvaluerPerformance.class.getResourceAsStream(
                    "/resources/qrels/qrel" + q + ".txt");

            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader reader = new BufferedReader(ipsr);

            String ligne, filePath, path;
            int pertinence;
            StringTokenizer st;

            ArrayList<Resultat> resultatsQrel = new ArrayList<Resultat>();

            try {
                while ((ligne = reader.readLine()) != null) {

                    st = new StringTokenizer(ligne);

                    if (st.hasMoreTokens()) {
                        filePath = st.nextToken();
                    } else {
                        filePath = "NULL";
                    }

                    if (st.hasMoreTokens()) {
                        path = st.nextToken();
                    } else {
                        path = "NULL";
                    }

                    if (st.hasMoreTokens()) {
                        pertinence = Integer.valueOf(st.nextToken());
                    } else {
                        pertinence = -2;
                    }

                    if(pertinence == 1) {
                        resultatsQrel.add(new Resultat(filePath, path, 1));
                    }

                }
            } catch (IOException ex) {
                System.out.println("Lecture du fichier Qrel : ECHEC.\n" + ex.getMessage());
            }

            int count = 0;

            for (Resultat r : resultats) {
                for(Resultat rQrel : resultatsQrel) {
                    if (r.getFilePath().compareTo(rQrel.getFilePath()) == 0
                            && r.getPath().compareTo(rQrel.getPath()) == 0) {
                        r.setPertinence(1);
                        count++;
                    }
                }
            }
            
            int size;

            if((size = resultatsQrel.size()) > maxParagraphes) {
                size = maxParagraphes;
            }

            double percent = (((double) count) / ((double) size)) *100;

            String text = "Req" + q + " : " + count + "/" + size+"  ("+percent+"%)\n";
            System.out.print(text);

            writer.write(text);
        }

        if(writer != null) writer.close();
    }
}
