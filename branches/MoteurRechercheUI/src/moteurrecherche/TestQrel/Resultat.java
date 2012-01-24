
package moteurrecherche.TestQrel;

public class Resultat {
    private String filePath;
    private String path;
    private int pertinence;
    private String paragraph;

    public Resultat(String f, String p) {
        filePath = f;
        path = p;
        pertinence = -1;
    }

    public Resultat(String f, String p, String paragraph) {
        filePath = f;
        path = p;
        this.paragraph = paragraph;
    }

    public Resultat(String f, String p, int pert) {
        filePath = f;
        path = p;
        pertinence = pert;
    }

    public Resultat(String f, String p, int pert, String paragraph) {
        filePath = f;
        path = p;
        pertinence = pert;
        this.paragraph = paragraph;
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

    public void setParagraph(String paragraph){
        this.paragraph = paragraph;
    }
    public String getParagraph(){
        return this.paragraph;
    }
    
}
