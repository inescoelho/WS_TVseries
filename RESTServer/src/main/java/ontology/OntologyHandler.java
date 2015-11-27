package ontology;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import server.data.ontology.Genre;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import server.data.ontology.Person;
import server.data.ontology.Series;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * OntologyHandler class, responsible for managing an ontology, making use of the Apache Jena framework. For more
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
public class OntologyHandler {

    private String namespace;
    private OntModel ontologyModel;

    // Both peopleList and seriesList have the respective ids as keys in the HashMap!!! (ids with nm)
    private HashMap<String, Individual> peopleList;
    private HashMap<String, Individual> seriesList;

    /**
     * Simple class constructor, which loads the ontology schema from a file
     * @param filePath The path to the file containing the ontology schema
     * @param type The format of the file (RDF, OWL, Turtle...)
     */
    public OntologyHandler(String filePath, String type) {
        // Read ontology from file
        createOntologyModel(filePath, type);

        // Get ontology namespace
        namespace = ontologyModel.getNsPrefixURI("");

        peopleList = new HashMap<>();
        seriesList = new HashMap<>();
        loadInstances();
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
     * Loads the instances already present in the ontology
     */
    private void loadInstances() {
        //List all classes and then list all individuals
        OntClass seriesGenreClass = ontologyModel.getOntClass(namespace + "SeriesGenre");
        OntClass personClass = ontologyModel.getOntClass(namespace + "Person");

        OntProperty hasSeriesId = ontologyModel.getOntProperty(namespace + "hasSeriesId");
        OntProperty hasPersonId = ontologyModel.getOntProperty(namespace + "hasPersonId");

        ExtendedIterator classes = ontologyModel.listClasses();

        while(classes.hasNext()) {
            OntClass currentClass = (OntClass) classes.next();
            ExtendedIterator instances = currentClass.listInstances();

            if(currentClass.hasSuperClass(personClass)) {
                while(instances.hasNext()) {
                    Individual currentInstance = (Individual) instances.next();
                    peopleList.put(currentInstance.getPropertyValue(hasPersonId).toString(), currentInstance);
                }

            } else if (currentClass.hasSuperClass(seriesGenreClass)) {
                while(instances.hasNext()) {
                    Individual currentInstance = (Individual) instances.next();
                    seriesList.put(currentInstance.getPropertyValue(hasSeriesId).toString(), currentInstance);
                }
            }
        }
    }

    /**
     * Gets all the tv series categories/genres in the ontology
     * @return An array list of instances of the "Genre" class
     */
    public ArrayList<Genre> getGenres() {

        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX my: <http://www.semanticweb.org/tv/series/ontologies/Tv-Series-Ontology#>\n" +
                "SELECT ?subject WHERE { ?subject rdfs:subClassOf my:SeriesGenre }";

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet results = qExe.execSelect();
        ArrayList<Genre> genres = new ArrayList<>();

        while(results.hasNext()) {
            QuerySolution result = results.next();
            RDFNode subjectNode = result.get("?subject");

            if(subjectNode.isResource()) {
                Resource subject = subjectNode.asResource();
                String genreName = subject.getLocalName();

                Genre currentGenre = new Genre(genreName);

                // Get a list with the names of the series from this category
                ArrayList<String[]> seriesInCategory = getSeriesNamesInGenre(genreName);
                currentGenre.setSeries(seriesInCategory);

                genres.add(currentGenre);
            }
        }

        qExe.close();

        return genres;
    }

    /**
     * Gets the strictly needed information about a given series, based on its id, to display in the series page
     * @param seriesId The id of the series whose information is going to be retrieved
     * @return An instance of the "Series" class, with all the needed information to display
     */
    public Series getSeriesInfo(String seriesId) {
        Individual series = seriesList.get(seriesId);

        if (series == null) {
            return null;
        }

        return makeSeriesFromIndividual(series, seriesId);
    }

    /**
     * Gets the strictly needed information about a given person, based on its id, to display in the person page
     * @param personId The id of the person whose information is going to be retrieved
     * @return An instance of the "Person" class, with all the needed information to display
     */
    public Person getPersonInfo(String personId) {
        Individual person = peopleList.get(personId);

        if (person == null) {
            return null;
        }

        return makePersonFromIndividual(person, personId);
    }

    /**
     * Gets all the names of the series of a given genre
     * @param genreName The genre in question
     * @return An ArrayList of "String[]" objects, containing the name and the id of the each series of the specified
     *         genre
     */
    private ArrayList<String[]> getSeriesNamesInGenre(String genreName) {
        String queryString =
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX my: <http://www.semanticweb.org/tv/series/ontologies/Tv-Series-Ontology#>\n" +
                "SELECT ?title ?id " +
                "WHERE { " +
                "?subject rdf:type my:" + genreName + ". " +
                "?subject my:hasTitle ?title. " +
                "?subject my:hasSeriesId ?id. " +
                "}";

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet resultSet = qExe.execSelect();
        ArrayList<String[]> seriesInCategory = new ArrayList<>();

        while (resultSet.hasNext()) {
            QuerySolution result = resultSet.next();

            RDFNode titleNode = result.get("?title");
            RDFNode idNode = result.get("?id");
            if (titleNode.isLiteral() && idNode.isLiteral()) {
                Literal currentSeries = titleNode.asLiteral();
                Literal currentSeriesId = idNode.asLiteral();

                seriesInCategory.add(new String[] {currentSeries.getString(), currentSeriesId.getString()});
            }
        }

        return seriesInCategory;
    }

    /**
     * Creates an instance of the "Series" class from a series "Individual" instance (Jena)
     * @param series The "Individual" instance
     * @param seriesId The id of the series
     * @return The created instance of the "Series" class
     */
    private Series makeSeriesFromIndividual(Individual series, String seriesId) {
        // Gather series' individual properties
        OntProperty hasTitle = ontologyModel.getDatatypeProperty(namespace + "hasTitle");
        OntProperty hasStoryline = ontologyModel.getDatatypeProperty(namespace + "hasStoryline");
        OntProperty hasDescription = ontologyModel.getDatatypeProperty(namespace + "hasDescription");
        OntProperty hasEpisodeDuration = ontologyModel.getDatatypeProperty(namespace + "hasEpisodeDuration");
        OntProperty hasPilotYear = ontologyModel.getDatatypeProperty(namespace + "hasPilotYear");
        OntProperty hasFinishYear = ontologyModel.getDatatypeProperty(namespace + "hasFinishYear");
        // FIxME: OntProperty hasImageURL = ontologyModel.getDatatypeProperty(namespace + "hasImageURL");


        // Get the values for each property
        String title = series.getPropertyValue(hasTitle).toString();
        String description = series.getPropertyValue(hasDescription).toString();
        String storyline = series.getPropertyValue(hasStoryline).toString();
        // FIXME: String imageURL = series.getPropertyValue(hasImageURL).toString();
        int episodeDuration = getIntFromString(series.getPropertyValue(hasEpisodeDuration).toString());

        int pilotYear;

        if (series.getPropertyValue(hasPilotYear) != null) {
            pilotYear = getIntFromString(series.getPropertyValue(hasPilotYear).toString());
        } else {
            pilotYear = -1;
        }

        int finishYear;

        if (series.getPropertyValue(hasFinishYear) != null) {
            finishYear = getIntFromString(series.getPropertyValue(hasFinishYear).toString());
        } else {
            finishYear = -1;
        }

        ArrayList<String[]> creators = getActorsOrCreatorsFromSeries(seriesId, false);
        ArrayList<String[]> actors = getActorsOrCreatorsFromSeries(seriesId, true);

        // FIXME: Add imageURL
        return new Series(title, description, storyline, seriesId, episodeDuration, pilotYear, finishYear, actors,
                          creators);
    }

    /**
     * Creates an instance of the "Person" class from a series "Individual" instance (Jena)
     * @param person The "Individual" instance
     * @param personId The id of the person
     * @return The created instance of the "Person" class
     */
    private Person makePersonFromIndividual(Individual person, String personId) {
        // Gather person's individual properties
        OntProperty hasName = ontologyModel.getDatatypeProperty(namespace + "hasName");
        OntProperty hasBiography = ontologyModel.getDatatypeProperty(namespace + "hasBiography");
        OntProperty hasBirthDate = ontologyModel.getDatatypeProperty(namespace + "hasBirthDate");
        OntProperty hasWikiURL = ontologyModel.getDatatypeProperty(namespace + "hasWikiURL");
        //FIXME: OntProperty hasImageURL = ontologyModel.getDatatypeProperty(namespace + "hasImageURL");


        String name = person.getPropertyValue(hasName).toString();
        String biography = person.getPropertyValue(hasBiography).toString();
        String birthDate = person.getPropertyValue(hasBirthDate).toString();
        String wikiURL = person.getPropertyValue(hasWikiURL).toString();
        // FIXME: String imageURL = person.getPropertyValue(hasImageURL).toString();

        ArrayList<String[]> seriesCreated = getSeriesActedOrCreatedFromActor(personId, false);
        ArrayList<String[]> seriesActed = getSeriesActedOrCreatedFromActor(personId, true);

        // FIXME: Add imageURL
        return new Person(personId, name, biography, birthDate, wikiURL, seriesActed, seriesCreated);
    }

    /**
     * Gets the relevant information of all the actors or creators in a given series
     * @param seriesId The id of the series
     * @param actor A boolean value, signalling if all the information of actors or creators should be retrieved
     * @return An ArrayList of "String[]" objects will all the relevant information about the actors or creators (name,
     *         id and image url)
     */
    private ArrayList<String[]> getActorsOrCreatorsFromSeries(String seriesId, boolean actor) {
        ArrayList<String[]> result = new ArrayList<>();
        String propertyName;

        if (actor) {
            propertyName = "hasActor";
        } else {
            propertyName = "hasCreator";
        }

        String queryString =
               "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
               "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
               "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
               "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
               "PREFIX my: <http://www.semanticweb.org/tv/series/ontologies/Tv-Series-Ontology#>\n" +
               "SELECT ?personName ?personId ?personImageURL " +
               "WHERE { " +
               "?subject my:hasSeriesId ?id FILTER( regex(?id, '" + seriesId + "') ). " +
               "?subject my:" + propertyName + "?person. " +
               "?person my:hasPersonId ?personId. " +
               "?person my:hasName ?personName. " +
               "?person my:hasWikiURL ?personImageURL. " +
               "}";
        // FIXME: CHANGE HASWIKIURL TO IMAGE URL WHEN ACTORS HAVE IT

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet resultSet = qExe.execSelect();

        while (resultSet.hasNext()) {
            QuerySolution querySolution = resultSet.next();

            RDFNode personNameNode = querySolution.get("?personName");
            RDFNode personIdNode = querySolution.get("?personId");
            RDFNode personImageURLNode = querySolution.get("personImageURL");
            if (personNameNode.isLiteral() && personIdNode.isLiteral() && personImageURLNode.isLiteral()) {
                Literal personName = personNameNode.asLiteral();
                Literal personId = personIdNode.asLiteral();
                Literal personImageURL = personImageURLNode.asLiteral();

                // Remember: first position has id, second has name and third has imageURL!!
                result.add(new String[] {personId.getString(), personName.getString(), personImageURL.getString()});
            }
        }

        qExe.close();
        return result;
    }

    /**
     * Gets the relevant information of all the series created by a given person or in which a given person acted
     * @param personId The id of the person
     * @param acted A boolean value, signalling if all the information of series acted or created should be retrieved
     * @return An ArrayList of "String[]" objects will all the relevant information about the series acted or created
     *         (name, id and image url)
     */
    private ArrayList<String[]> getSeriesActedOrCreatedFromActor(String personId, boolean acted) {
        ArrayList<String[]> result = new ArrayList<>();

        String propertyName;

        if (acted) {
            propertyName = "hasSeriesAppearance";
        } else {
            propertyName = "hasSeriesCreated";
        }

        String queryString =
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX my: <http://www.semanticweb.org/tv/series/ontologies/Tv-Series-Ontology#>\n" +
                "SELECT ?seriesTitle ?seriesID\n" +
                "WHERE {\n" +
                "     ?subject my:hasPersonId ?id FILTER( regex(?id, '" + personId + "') ).\n" +
                "     ?subject my:" + propertyName + "?series.\n" +
                "     ?series my:hasSeriesId ?seriesID.\n" +
                "     ?series my:hasTitle ?seriesTitle.\n" +
                // FIXME: ?series my:hasWikiURL ?personImageURL.
                "}";
        // FIXME: ADD PERSONIMAGEURL

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet resultSet = qExe.execSelect();

        while (resultSet.hasNext()) {
            QuerySolution querySolution = resultSet.next();

            // The next variable is just here to stop Intellij from telling me I have duplicate code!
            // (In fact the code is duplicate but I will add more stuff here)
            int unusedVariable = 2;

            RDFNode seriesTitleNode = querySolution.get("?seriesTitle");
            RDFNode seriesIdNode = querySolution.get("?seriesID");
            // FIXME: RDFNode seriesImageURLNode = querySolution.get("personImageURL");
            if (seriesTitleNode.isLiteral() && seriesIdNode.isLiteral() /*FIXME: && seriesImageURLNode.isLiteral()*/ ) {
                Literal seriesTitle = seriesTitleNode.asLiteral();
                Literal seriesId = seriesIdNode.asLiteral();
                // FIXME: Literal seriesImageURL = seriesImageURLNode.asLiteral();

                // Remember: first position has id, second has name and third has imageURL!!
                result.add(new String[] { seriesId.getString(), seriesTitle.getString(), "imageURL" /*FIXME: seriesImageURL.getString()*/ });
            }
        }

        qExe.close();
        return result;
    }

    /**
     * Extracts the "integer part" of a given string
     * @param string The string to process
     * @return The "integer part" of the string (-1 if string is empty)
     */
    private int getIntFromString(String string) {
        if (string.isEmpty())
            return -1;

        Scanner scanner = new Scanner(string).useDelimiter("[^0-9]+");
        int result = scanner.nextInt();
        scanner.close();
        return result;
    }
}
