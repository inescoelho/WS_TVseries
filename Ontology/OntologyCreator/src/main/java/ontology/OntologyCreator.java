package ontology;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDFS;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * OntologyCreator class, responsible for managing an ontology, making use of the Apache Jena framework. For more
 * information please see https://jena.apache.org/index.html
 *
 * The class has the following attributes:
 *
 *  - namespace     --> The namespace of the ontology
 *  - ontologyModel --> Apache Jena's representation of an ontology model. In our case it contains the representation
 *                      of an ontology model loaded from a given file
 *  - actorsList    --> A map of type "<String, Individual>", representing all the actors in the ontology
 *  - creatorsList  --> A map of type "<String, Individual>", representing all the series creators in the ontology
 *  - seriesList    --> A map of type "<String, Individual>", representing all the series in the ontology
 *
 */
public class OntologyCreator {

    private String namespace;
    private OntModel ontologyModel;

    private HashMap<String, Individual> actorsList;
    private HashMap<String, Individual> creatorsList;
    private HashMap<String, Individual> seriesList;

    // FIXME: MAJOR PROBLEM HERE: IN THE WAY I AM THINKING WE WILL NEED TO ASSURE THAT WE DO NOT HAVE ACTORS, CREATORS
    //        AND SERIES WITH THE SAME NAME

    /**
     * Simple class constructor, which loads the ontology schema from a file
     * @param filePath The path to the file containing the ontology schema
     * @param type The format of the file (RDF, OWL, Turtle...)
     */
    public OntologyCreator(String filePath, String type) {
        // Read ontology from file
        createOntologyModel(filePath, type);

        // Get ontology namespace
        namespace = ontologyModel.getNsPrefixURI("");

        actorsList = new HashMap<>();
        creatorsList = new HashMap<>();
        seriesList = new HashMap<>();
    }

    /**
     * Creates a default ontology model
     */
    public void createOntologyModel() {
        ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    }

    /**
     * Creates an ontology model with the schema stored in a given file
     * @param filePath The path to the file containing the ontology schema
     * @param type The format of the file (RDF, OWL, Turtle...)
     */
    public void createOntologyModel(String filePath, String type) {
        createOntologyModel();
        ontologyModel.read(filePath, type);
    }

    /**
     * Creates a new tv series and adds it to the ontology
     * @param title The tile of the series
     * @param description A brief description of the series
     * @param hasFinished A boolean value, signalling if the series has already finished
     * @param duration The average duration, in minutes, of each episode of the series
     * @param seasonNumber The current series' season number (-1 if series has not started)
     * @param pilotYear The year when the series' pilot episode aired (-1 if no pilot has aired)
     * @param genres The genres of the series
     */
    public void createSeries(String title, String description, boolean hasFinished, int duration, int seasonNumber,
                             int pilotYear, ArrayList<String> genres) {
        String trimedName = title.replaceAll(" ", "_").toLowerCase();

        Individual newSeries = ontologyModel.createIndividual(namespace + trimedName,
                                                              ontologyModel.getOntClass(namespace + "SeriesGenre"));

        // Make this series extend its genres
        for (String currentGenre : genres) {
            OntClass currentClass = ontologyModel.getOntClass(namespace + currentGenre);
            newSeries.addProperty(RDFS.subClassOf, currentClass);
        }

        // Add series properties
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasTitle"), title);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasDescription"), description);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasFinished"), hasFinished);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasEpisodeDuration"), duration);

        if (seasonNumber >= 0) {
            newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasSeasonNumber"), seasonNumber);
        }

        if (pilotYear >= 0) {
            newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasPilotYear"), pilotYear);
        }

        // Add series to series list
        seriesList.put(title, newSeries);

        System.out.println("Created series " + newSeries.getLocalName());
    }

    /**
     * Creates a new tv series creator and adds it to the ontology
     * @param creatorName The name of the creator
     * @param biography A small biography of the creator
     * @param birthDate The creator's date of birth
     */
    public void createCreator(String creatorName, String biography, String birthDate) {
        createPerson(false, creatorName, biography, birthDate);
    }

    /**
     * Creates a new actor and adds it to the ontology
     * @param actorName The name of the actor
     * @param biography A small biography of the actor
     * @param birthDate The actor's date of birth
     */
    public void createActor(String actorName, String biography, String birthDate) {
        createPerson(true, actorName, biography, birthDate);
    }

    /**
     * Creates a new person (either a creator or an actor) and adds it to the ontology
     * @param actor A boolean value, signalling if the person to be created is a creator or an actor
     * @param name The name of the person
     * @param biography A small biography of the person
     * @param birthDate The person's date of birth
     */
    private void createPerson(boolean actor, String name, String biography, String birthDate) {
        String trimedName = name.replaceAll(" ", "_").toLowerCase();
        Individual newIndividual;

        if (actor) {
            newIndividual = ontologyModel.createIndividual(namespace + trimedName,
                                                           ontologyModel.getOntClass(namespace + "Actor"));
        } else {
            newIndividual = ontologyModel.createIndividual(namespace + trimedName,
                                                           ontologyModel.getOntClass(namespace + "Creator"));
        }

        // Add actor properties
        newIndividual.addLiteral(ontologyModel.getProperty(namespace + "hasName"), name);
        newIndividual.addLiteral(ontologyModel.getProperty(namespace + "hasBiography"), biography);
        newIndividual.addLiteral(ontologyModel.getProperty(namespace + "hasBirthDate"), birthDate);

        if (actor) {
            // Add actor to actor's list
            actorsList.put(name, newIndividual);
            System.out.println("Created actor " + newIndividual.getLocalName());
        } else {
            creatorsList.put(name, newIndividual);
            System.out.println("Created creator " + newIndividual.getLocalName());
        }
    }

    /**
     * Adds a link (property) between an actor and a tv series
     * @param seriesName The name of the series
     * @param actorName The name of the actor
     * @return A boolean value signalling if any errors occurred (link between the two entities already exists, for
     *         example) or if everything went well
     */
    public boolean addSeriesToActor(String seriesName, String actorName) {
        // Get actor and series
        Individual series = seriesList.get(seriesName);
        Individual actor = actorsList.get(actorName);

        return addActorCreatorToSeries(true, series, actor);
    }

    /**
     * Adds a link (property) between a creator and a tv series
     * @param seriesName The name of the series
     * @param creatorName The name of the creator
     * @return A boolean value signalling if any errors occurred (link between the two entities already exists, for
     *         example) or if everything went well
     */
    public boolean addSeriesToCreator(String seriesName, String creatorName) {
        // Get creator and series
        Individual series = seriesList.get(seriesName);
        Individual creator = creatorsList.get(creatorName);

        return addActorCreatorToSeries(false, series, creator);
    }

    /**
     * Adds a link (property) between a tv series and a creator or an actor
     * @param addActor A boolean value, signalling whether an actor or a creator will be processed
     * @param series An instance of Jena's "Individual" class, representing the tv series
     * @param actorOrCreator An instance of Jena's "Individual" class, representing the actor or the creator
     * @return A boolean value signalling if any errors occurred (link between the two entities already exists, for
     *         example) or if everything went well
     */
    private boolean addActorCreatorToSeries(boolean addActor, Individual series, Individual actorOrCreator) {
        Property property, inverseProperty;

        // Get property and its inverse
        if (addActor) {
            property = ontologyModel.getProperty(namespace + "hasSeriesAppearance");
            inverseProperty = ontologyModel.getProperty(namespace + "hasActor");
        } else {
            property = ontologyModel.getProperty(namespace + "hasSeriesCreated");
            inverseProperty = ontologyModel.getProperty(namespace + "hasCreator");
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

    /**
     * Saves the current ontology to a given file, with the given format
     * @param filePath The path where the file should be saved
     * @param type The format of the file
     * @return A boolean value, signalling if any errors occurred, or if everything went well
     */
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

    /**
     * Returns the ontology's namespace
     * @return The ontology's namespace
     */
    public String getNamespace() {
        return namespace;
    }
}
