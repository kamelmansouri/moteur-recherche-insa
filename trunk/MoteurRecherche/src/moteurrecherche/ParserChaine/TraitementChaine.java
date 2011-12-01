package moteurrecherche.ParserChaine;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;

public class TraitementChaine {

    /* Attributs donnés en param de constructeur */
    private TraitementCollection collection;
    private HashMap<String, TermeCollection> listeTermesCollection;
    private ArrayList<TermePosition> listeTermesChaine;
    /* Permet d'avoir un accès direct aux indices de l'arraylist */
    HashMap<String, TermeDansNoeud> termesDansNoeudCourant;
    private ArrayList<String> stopListe;
    private String chaine;
    private int idNoeud;
    private int position;   /* Position du mot dans la chaîne */
    private int compteurMotsDansNoeud;


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
        this.listeTermesChaine = new ArrayList<TermePosition>();
        this.termesDansNoeudCourant = new HashMap<String, TermeDansNoeud>();
        this.stopListe = tc.getStopListe();
        this.collection = tc;
        this.position = 1;
        this.compteurMotsDansNoeud = 0;

        //System.out.println("long a traiter : "+this.chaine.length());
    }

    /**
     * Divise puis formate chaque mot d'une chaîne de caractères donnée.
     * @param str la chaîne de caractères à traiter
     */
    public void traiterChaine() {

        int indexIdTerme, idTermeCourant;
        TermeDansNoeud termeDansNoeud;

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
                        compteurMotsDansNoeud++;

                        indexIdTerme = collection.getIndexIdTerme();
                        
                        /* Mise à jour frequence si déjà présent dans liste collection */
                        if (existeDansListeTermesCollection(mot)) {
                            listeTermesCollection.get(mot).incrementerFrequence();
                            idTermeCourant = listeTermesCollection.get(mot).getIdTerme();
                        } /* Ajout si nouveau mot */ else {
                            listeTermesCollection.put(mot, new TermeCollection(indexIdTerme));

                            collection.incrementerIdTerme();
                            idTermeCourant = collection.getIndexIdTerme();
                        }                      


                        /* Ajout dans la liste de termes avec position */
                        collection.getListeTermesPosition().add(
                                new TermePosition(
                                idTermeCourant,
                                idNoeud,
                                position));

                        //Incrémenter le nb d'occurrences si existe déjà, sinon créer
                        if ((termeDansNoeud = termesDansNoeudCourant.get(mot)) == null) {
                            termesDansNoeudCourant.put(mot, new TermeDansNoeud(
                                    idTermeCourant,
                                    idNoeud));
                        } else {
                            termeDansNoeud.incrementerFreq();
                        }

                        position++;


                    }
                }
            }

        }

        /* Mise à jour effective de la liste de termes */
        collection.getListeTermesDansNoeud().addAll(termesDansNoeudCourant.values());
        collection.setListeTermes(listeTermesCollection);
    }

    public int getCompteurMotsDansNoeud() {
        return compteurMotsDansNoeud;
    }


    /**
     * @return La liste de termes de la chaîne traitée
     */
    public ArrayList<TermePosition> getListeTermesChaine() {
        return listeTermesChaine;
    }

    /**
     * 
     * @return La liste des termes dans le noeud courant, avec leur fréquence
     */
    public HashMap<String, TermeDansNoeud> getTermesDansNoeudCourant() {
        return termesDansNoeudCourant;
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
