package moteurrecherche.Ontologie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class ParserOntologie {

    private HashMap<String, Object> tableLabelInstance;
    private HashMap<Object, ArrayList<String>> tableInstanceLabel;

    public ParserOntologie() {
        /* Structure de données et variables */
        tableLabelInstance = new HashMap<String, Object>();
        tableInstanceLabel = new HashMap<Object, ArrayList<String>>();

        /*************************************/
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        //Charger le fichier
        File file = new File(ParserOntologie.class.getResource("/resources/balade.owl.xml").getPath());

        //Charger l'ontologie contenue dans ce fichier
        OWLOntology ontologie = null;
        try {
            ontologie = manager.loadOntologyFromOntologyDocument(file);
        } catch (OWLOntologyCreationException ex) {
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
                    tableLabelInstance.put(val.getLiteral(), instance);

                    //Ajout map <instance, liste_label>
                    if (tableInstanceLabel.get(instance) == null) {
                        ArrayList<String> labels = new ArrayList<String>();
                        labels.add(val.getLiteral());
                        tableInstanceLabel.put(instance, labels);
                    } else {
                        tableInstanceLabel.get(instance).add(val.getLiteral());
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
                    tableLabelInstance.put(val.getLiteral(), instance);

                    //Ajout map <instance, liste_label>
                    if (tableInstanceLabel.get(instance) == null) {
                        ArrayList<String> labels = new ArrayList<String>();
                        labels.add(val.getLiteral());
                        tableInstanceLabel.put(instance, labels);
                    } else {
                        tableInstanceLabel.get(instance).add(val.getLiteral());
                    }
                }
            }
        }

        //DEBUG only, afficher la hashmap
        //System.out.println("Table <Label, Instance> : \n"+tableLabelInstance);
        //System.out.println("Table <Instance, List<Labels>> : \n"+tableInstanceLabel);


        /*Entrée clavier */

        System.out.println("Veuillez entrer le nom d'une classe ou une instance : ");
        Scanner scanner = new Scanner(System.in);
        String user_search = scanner.nextLine();

        /* Recherche dans la hashmap */
        Object o = tableLabelInstance.get(user_search);

        /************************************************************************/
        /* Si la recherche de l'utilisateur renvoie un objet de type Class ******/
        /************************************************************************/
        if (o instanceof OWLClass) {

            // récupération des super classes (de niveau supérieur uniquement)
            System.out.println("Super classe : " + ((OWLClass) o).getSuperClasses(ontologie));

            //Récupération du premier niveau de sous-classes
            Set<OWLClassExpression> sousClasses = ((OWLClass) o).getSubClasses(ontologie);


            //Pour chaque sous-classe, aller recuperer les mots associes
            for (OWLClassExpression sousClasse : sousClasses) {
                System.out.println("Sous classe : " + sousClasse);

                ArrayList<String> labels = tableInstanceLabel.get(sousClasse);
                for(String lab : labels)
                    System.out.println("\tLabel: " + lab);
            }
            


        } /************************************************************************/
        /* Si la recherche de l'utilisateur renvoie un objet de type Individual */ /************************************************************************/
        else if (o instanceof OWLNamedIndividual) {

            System.out.println("Found Individual : " + o);

        } /************************************************************************/
        /* Cas d'erreur : la clef n'existe pas ******************************** */ /************************************************************************/
        else {
            System.out.println("Erreur : la clef entrée ne retourne aucune valeur.");
        }
    }
}
