package ontology;

import data.Date;
import data.Person;
import data.Series;
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
import java.util.Scanner;

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

    // Both peopleList and seriesList have the respective ids as keys in the HashMap!!!
    private HashMap<String, Individual> peopleList;
    private HashMap<String, Individual> seriesList;

    private Scanner scanner;

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

    /**
     * Creates a new tv series and adds it to the ontology
     * @param series The series to be created
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    public boolean createSeries(Series series) {
        if (series == null)
            return false;

        int seriesDuration, startYear, finishYear;

        // Get the series duration as an integer
        seriesDuration = getIntFromString(series.getDuration());
        startYear = getIntFromString(series.getStartYear());
        finishYear = getIntFromString(series.getFinishYear());

        return createSeries(series.getSeriesId(), series.getTitle(), series.getDescription(), series.getStoryline(),
                seriesDuration, series.getGenres(), startYear, finishYear);
    }

    /**
     * Creates a new tv series and adds it to the ontology
     * @param id The id of the series
     * @param title The tile of the series
     * @param description A brief description of the series
     * @param storyline The storyline of the series
     * @param duration The average duration, in minutes, of each episode of the series
     * @param genres The genres of the series
     * @param startYear The year when the series first aired (-1 if series has not yet aired)
     * @param finishYear The year when the series last aired (-1 if series has not finish yet)
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    private boolean createSeries(String id, String title, String description, String storyline, int duration,
                                ArrayList<String> genres, int startYear, int finishYear) {

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
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasSeriesId"), "tt" + id);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasTitle"), title);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasDescription"), description);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasStoryline"), storyline);
        newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasEpisodeDuration"), duration);

        if (startYear != -1) {
            newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasPilotYear"), startYear);
        }

        if (finishYear != -1) {
            newSeries.addLiteral(ontologyModel.getProperty(namespace + "hasFinishYear"), finishYear);
        }

        // Add series to series list
        seriesList.put(id, newSeries);

        System.out.println("Created series " + newSeries.getLocalName());
        return true;
    }

    /**
     * Creates a new tv series creator and adds it to the ontology
     * @param creator The creator to be created
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    public boolean createCreator(Person creator) {
        return createPerson(false, creator.getId(), creator.getName(), creator.getBiography(),
                            creator.getBirthday());
    }

    /**
     * Creates a new actor and adds it to the ontology
     * @param actor The actor to be created
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    public boolean createActor(Person actor) {
        return createPerson(true, actor.getId(), actor.getName(), actor.getBiography(), actor.getBirthday());
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
    private boolean createPerson(boolean actor, String id, String name, String biography, Date birthDate) {
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
        if (biography != null) {
            newIndividual.addLiteral(ontologyModel.getProperty(namespace + "hasBiography"), biography);
        }
        if (birthDate != null) {
            newIndividual.addLiteral(ontologyModel.getProperty(namespace + "hasBirthDate"), birthDate.toString());
        }

        // Add person to the list
        peopleList.put(id, newIndividual);

        if (actor) {
            System.out.println("Created actor " + newIndividual.getLocalName());
        } else {
            System.out.println("Created creator " + newIndividual.getLocalName());
        }

        return true;
    }

    /**
     * Make an instance of the Actor class also an instance of the Creator class
     * @param actor The actor
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    public boolean makeActorCreator(Person actor) {

        Individual individual = peopleList.get(actor.getId());
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
     * @param creator The creator
     * @return A boolean value signalling if any errors occurred or if everything went well
     */
    public boolean makeCreatorActor(Person creator) {

        Individual individual = peopleList.get(creator.getId());
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
     * @param series The series
     * @param actor The actor
     * @return A boolean value signalling if any errors occurred (link between the two entities already exists, for
     *         example) or if everything went well
     */
    public boolean addSeriesToActor(Series series, Person actor) {
        // Get actor and series
        Individual seriesIndividual = seriesList.get(series.getSeriesId());
        Individual actorIndividual = peopleList.get(actor.getId());

        return addActorCreatorToSeries(true, seriesIndividual, actorIndividual);
    }

    /**
     * Adds a link (property) between a creator and a tv series
     * @param series The series
     * @param creator The creator
     * @return A boolean value signalling if any errors occurred (link between the two entities already exists, for
     *         example) or if everything went well
     */
    public boolean addSeriesToCreator(Series series, Person creator) {
        // Get creator and series
        Individual seriesIndividual = seriesList.get(series.getSeriesId());
        Individual creatorIndividual = peopleList.get(creator.getId());

        return addActorCreatorToSeries(false, seriesIndividual, creatorIndividual);
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
     * Checks if a given person already exists in the list of people
     * @param person The person in question
     * @return A boolean value, signalling whether or not the person in question already exists in the system
     */
    public boolean checkPerson(Person person) {
        return peopleList.get(person.getId()) != null;
    }

    /**
     * Checks if the given person is a creator
     * @param creator The possible creator
     * @return A boolean value, signalling if the given person is a creator or not
     */
    public boolean checkCreator(Person creator) {

        Individual possibleCreator = peopleList.get(creator.getId());

        if (possibleCreator == null) {
            return false;
        }

        OntClass creatorClass = ontologyModel.getOntClass(namespace + "Creator");
        if(creatorClass == null) {
            Thread.dumpStack();
            System.out.println("Could not find Creator and this should not have happened!");
            return false;
        }

        return possibleCreator.hasProperty(RDF.type, creatorClass);
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

    /**
     * Extracts the "integer part" of a given string
     * @param string The string to process
     * @return The "integer part" of the string (-1 if string is empty)
     */
    private int getIntFromString(String string) {
        if (string.isEmpty())
            return -1;

        scanner = new Scanner(string).useDelimiter("[^0-9]+");
        int result = scanner.nextInt();
        scanner.close();
        return result;
    }
}
