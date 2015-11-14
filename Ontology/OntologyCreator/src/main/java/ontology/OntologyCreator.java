package ontology;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * OntologyCreator class, responsible for managing an ontology, making use of the Apache Jena framework. For more
 * information please see https://jena.apache.org/index.html
 *
 * The class has the following attributes:
 *
 *  - namespace     --> The namespace of the ontology
 *  - ontologyModel --> Apache Jena's representation of an ontology model. In our case it contains the representation
 *                      of an ontology model loaded from a given file
 *  - peopleList    --> A map of type "<String, Individual>", representing all the actors and series creators in the
 *                      ontology
 *  - seriesList    --> A map of type "<String, Individual>", representing all the series in the ontology
 *
 */
public class OntologyCreator {

    private String namespace;
    private OntModel ontologyModel;

    private HashMap<String, Individual> peopleList;
    private HashMap<String, Individual> seriesList;

    // FIXME: DO NOT FORGET PERSON ID IS nm0232998 AND MOVIE ID IS tt0232998!!!!!

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

        peopleList = new HashMap<>();
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

    public boolean createSeries(String id, String title, String description, String storyline, int duration,
                                ArrayList<String> genres) {
        return createSeries(id, title, description, storyline, duration, genres, new int[]{-1, -1, -1});
    }

    /**
     * Creates a new tv series and adds it to the ontology
     * @param id The id of the series
     * @param title The tile of the series
     * @param description A brief description of the series
     * @param storyline The storyline of the series
     * @param duration The average duration, in minutes, of each episode of the series
     * @param genres The genres of the series
     * @param optionalParams An array of int values containing BY THIS ORDER the series' pilot year, finish year and
     *                       current season number (-1 for each if no info is supplied)
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    public boolean createSeries(String id, String title, String description, String storyline, int duration,
                                ArrayList<String> genres, int[] optionalParams) {

        String trimedName = title.replaceAll(" ", "_").toLowerCase();

        Individual newSeries = ontologyModel.createIndividual(namespace + trimedName,
                                                              ontologyModel.getOntClass(namespace + "SeriesGenre"));

        if (newSeries == null) {
            return false;
        }

        // Make this series extend its genres
        for (String currentGenre : genres) {
            OntClass currentClass = ontologyModel.getOntClass(namespace + currentGenre);

            // Do not add genres not supported in the ontology
            if (currentClass != null) {
                newSeries.addProperty(RDF.type, currentClass);
            }
        }

        // Add series properties
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasSeriesId"), id);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasTitle"), title);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasDescription"), description);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasStoryline"), storyline);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasEpisodeDuration"), duration);

        // Add optional properties
        if (optionalParams.length == 3) {
            if (optionalParams[0] != -1) {
                newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasPilotYear"), optionalParams[0]);
            }
            if (optionalParams[1] != -1) {
                newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasFinishYear"), optionalParams[1]);
            }

            if (optionalParams[2] != -1) {
                newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasSeasonNumber"), optionalParams[2]);
            }
        }

        // Add series to series list
        seriesList.put(title, newSeries);

        System.out.println("Created series " + newSeries.getLocalName());
        return true;
    }

    /**
     * Creates a new tv series creator and adds it to the ontology
     * @param id The id of the creator
     * @param creatorName The name of the creator
     * @param biography A small biography of the creator
     * @param birthDate The creator's date of birth
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    public boolean createCreator(String id, String creatorName, String biography, String birthDate) {
        return createPerson(false, id, creatorName, biography, birthDate);
    }

    /**
     * Creates a new actor and adds it to the ontology
     * @param id The id of the actor
     * @param actorName The name of the actor
     * @param biography A small biography of the actor
     * @param birthDate The actor's date of birth
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    public boolean createActor(String id, String actorName, String biography, String birthDate) {
        return createPerson(true, id, actorName, biography, birthDate);
    }

    /**
     * Creates a new person (either a creator or an actor) and adds it to the ontology
     * @param actor A boolean value, signalling if the person to be created is a creator or an actor
     * @param id The id of the person
     * @param name The name of the person
     * @param biography A small biography of the person
     * @param birthDate The person's date of birth
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    private boolean createPerson(boolean actor, String id, String name, String biography, String birthDate) {
        String trimedName = name.replaceAll(" ", "_").toLowerCase();
        Individual newIndividual = null;

        if (actor) {
            newIndividual = ontologyModel.createIndividual(namespace + trimedName,
                                                           ontologyModel.getOntClass(namespace + "Actor"));
        } else {
            newIndividual = ontologyModel.createIndividual(namespace + trimedName,
                                                           ontologyModel.getOntClass(namespace + "Creator"));
        }

        if (newIndividual == null) {
            return false;
        }

        // Add actor properties
        newIndividual.addLiteral(ontologyModel.getProperty(namespace + "hasPersonId"), id);
        newIndividual.addLiteral(ontologyModel.getProperty(namespace + "hasName"), name);
        newIndividual.addLiteral(ontologyModel.getProperty(namespace + "hasBiography"), biography);
        newIndividual.addLiteral(ontologyModel.getProperty(namespace + "hasBirthDate"), birthDate);

        // Add person to the list
        peopleList.put(name, newIndividual);

        if (actor) {
            System.out.println("Created actor " + newIndividual.getLocalName());
        } else {
            System.out.println("Created creator " + newIndividual.getLocalName());
        }

        return true;
    }

    /**
     * Make an instance of the Actor class also an instance of the Creator class
     * @param individualName The name of the actor
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    public boolean makeActorCreator(String individualName) {

        Individual individual = peopleList.get(individualName);
        OntClass creatorClass = ontologyModel.getOntClass(namespace + "Creator");

        if (!individual.hasProperty(RDF.type, creatorClass)) {
            individual.addProperty(RDF.type, creatorClass);
            return true;
        }

        // Is already of type creator
        return false;
    }

    /**
     * Make an instance of the Creator class also an instance of the Actor class
     * @param individualName The name of the creator
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    public boolean makeCreatorActor(String individualName) {

        Individual individual = peopleList.get(individualName);
        OntClass actorClass = ontologyModel.getOntClass(namespace + "Actor");

        if (!individual.hasProperty(RDF.type, actorClass)) {
            individual.addProperty(RDF.type, actorClass);
            return true;
        }

        // Is already of type creator
        return false;
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
        Individual actor = peopleList.get(actorName);

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
        Individual creator = peopleList.get(creatorName);

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
            property = ontologyModel.getProperty(namespace + "hasActor");
            inverseProperty = ontologyModel.getProperty(namespace + "hasSeriesAppearance");
        } else {
            property = ontologyModel.getProperty(namespace + "hasCreator");
            inverseProperty = ontologyModel.getProperty(namespace + "hasSeriesCreated");
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

                Statement resId = resource.getProperty(ontologyModel.getDatatypeProperty(namespace + "hasPersonId"));
                Statement personId = actorOrCreator.getProperty(ontologyModel.getDatatypeProperty(namespace +
                                                                                                  "hasPersonId"));

                // If the resources are the same
                if (resId.getLiteral().equals(personId.getLiteral())) {
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
