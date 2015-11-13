package ontology;


import com.github.jsonldjava.utils.Obj;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDFS;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

public class OntologyCreator {

    private String namespace;
    private OntModel ontologyModel;

    private HashMap<String, DatatypeProperty> datatypeProperties;
    private HashMap<String, ObjectProperty> objectProperties;
    private HashMap<String, OntClass> ontologyClasses;

    private HashMap<String, Individual> actorsList;
    private HashMap<String, Individual> creatorsList;
    private HashMap<String, Individual> seriesList;

    // FIXME: MAJOR PROBLEM HERE: IN THE WAY I AM THINKING WE WILL NEED TO ASSURE THAT WE DO NOT HAVE ACTORS, CREATORS
    //        AND SERIES WITH THE SAME NAME

    public OntologyCreator(String filePath, String type) {
        // Read ontology from file
        createOntologyModel(filePath, type);

        // Get ontology namespace
        namespace = ontologyModel.getNsPrefixURI("");

        actorsList = new HashMap<>();
        creatorsList = new HashMap<>();
        seriesList = new HashMap<>();

        // Get Properties and classes
        loadProperties();
        loadClasses();
    }

    public void createOntologyModel() {
        ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    }

    public void createOntologyModel(String filePath, String type) {
        createOntologyModel();
        ontologyModel.read(filePath, type);
    }

    private void loadProperties() {
        datatypeProperties = new HashMap<>();
        objectProperties = new HashMap<>();

        List<DatatypeProperty> datatypePropertyList = ontologyModel.listDatatypeProperties().toList();
        List<ObjectProperty> objectPropertyList = ontologyModel.listObjectProperties().toList();

        for (DatatypeProperty datatypeProperty : datatypePropertyList) {
            datatypeProperties.put(datatypeProperty.getLocalName(), datatypeProperty);
        }

        for (ObjectProperty objectProperty : objectPropertyList) {
            objectProperties.put(objectProperty.getLocalName(), objectProperty);
        }
    }

    private void loadClasses() {
        ontologyClasses = new HashMap<>();
        List<OntClass> ontClassList = ontologyModel.listClasses().toList();

        /* The following code was suggested by Intellij instead of:

            for(OntClass currentClass : ontClassList) {
                if(currentClass.getLocalName() != null) {
                    ontologyClasses.put(currentClass.getLocalName(), currentClass);
                }
            }
         */
        ontClassList.stream().filter(currentClass -> currentClass.getLocalName() != null)
                             .forEach(currentClass -> ontologyClasses.put(currentClass.getLocalName(), currentClass));
    }

    public void createSeries(String title, String description, boolean hasFinished, int duration, int seasonNumber,
                             int pilotYear, ArrayList<String> genres) {
        String trimedName = title.replaceAll(" ", "_").toLowerCase();

        Individual newSeries = ontologyModel.createIndividual(namespace + trimedName,
                                                              ontologyModel.getOntClass("SeriesGenre"));

        // Make this series extend its genres
        for (String currentGenre : genres) {
            OntClass currentClass = ontologyClasses.get(currentGenre);
            newSeries.addProperty(RDFS.subClassOf, currentClass);
        }

        // Add series properties
        newSeries.addLiteral(datatypeProperties.get("hasTitle"), title);
        newSeries.addLiteral(datatypeProperties.get("hasDescription"), description);
        newSeries.addLiteral(datatypeProperties.get("hasFinished"), hasFinished);
        newSeries.addLiteral(datatypeProperties.get("hasEpisodeDuration"), duration);
        newSeries.addLiteral(datatypeProperties.get("hasSeasonNumber"), seasonNumber);
        newSeries.addLiteral(datatypeProperties.get("hasPilotYear"), pilotYear);

        // Add series to series list
        seriesList.put(title, newSeries);

        System.out.println("Created series " + newSeries.getLocalName());
    }

    public void createCreator(String creatorName, String biography, Calendar birthDate) {
        createPerson(false, creatorName, biography, birthDate);
    }

    public void createActor(String actorName, String biography, Calendar birthDate) {
        createPerson(true, actorName, biography, birthDate);
    }

    private void createPerson(boolean actor, String name, String biography, Calendar birthDate) {
        String trimedName = name.replaceAll(" ", "_").toLowerCase();
        Individual newIndividual;

        if (actor) {
            newIndividual = ontologyModel.createIndividual(namespace + trimedName,
                                                           ontologyModel.getOntClass("Actor"));
        } else {
            newIndividual = ontologyModel.createIndividual(namespace + trimedName,
                                                           ontologyModel.getOntClass("Creator"));
        }

        // Add actor properties
        newIndividual.addLiteral(datatypeProperties.get("hasActorName"), name);
        newIndividual.addLiteral(datatypeProperties.get("hasActorBiography"), biography);
        newIndividual.addLiteral(datatypeProperties.get("hasActorBirthDate"), birthDate);

        if (actor) {
            // Add actor to actor's list
            actorsList.put(name, newIndividual);
            System.out.println("Created actor " + newIndividual.getLocalName());
        } else {
            creatorsList.put(name, newIndividual);
            System.out.println("Created creator " + newIndividual.getLocalName());
        }
    }

    public boolean addSeriesToActor(String seriesName, String actorName) {
        // Get actor and series
        Individual series = seriesList.get(seriesName);
        Individual actor = actorsList.get(actorName);

        return addActorCreatorToSeries(true, series, actor);
    }

    public boolean addSeriesToCreator(String seriesName, String creatorName) {
        // Get creator and series
        Individual series = seriesList.get(seriesName);
        Individual creator = creatorsList.get(creatorName);

        return addActorCreatorToSeries(false, series, creator);
    }

    private boolean addActorCreatorToSeries(boolean addActor, Individual series, Individual actorOrCreator) {
        ObjectProperty property, inverseProperty;

        // Get property and its inverse
        if (addActor) {
            property = objectProperties.get("hasSeriesAppearance");
            inverseProperty = objectProperties.get("hasActor");
        } else {
            property = objectProperties.get("hasSeriesCreated");
            inverseProperty = objectProperties.get("hasCreator");
        }

        if (series == null || actorOrCreator == null || property == null || inverseProperty == null) {
            return false;
        }

        // Check if there is already a connection between the actor/creator and the series
        if (series.hasProperty(property)) {

            Statement statement = series.getProperty(property);
            RDFNode object = statement.getObject();

            // Since this is an ObjectProperty the object can only be of type resource, but we will play it safe
            if (object.isResource()) {
                Resource resource = object.asResource();

                // If the resources are the same
                if (resource.getLocalName().equals(actorOrCreator.getLocalName())) {
                    return false;
                }
            }
        }

        // Add the property and its inverse to both
        series.addProperty(property, actorOrCreator);
        actorOrCreator.addProperty(inverseProperty, series);

        return true;
    }

    public boolean writeModelToFile(String filePath, String type) {
        try {
            OutputStream out = new FileOutputStream(filePath);
            ontologyModel.write(out, type);

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getNamespace() {
        return namespace;
    }

    public void printStatements() {

        // print out the predicate, subject and object of each statement
        StmtIterator iter = ontologyModel.listStatements();
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();  // get next statement
            Resource subject   = stmt.getSubject();     // get the subject
            Property  predicate = stmt.getPredicate();   // get the predicate
            RDFNode   object    = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");
        }
    }

    public void printClasses() {
        Iterator<OntClass> it = ontologyModel.listClasses();
        OntClass class1 = it.next();

        while (it.hasNext()) {
            if (class1.getLocalName() != null)
                System.out.println(class1.getLocalName());
            class1 = it.next();
        }
    }
}
