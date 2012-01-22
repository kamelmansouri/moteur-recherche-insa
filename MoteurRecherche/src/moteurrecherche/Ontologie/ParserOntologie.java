package moteurrecherche.Ontologie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import moteurrecherche.ParserChaine.TraitementMot;
import moteurrecherche.Recherche.TraiterRequete;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class ParserOntologie {

    private HashMap<String, Object> tableLabelInstance;
    private HashMap<Object, ArrayList<String>> tableInstanceLabel;
    private OWLOntology ontologie = null;

    public ParserOntologie() {
        /* Structure de données et variables */
        tableLabelInstance = new HashMap<String, Object>();
        tableInstanceLabel = new HashMap<Object, ArrayList<String>>();

        /*************************************/
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        //Charger le fichier
        File file = new File(ParserOntologie.class.getResource("/resources/balade.owl.xml").getPath());

        //Charger l'ontologie contenue dans ce fichier
        try {
            ontologie = manager.loadOntologyFromOntologyDocument(file);
        } catch (OWLOntologyCreationException ex) {
            System.out.println("Error while loading ontologie");
        }
        System.out.println("Ontologie chargée: " + ontologie);

        //Permet de parcourir les ontologies
        OWLDataFactory df = manager.getOWLDataFactory();

        OWLAnnotationProperty label = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());


        /**********************************************************/
        /* Ajouter les couples (label, Class Object) dans la map  */
        /**********************************************************/
        for (OWLClass instance : ontologie.getClassesInSignature()) {

            // Get the annotations on the class that use the label property
            for (OWLAnnotation annotation : instance.getAnnotations(ontologie, label)) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();

                    //Ajout map <label, instance>
                    tableLabelInstance.put(formaterMot(val.getLiteral()), instance);

                    //Ajout map <instance, liste_label>
                    if (tableInstanceLabel.get(instance) == null) {
                        ArrayList<String> labels = new ArrayList<String>();
                        labels.add(formaterMot(val.getLiteral()));
                        tableInstanceLabel.put(instance, labels);
                    } else {
                        tableInstanceLabel.get(instance).add(formaterMot(val.getLiteral()));
                    }

                }
            }
        }


        /**********************************************************/
        /*    Ajouter les couples (label, individus) dans la map  */
        /**********************************************************/
        for (OWLNamedIndividual instance : ontologie.getIndividualsInSignature()) {
            for (OWLAnnotation annotation : instance.getAnnotations(ontologie, label)) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();

                    //Ajout map <label, instance>
                    tableLabelInstance.put(formaterMot(val.getLiteral()), instance);

                    //Ajout map <instance, liste_label>
                    if (tableInstanceLabel.get(instance) == null) {
                        ArrayList<String> labels = new ArrayList<String>();
                        labels.add(formaterMot(val.getLiteral()));
                        tableInstanceLabel.put(instance, labels);
                    } else {
                        tableInstanceLabel.get(instance).add(formaterMot(val.getLiteral()));
                    }
                }
            }
        }

        //DEBUG only, afficher la hashmap
//        System.out.println("Table <Label, Instance> : \n"+tableLabelInstance);
//        System.out.println("Table <Instance, List<Labels>> : \n"+tableInstanceLabel);
    }


    public ArrayList<String> getMotsAAjouter(String motCherche){
        ArrayList<String> motsAAjouter = new ArrayList<String>();

        /* Recherche dans la hashmap */
        Object o = tableLabelInstance.get(motCherche);

        /************************************************************************/
        /* Si la recherche de l'utilisateur renvoie un objet de type Class ******/
        /************************************************************************/
        if (o instanceof OWLClass) {
            //Recuperation des synonims
            motsAAjouter.addAll(tableInstanceLabel.get(o));

            // récupération des super classes (de niveau supérieur uniquement)
//            System.out.println("Super classe : " + ((OWLClass) o).getSuperClasses(ontologie));

            //Récupération du premier niveau de sous-classes
            Set<OWLClassExpression> sousClasses = ((OWLClass) o).getSubClasses(ontologie);


            //Pour chaque sous-classe, aller recuperer les mots associes
            for (OWLClassExpression sousClasse : sousClasses) {
                System.out.println("Sous classe : " + sousClasse);

                ArrayList<String> labels = tableInstanceLabel.get(sousClasse);

                for(String lab : labels)
                    System.out.println("\tLabel: " + lab);

                //Ajout les mots des sous-classes dans la liste
                motsAAjouter.addAll(labels);
            }
            


        } /************************************************************************/
        /* Si la recherche de l'utilisateur renvoie un objet de type Individual */
        /************************************************************************/
        else if (o instanceof OWLNamedIndividual) {
            //Recuperation des synonims
            motsAAjouter.addAll(tableInstanceLabel.get(o));
            
            System.out.println("Found Individual : " + o);

        } /************************************************************************/
        /* Cas d'erreur : la clef n'existe pas ******************************** */ /************************************************************************/
        else {
            System.out.println("Erreur : la clef entrée ne retourne aucune valeur.");
        }

        return motsAAjouter;
    }

    private String formaterMot(String mot) {
        /* ToDo: prendre en compte les requêtes avec guillemets */

        /* Mettre la chaine en minuscule et remplacer les accents */
        mot = mot.toLowerCase();

        TraitementMot requeteTraitee = new TraitementMot(mot);
        requeteTraitee.remplacerAccents();

        /* Separer les mots de la chaîne */
        String delimiteur = "[^a-z0-9]"; //On ne garde que les lettres et chiffres

        return mot.replaceAll(delimiteur, " ");
    }
}
