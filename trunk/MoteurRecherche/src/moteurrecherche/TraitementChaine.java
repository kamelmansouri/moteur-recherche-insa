package moteurrecherche;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;

public class TraitementChaine {

    /* Attributs donnés en param de constructeur */
    private TraitementCollection traitementCollec;
    private HashMap<String, Integer> listeTermesCollection;
    private ArrayList<Terme> listeTermesChaine;
    private ArrayList<String> stopListe;
    private String chaine;
    private int idNoeud;
    private int position;   /* Position du mot dans la chaîne */

    /**
     * Construit un objet dont la méthode principale traiterChaine
     * permet de mettre à jour la liste des termes dans la collection d'une part
     * et de construire une liste de termes relative à la chaîne traitée
     * d'autre part.
     * @param tc l'objet qui traite la collection
     * @param str la chaîne à traiter
     * @param id_noeud le noeud dont la chaîne est issue
     */
    public TraitementChaine(TraitementCollection tc,
            String str, int id_noeud) {
        this.chaine = str;
        this.idNoeud = id_noeud;
        this.listeTermesCollection = tc.getListeTermes();
        this.listeTermesChaine = new ArrayList<Terme>();
        this.stopListe = tc.getStopListe();
        this.traitementCollec = tc;
        this.position = 1;
    }

    /**
     * Divise puis formate chaque mot d'une chaîne de caractères donnée.
     * @param str la chaîne de caractères à traiter
     */
    public void traiterChaine() {
        /* Permet d'avoir un accès direct aux indices de l'arraylist */
        HashMap<String, Integer> positionTermeDansListe = new HashMap<String, Integer>();

        /* Mettre la chaine en minuscule et remplacer les accents */
        chaine = chaine.toLowerCase();
        remplacerAccents();

        /* Separer les mots de la chaîne */
        String delimiteur = "[^a-z]"; //On ne garde que les lettres
        String[] listeMots = chaine.split(delimiteur);

        /* Appliquer le formatage pour chaque mot */
        for (String mot : listeMots) {
            /* On ignore les mots de 1 lettre */
            if (mot.length() > 1) {

                /* Formatage du mot */
                TraitementMot traitementMot = new TraitementMot(mot);
                traitementMot.formaterMot();
                mot = traitementMot.getMot();

                /* Ajout dans la liste si le mot fait au moins 2 lettres */
                if (mot.length() > 1) {
                    /* Si le mot n'est pas dans la stop liste */
                    if (!existeDansStopListe(mot)) {

                        /* Mise à jour si déjà présent dans la liste collection */
                        if (existeDansListeTermesCollection(mot)) {
                            listeTermesCollection.put(mot,
                                    listeTermesCollection.get(mot)+1);
                        } /* Ajout si nouveau mot */
                        else {
                            listeTermesCollection.put(mot, 1);
                        }

                        //Incrémenter le nb d'occurrences si existe déjà
                        //if(listeTermesChaine.contains(mot) != null)
                            //listeTermesChaine.get(mot).incrementerOccurrence();

                        /* Ajout dans la liste de termes de cette chaîne */
                        listeTermesChaine.add(new Terme(mot, idNoeud, position++));
                        
                    }
                }
            }

        }
        /* Mise à jour effective de la liste de termes */
        traitementCollec.setListeTermes(listeTermesCollection);
    }

    /**
     * @return La liste de termes de la chaîne traitée
     */
    public ArrayList<Terme> getListeTermesChaine() {
        return listeTermesChaine;
    }

    /**
     * Remplace tous les accents dans une chaîne de caractères.
     * Met également le mot en minuscule.
     * @param mot le mot pouvant contenir des accents
     * @return le mot sans accents
     */
    private void remplacerAccents() {
        chaine = Normalizer.normalize(chaine, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
    }

    /**
     * Vérifie l'existence d'un mot dans la stop liste.
     * @param mot le mot à vérifier
     * @return true si le mot existe, false sinon.
     */
    private boolean existeDansStopListe(String mot) {
        if (stopListe.contains(mot)) {
            return true;
        }

        return false;
    }

    /**
     * Vérifie l'existence d'un mot dans la liste de termes de la collection.
     * @param mot le mot à vérifier
     * @return true si le mot existe, false sinon.
     */
    private boolean existeDansListeTermesCollection(String mot) {
        if (listeTermesCollection.containsKey(mot)) {
            return true;
        }

        return false;
    }
}
