
package moteurrecherche.TestQrel;

public class Resultat {
    private String filePath;
    private String path;
    private int pertinence;

    public Resultat(String f, String p) {
        filePath = f;
        path = p;
        pertinence = -1;
    }

    public Resultat(String f, String p, int pert) {
        filePath = f;
        path = p;
        pertinence = pert;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getPath() {
        return path;
    }

    public int getPertinence() {
        return pertinence;
    }

    public void setPertinence(int pertinence) {
        this.pertinence = pertinence;
    }

    @Override
    public String toString() {
        return filePath+" : "+path+" : "+pertinence+"\n";
    }

    
}
