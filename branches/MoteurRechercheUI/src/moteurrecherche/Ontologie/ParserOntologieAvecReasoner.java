package moteurrecherche.Ontologie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import moteurrecherche.ParserChaine.TraitementMot;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class ParserOntologieAvecReasoner {

    private HashMap<String, Object> tableLabelInstance;
    private HashMap<Object, ArrayList<String>> tableInstanceLabel;
    private OWLOntology ontologie = null;
    private Reasoner hermit;
    private OWLAnnotationProperty label;

    public ParserOntologieAvecReasoner() {
        this.readOntology();
        this.initReasoner();

        this.label = hermit.getDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        
        //Si l'ontologie est consistente
        if (hermit.isConsistent()) {
            this.fillHashMaps();
        } else {
            System.out.println("Cette ontologie n'est pas consistente!!");
            System.exit(0);
        }
    }
    
    private void fillHashMaps(){
        for (OWLClass classe : ontologie.getClassesInSignature()) {
            // Met le classe dans les hashmaps
            fillWithClasses(classe);
            // Met les individues de cette classe dans les hashMaps
            fillWithIndividuals(classe);
        }
    }
    
    private void fillWithClasses( OWLClass classe){
        for (OWLAnnotation annotation : classe.getAnnotations(ontologie, label)) {
            if (annotation.getValue() instanceof OWLLiteral) {
                OWLLiteral val = (OWLLiteral) annotation.getValue();

                //Ajout map <label, instance>
                tableLabelInstance.put(formaterMot(val.getLiteral()), classe);

                //Ajout map <instance, liste_label>
                if (tableInstanceLabel.get(classe) == null) {
                    ArrayList<String> labels = new ArrayList<String>();
                    labels.add(formaterMot(val.getLiteral()));
                    tableInstanceLabel.put(classe, labels);
                } else {
                    tableInstanceLabel.get(classe).add(formaterMot(val.getLiteral()));
                }
            }
        }
    }
    
     private void fillWithIndividuals( OWLClass classe ){
        Set<OWLNamedIndividual> individuals = hermit.getInstances(classe, true).getFlattened();
        for(OWLNamedIndividual individual : individuals){
            for (OWLAnnotation annotationInd : individual.getAnnotations(ontologie, label)) {
                if (annotationInd.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotationInd.getValue();

                    //Ajout map <label, instance>
                    tableLabelInstance.put(formaterMot(val.getLiteral()), individual);

                    //Ajout map <instance, liste_label>
                    if (tableInstanceLabel.get(individual) == null) {
                        ArrayList<String> labels = new ArrayList<String>();
                        labels.add(formaterMot(val.getLiteral()));
                        tableInstanceLabel.put(individual, labels);
                    } else {
                        tableInstanceLabel.get(individual).add(formaterMot(val.getLiteral()));
                    }
                }
            }
        }
    }
    
    private void readOntology(){
         OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        
        // Structure de données et variables
        tableLabelInstance = new HashMap<String, Object>();
        tableInstanceLabel = new HashMap<Object, ArrayList<String>>();
        
        //Charger le fichier
        File file = new File(ParserOntologieAvecReasoner.class.getResource("/resources/balade.owl.xml").getPath());

        //Charger l'ontologie contenue dans ce fichier
        try {
            ontologie = manager.loadOntologyFromOntologyDocument(file);
        } catch (OWLOntologyCreationException ex) {
            System.out.println("Error while loading ontology");
        }

        System.out.println("Ontologie chargée: " + ontologie);
    }
    
    private void initReasoner(){
        OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
        hermit = (Reasoner) reasonerFactory.createReasoner(ontologie, config);
        hermit.precomputeInferences();
        
    }
    
    private String formaterMot(String mot) {
        //ToDo: prendre en compte les requêtes avec guillemets

        // Mettre la chaine en minuscule et remplacer les accents
        mot = mot.toLowerCase();

        TraitementMot requeteTraitee = new TraitementMot(mot);
        requeteTraitee.remplacerAccents();

        // Separer les mots de la chaîne
        String delimiteur = "[^a-z0-9]"; //On ne garde que les lettres et chiffres

        return mot.replaceAll(delimiteur, " ");
    }
    
    public ArrayList<String> getMotsAAjouter(String motCherche){
        ArrayList<String> motsAAjouter = new ArrayList<String>();

        //Recherche dans la hashmap 
        Object o = tableLabelInstance.get(motCherche);

        // Si la recherche de l'utilisateur renvoie un objet de type Class
        if (o instanceof OWLClass) {
            
            //Recuperation des synonims
            motsAAjouter.addAll(tableInstanceLabel.get(o));

            //Récupération des instances
            Set<OWLNamedIndividual> instances = hermit.getInstances((OWLClass) o, true).getFlattened();
            for(OWLNamedIndividual instance : instances){
                for (OWLAnnotation annotationInd : instance.getAnnotations(ontologie, label)) {
                    if (annotationInd.getValue() instanceof OWLLiteral) {
                        OWLLiteral val = (OWLLiteral) annotationInd.getValue();
                        motsAAjouter.add(this.formaterMot(val.getLiteral()));
                    }
                }
            }
            
            //Récupération du premier niveau de sous-classes
            Set<OWLClass> sousClasses = hermit.getSubClasses((OWLClass) o, true).getFlattened();

            //Pour chaque sous-classe, aller recuperer les mots associes
            for (OWLClassExpression sousClasse : sousClasses) {

                ArrayList<String> labels = tableInstanceLabel.get(sousClasse);
                
                if(labels != null)
                    //Ajout les mots des sous-classes dans la liste
                    motsAAjouter.addAll(labels);
            }
            
        } 
        
        // Si la recherche de l'utilisateur renvoie un objet de type Individual
        else if (o instanceof OWLNamedIndividual) {
            //Recuperation des synonims
            motsAAjouter.addAll(tableInstanceLabel.get(o));
            
            System.out.println("Found Individual : " + o);

        }
        
        // Cas d'erreur : la clef n'existe pas
        else {
            //System.out.println("Erreur : la clef entrée ne retourne aucune valeur.");
        }

        return motsAAjouter;
    }    
}