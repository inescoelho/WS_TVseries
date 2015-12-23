package ontology;

import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import server.data.utils.PeopleAndSeries;
import server.data.ResultObject;
import server.data.OperationResult;
import server.data.ontology.Genre;
import server.data.ontology.Person;
import server.data.ontology.Series;
import server.data.utils.PersonFromSeriesFrequency;
import server.data.utils.SeriesFromPeopleFrequency;

import java.util.*;

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

    private List<String> genres;
    private ResultObject resultObject;

    private final String queryPrefix =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX my: <http://www.semanticweb.org/tv/series/ontologies/Tv-Series-Ontology#>\n";

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

        genres = getGenresNames();
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

        String queryString = queryPrefix +
                "SELECT ?subject WHERE { " +
                "?subject rdfs:subClassOf my:SeriesGenre. " +
                "?subject a ?class. " +
                "} ORDER BY ASC(?class)";

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
        System.out.println("HERE " + person);

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
        String queryString = queryPrefix +
                "SELECT ?title ?id ?imageURL \n" +
                "WHERE { \n" +
                "?subject rdf:type my:" + genreName + " .\n " +
                "?subject my:hasTitle ?title .\n " +
                "?subject my:hasSeriesId ?id .\n " +
                "?subject my:hasSeriesImageURL ?rating .\n " +
                "OPTIONAL { ?subject my:hasSeriesImageURL ?imageURL . }\n " +
                "} ORDER BY DESC(?rating) ?title\n";

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet resultSet = qExe.execSelect();
        ArrayList<String[]> seriesInCategory = new ArrayList<>();

        while (resultSet.hasNext()) {
            QuerySolution result = resultSet.next();

            RDFNode titleNode = result.get("?title");
            RDFNode idNode = result.get("?id");
            RDFNode seriesImageURLNode = result.get("?imageURL");
            if (titleNode.isLiteral() && idNode.isLiteral()) {
                Literal currentSeries = titleNode.asLiteral();
                Literal currentSeriesId = idNode.asLiteral();

                String imageURL = "";
                if (seriesImageURLNode != null && seriesImageURLNode.isLiteral()) {
                    imageURL = seriesImageURLNode.asLiteral().getString();
                }

                seriesInCategory.add(new String[] {currentSeries.getString(), currentSeriesId.getString(), imageURL});
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
        OntProperty hasSeriesImageURL = ontologyModel.getDatatypeProperty(namespace + "hasSeriesImageURL");
        OntProperty hasRating = ontologyModel.getDatatypeProperty(namespace + "hasRating");


        // Get the values for each property
        String title = series.getPropertyValue(hasTitle).toString();
        String description = series.getPropertyValue(hasDescription).toString();
        String storyline = series.getPropertyValue(hasStoryline).toString();
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

        String imageURL;

        if (series.getPropertyValue(hasSeriesImageURL) != null) {
            imageURL = series.getPropertyValue(hasSeriesImageURL).toString();
        } else {
            imageURL = "";
        }

        double rating;

        if (series.getPropertyValue(hasRating) != null) {
            rating = series.getPropertyValue(hasRating).asLiteral().getDouble();
            rating = Math.floor(rating * 100) / 100;
        } else {
            rating = -1;
        }

        ArrayList<String[]> creators = getActorsOrCreatorsFromSeries(seriesId, false, null);
        ArrayList<String[]> actors = getActorsOrCreatorsFromSeries(seriesId, true, null);

        return new Series(title, description, storyline, seriesId, episodeDuration, pilotYear, finishYear, imageURL,
                          rating, actors, creators);
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
        OntProperty hasPersonImageURL = ontologyModel.getDatatypeProperty(namespace + "hasPersonImageURL");


        String name = person.getPropertyValue(hasName).toString();

        String biography = "";
        if (person.getPropertyValue(hasBiography) != null) {
            biography = person.getPropertyValue(hasBiography).toString();
            biography = biography.replace("\"", "");
            biography = biography.replace("\\", "");
        }


        String birthDate = "";
        if (person.getPropertyValue(hasBirthDate) != null) {
            birthDate = person.getPropertyValue(hasBirthDate).toString();
        }

        String wikiURL = "";
        if (person.getPropertyValue(hasWikiURL) != null) {
            wikiURL = person.getPropertyValue(hasWikiURL).toString();
        }

        String imageURL;
        if (person.getPropertyValue(hasPersonImageURL) != null) {
            imageURL = person.getPropertyValue(hasPersonImageURL).toString();
        } else {
            imageURL = "";
        }

        ArrayList<String[]> seriesCreated = getSeriesActedOrCreatedFromActor(personId, false);
        ArrayList<String[]> seriesActed = getSeriesActedOrCreatedFromActor(personId, true);

        return new Person(personId, name, biography, birthDate, wikiURL, imageURL, seriesActed, seriesCreated);
    }

    /**
     * Gets the relevant information of all the actors or creators in a given series
     * @param seriesId The id of the series
     * @param actor A boolean value, signalling if all the information of actors or creators should be retrieved
     * @param lastChecked The list with historical data of the people and series checked by the user
     * @return An ArrayList of "String[]" objects will all the relevant information about the actors or creators (name,
     *         id and image url)
     */
    private ArrayList<String[]> getActorsOrCreatorsFromSeries(String seriesId, boolean actor,
                                                              ArrayList<String> lastChecked) {
        ArrayList<String[]> result = new ArrayList<>();
        String propertyName;

        if (actor) {
            propertyName = "hasActor";
        } else {
            propertyName = "hasCreator";
        }

        String queryString = queryPrefix +
               "SELECT ?personName ?personId ?personImageURL \n" +
               "WHERE { \n" +
               "       ?subject my:hasSeriesId ?id FILTER( regex(?id, '" + seriesId + "') ) .\n" +
               "       ?subject my:" + propertyName + " ?person .\n" +
               "       ?person my:hasPersonId ?personId .\n" +
               "       ?person my:hasName ?personName .\n" +
               "       OPTIONAL { ?person my:hasPersonImageURL ?personImageURL . }\n" +
               "} ORDER BY ASC(?personName)\n";

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet resultSet = qExe.execSelect();

        // System.out.println("\n====================================================\n" + queryString + "\n\n");

        while (resultSet.hasNext()) {
            QuerySolution querySolution = resultSet.next();

            RDFNode personNameNode = querySolution.get("?personName");
            RDFNode personIdNode = querySolution.get("?personId");
            RDFNode personImageURLNode = querySolution.get("?personImageURL");
            if (personNameNode.isLiteral() && personIdNode.isLiteral()) {
                Literal personName = personNameNode.asLiteral();
                Literal personId = personIdNode.asLiteral();

                String personImageUrl = "";
                if (personImageURLNode != null && personImageURLNode.isLiteral()) {
                    Literal personImageURL = personImageURLNode.asLiteral();
                    personImageUrl = personImageURL.getString();
                }

                // Remember: first position has id, second has name and third has imageURL!!
                if (lastChecked == null || !lastChecked.contains(personId.getString())) {
                    result.add(new String[]{personId.getString(), personName.getString(), personImageUrl});
                }
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

        String queryString = queryPrefix +
                "SELECT ?seriesTitle ?seriesID ?seriesImageURL\n" +
                "WHERE {\n" +
                "     ?subject my:hasPersonId ?id FILTER( regex(?id, '" + personId + "') ).\n" +
                "     ?subject my:" + propertyName + " ?series.\n" +
                "     ?series my:hasSeriesId ?seriesID.\n" +
                "     ?series my:hasTitle ?seriesTitle.\n" +
                "     ?series my:hasSeriesImageURL ?seriesImageURL. " +
                "} ORDER BY ASC(?seriesTitle)";

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet resultSet = qExe.execSelect();

        while (resultSet.hasNext()) {
            QuerySolution querySolution = resultSet.next();


            RDFNode seriesTitleNode = querySolution.get("?seriesTitle");
            RDFNode seriesIdNode = querySolution.get("?seriesID");
            RDFNode seriesImageURLNode = querySolution.get("?seriesImageURL");
            if (seriesTitleNode.isLiteral() && seriesIdNode.isLiteral() && seriesImageURLNode.isLiteral() ) {
                Literal seriesTitle = seriesTitleNode.asLiteral();
                Literal seriesId = seriesIdNode.asLiteral();
                Literal seriesImageURL = seriesImageURLNode.asLiteral();

                // Remember: first position has id, second has name and third has imageURL!!
                result.add(new String[] { seriesId.getString(), seriesTitle.getString(), seriesImageURL.getString() });
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

    public OperationResult performSearch(String query) {

        query = query.toLowerCase();

        StringTokenizer tokenizer = new StringTokenizer(query);
        resultObject = new ResultObject();
        TokenType past = null;
        String buffer = "";

        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();

            TokenType type = isCategory(word);

            switch (type) {
                case SERIES:
                case GENRE:
                    buffer = processBuffer(buffer, past, type);
                    past = TokenType.SERIES;
                    resultObject.setSeries(true);
                    break;

                case GENRE_TYPE:
                    buffer = processBuffer(buffer, past, type);
                    past = TokenType.SERIES;
                    resultObject.setSeries(true);
                    resultObject.addToGenreList(word);
                    break;

                case ACTOR:
                    buffer = processBuffer(buffer, past, type);
                    past = TokenType.ACTOR;
                    resultObject.setActor(true);
                    break;

                case CREATOR:
                    buffer = processBuffer(buffer, past, type);
                    past = TokenType.CREATOR;
                    resultObject.setCreator(true);
                    break;

                case PERSON:
                    buffer = processBuffer(buffer, past, type);
                    past = TokenType.PERSON;
                    resultObject.setPerson(true);
                    break;

                case AND:
                    buffer = processBuffer(buffer, past, type);
                    break;

                case BEGIN:
                case END:
                    buffer = processBuffer(buffer, past, type);
                    past = type;
                    break;

                case BETWEEN:
                    buffer = processBuffer(buffer, past, type);
                    if (past == TokenType.BEGIN) {
                        past = TokenType.START_BETWEEN;
                    } else if (past == TokenType.END){
                        past = TokenType.END_BETWEEN;
                    }
                    break;

                case SCORE:
                    buffer = processBuffer(buffer, past, type);
                    resultObject.setScore(ResultObject.ScoreSearch.SET);

                    // Read next token
                    if (tokenizer.hasMoreTokens()) {
                        word = tokenizer.nextToken();
                        past = type;
                        type = isCategory(word);

                        // Number -- Score equal
                        if (type == TokenType.NOT_FOUND_TYPE) {
                            resultObject.setScore(ResultObject.ScoreSearch.SET_EQUAL);
                            buffer = processBuffer(word + " ", past, type);
                        } else if (type == TokenType.EQUAL) {
                            resultObject.setScore(ResultObject.ScoreSearch.SET_EQUAL);

                            buffer = processMoreTokens(tokenizer, past, buffer);

                            /*
                            if (tokenizer.hasMoreTokens()) {
                                word = tokenizer.nextToken(); // Read number
                                TokenType type1 = isCategory(word);
                                if (type1 == TokenType.NOT_FOUND_TYPE) {
                                    buffer = processBuffer(word + " ", past, type1);
                                }
                            }
                            */
                        } else if (type == TokenType.LOWER){
                            resultObject.setScore(ResultObject.ScoreSearch.SET_LOWER);

                            buffer = processMoreTokens(tokenizer, past, buffer);

                            /*
                            if (tokenizer.hasMoreTokens()) {
                                word = tokenizer.nextToken(); // Read number
                                TokenType type1 = isCategory(word);
                                if (type1 == TokenType.NOT_FOUND_TYPE) {
                                    buffer = processBuffer(word + " ", past, type1);
                                }
                            }
                            */
                        } else if (type == TokenType.HIGHER) {
                            resultObject.setScore(ResultObject.ScoreSearch.SET_HIGHER);

                            buffer = processMoreTokens(tokenizer, past, buffer);

                            /*
                            if (tokenizer.hasMoreTokens()) {
                                word = tokenizer.nextToken(); // Read number
                                TokenType type1 = isCategory(word);
                                if (type1 == TokenType.NOT_FOUND_TYPE) {
                                    buffer = processBuffer(word + " ", past, type1);
                                }
                            }
                            */
                        }
                    }

                    break;

                case BORN:
                    buffer = processBuffer(buffer, past, type);
                    resultObject.setBrithYear(ResultObject.BirthYear.SET);

                    // Read next token
                    if (tokenizer.hasMoreTokens()) {
                        word = tokenizer.nextToken();
                        past = type;
                        type = isCategory(word);

                        if (type == TokenType.NOT_FOUND_TYPE) {
                            buffer = processBuffer(word + " ", past, type);
                        }
                    }

                    break;

                case NOT_FOUND_TYPE:
                    buffer += word + " ";
                    break;
            }
        }

        processBuffer(buffer, past, null, true);

        System.out.println(resultObject);

        return processResultObject();
    }

    private String processMoreTokens(StringTokenizer tokenizer, TokenType past, String buffer) {
        String word;
        if (tokenizer.hasMoreTokens()) {
            word = tokenizer.nextToken(); // Read number
            TokenType type1 = isCategory(word);
            if (type1 == TokenType.NOT_FOUND_TYPE) {
                buffer = processBuffer(word + " ", past, type1);
            }
        }
        return buffer;
    }

    private TokenType isCategory(String word) {

        // Check if series keyword
        if (Strings.seriesSynonyms.contains(word)) {
            return TokenType.SERIES;
        }

        // Check if genre keyword
        if (Strings.genresSynonyms.contains(word)) {
            return TokenType.GENRE;
        }

        // Check if specific genre
        if (genres.contains(word)) {
            return TokenType.GENRE_TYPE;
        }

        // Check if actor keyword
        if (Strings.actorSynonyms.contains(word)) {
            return TokenType.ACTOR;
        }

        // Check if creator keyword
        if (Strings.creatorSynonyms.contains(word)) {
            return TokenType.CREATOR;
        }

        // Check if person keyword
        if (Strings.personSynonyms.contains(word)) {
            return TokenType.PERSON;
        }

        // Check if and keyword
        if (Strings.andSynonyms.contains(word)) {
            return TokenType.AND;
        }

        // Check if beginning date
        if (Strings.beginDateSynonyms.contains(word)) {
            return TokenType.BEGIN;
        }

        // Check if end date
        if (Strings.endDateSynonyms.contains(word)) {
            return TokenType.END;
        }

        // Check if between
        if (Strings.betweenSynonyms.contains(word)) {
            return TokenType.BETWEEN;
        }

        // Check if score
        if (Strings.scoreSynonyms.contains(word)) {
            return TokenType.SCORE;
        }

        // Check if lower
        if (Strings.lowerSynonyms.contains(word)) {
            return TokenType.LOWER;
        }

        // Check if higher
        if (Strings.higherSynonyms.contains(word)) {
            return TokenType.HIGHER;
        }

        // Check if equal
        if (Strings.equalSynonyms.contains(word)) {
            return TokenType.EQUAL;
        }

        // Check if born
        if (Strings.bornSynonyms.contains(word)) {
            return TokenType.BORN;
        }

        return TokenType.NOT_FOUND_TYPE;
    }

    private String processBuffer(String buffer, TokenType past, TokenType current) {
        return processBuffer(buffer, past, current, false);
    }

    private String processBuffer(String buffer, TokenType past, TokenType current, boolean last) {
        TokenType type;

        if (past == null) {
            // Consider current
            if (current == TokenType.PERSON || current == TokenType.ACTOR || current == TokenType.CREATOR) {
                type = TokenType.SERIES;
                resultObject.setSeries(true);
            } else {
                type = current;
            }
        } else {
            // Consider past
            type = past;

            if (past == TokenType.SCORE) {
                // Current is not going to be null and will always have the type
                if (current == TokenType.NOT_FOUND_TYPE) {
                    // Buffer has the value
                    float value = Float.valueOf(buffer.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
                    resultObject.setScoreValue(value);
                }

                return "";
            } else if (past == TokenType.BORN) {
                // Current is not going to be null and will always have the type
                if (current == TokenType.NOT_FOUND_TYPE) {
                    // buffer has the value
                    int value = getIntFromString(buffer);
                    resultObject.setBornYearValue(value);
                }

                return "";
            }
        }

        if (buffer.length() == 0) {
            if (last && type == TokenType.BEGIN) {
                resultObject.setStillRunning(true);
            } else if (last && type == TokenType.END) {
                resultObject.setStillRunning(false);
            }

            return "";
        }

        buffer = buffer.trim();
        // Analyse
        if (type == TokenType.GENRE || type == TokenType.GENRE_TYPE || type == TokenType.SERIES) {
            resultObject.addSeriesTitle(buffer);
        } else if(type == TokenType.ACTOR) {
            resultObject.addActor(buffer);
        } else if (type == TokenType.CREATOR) {
            resultObject.addCreator(buffer);
        } else if (type == TokenType.PERSON) {
            resultObject.addPerson(buffer);
        } else if (type == TokenType.BEGIN) {
            // buffer has an int
            int year = getIntFromString(buffer);
            resultObject.setStartedYear(new int[] {year});
        } else if (type == TokenType.END) {
            // buffer has an int
            int year = getIntFromString(buffer);
            resultObject.setFinishYear(new int[] {year});
        } else if (type == TokenType.START_BETWEEN) {
            int startYear1, startYear2;
            String[] temp = buffer.split(" ");
            startYear1 = getIntFromString(temp[0]);
            startYear2 = getIntFromString(temp[1]);

            resultObject.setStartedYear(new int[] {startYear1, startYear2});
        } else if (type == TokenType.END_BETWEEN) {
            int finishYear1, finishYear2;
            String[] temp = buffer.split(" ");
            finishYear1 = getIntFromString(temp[0]);
            finishYear2 = getIntFromString(temp[1]);

            resultObject.setFinishYear(new int[] {finishYear1, finishYear2});
        } else if (type == null) {
            // No past nor current
            resultObject.setPerson(true);
            resultObject.setSeries(true);
            resultObject.addPerson(buffer);
            resultObject.addSeriesTitle(buffer);
        }

        return "";
    }

    private OperationResult processResultObject() {

        ArrayList<String[]> series = new ArrayList<>();
        ArrayList<String[]> people = new ArrayList<>();

        if (resultObject.isSeries()) {
            // Search series

            List<String> actorsList = resultObject.getActorsList();
            List<String> creatorsList = resultObject.getCreatorsList();

            if (checkIfEquals(resultObject.getSeriesTiles(), resultObject.getPeopleList())) {

                for (String seriesTitle : resultObject.getSeriesTiles()) {
                    ArrayList<String[]> currentSeriesFound = searchSeries(seriesTitle, actorsList, creatorsList);
                    for (String[] temp : currentSeriesFound) {
                        series.add(temp);
                    }
                }
            } else if (resultObject.getSeriesTiles().size() > 0) {

                for (String seriesTitle : resultObject.getSeriesTiles()) {

                    boolean firstTime = true;
                    ArrayList<String[]> seriesWhatever = new ArrayList<>();

                    // Search for people
                    for (String currentPerson : resultObject.getPeopleList()) {
                        actorsList.add(currentPerson);
                        ArrayList<String[]> currentSeriesFound1 = searchSeries(seriesTitle, actorsList, creatorsList);
                        actorsList.remove(currentPerson);

                        creatorsList.add(currentPerson);
                        ArrayList<String[]> currentSeriesFound2 = searchSeries(seriesTitle, actorsList, creatorsList);
                        creatorsList.remove(currentPerson);

                        // Join currentSeriesFound1 and currentSeriesFound2
                        joinSeries(currentSeriesFound2, currentSeriesFound1);

                        // Intersect what we have in currentSeriesFound with what is already stored in "series"
                        if (firstTime) {
                            // Just add to seriesWhatever
                            for (String[] temp : currentSeriesFound1) {
                                seriesWhatever.add(temp);
                            }

                            firstTime = false;
                        } else {
                            // Merge them and if nothing in common set series to empty and just break
                            boolean somethingInCommon = false;
                            ArrayList<Integer> indexesToRemove = new ArrayList<>();
                            for (String[] temp : seriesWhatever) {

                                boolean common = false;
                                for (String[] temp2 : currentSeriesFound1) {
                                    if (temp[1].equals(temp2[1])) {
                                        somethingInCommon = true;
                                        common = true;
                                    }
                                }

                                if (!common) {
                                    indexesToRemove.add(seriesWhatever.indexOf(temp));
                                }
                            }

                            if (!somethingInCommon) {
                                seriesWhatever = new ArrayList<>();
                                break;
                            } else {
                                for (int i=indexesToRemove.size()-1; i >= 0; i--) {
                                    series.remove((int)indexesToRemove.get(i));
                                }
                            }
                        }
                    }

                    // Just add to series
                    for (String[] temp : seriesWhatever) {
                        series.add(temp);
                    }

                    // Just search without people (if no people them seriesWhatever is empty, so the previous for does
                    // not execute
                    if (resultObject.getPeopleList().size() == 0) {
                        actorsList = resultObject.getActorsList();
                        creatorsList = resultObject.getCreatorsList();
                        ArrayList<String[]> currentSeriesFound = searchSeries(seriesTitle, actorsList, creatorsList);
                        for (String[] temp : currentSeriesFound) {
                            series.add(temp);
                        }
                    }
                }

            } else {

                if (!resultObject.isPerson()) {
                    ArrayList<String[]> currentSeriesFound = searchSeries("", actorsList, creatorsList);

                    for (String[] aCurrentSeriesFound : currentSeriesFound) {
                        series.add(aCurrentSeriesFound);
                    }
                } else {

                    boolean firstTime = true;

                    for (String currentPerson : resultObject.getPeopleList()) {
                        actorsList.add(currentPerson);
                        ArrayList<String[]> currentSeriesFound1 = searchSeries("", actorsList, creatorsList);
                        actorsList.remove(currentPerson);

                        creatorsList.add(currentPerson);
                        ArrayList<String[]> currentSeriesFound2 = searchSeries("", actorsList, creatorsList);
                        creatorsList.remove(currentPerson);

                        // Join currentSeriesFound1 and currentSeriesFound2
                        joinSeries(currentSeriesFound2, currentSeriesFound1);

                        // Intersect what we have in currentSeriesFound with what is already stored in "series"
                        if (firstTime) {
                            // Just add to series
                            for (String[] temp : currentSeriesFound1) {
                                series.add(temp);
                            }

                            firstTime = false;
                        } else {
                            // Merge them and if nothing in common set series to empty and just break
                            boolean somethingInCommon = false;
                            ArrayList<Integer> indexesToRemove = new ArrayList<>();
                            for (String[] temp : series) {

                                boolean common = false;
                                for (String[] temp2 : currentSeriesFound1) {
                                    if (temp[1].equals(temp2[1])) {
                                        somethingInCommon = true;
                                        common = true;
                                    }
                                }

                                if (!common) {
                                    indexesToRemove.add(series.indexOf(temp));
                                }
                            }

                            if (!somethingInCommon) {
                                series = new ArrayList<>();
                                break;
                            } else {

                                for (int i=indexesToRemove.size()-1; i >= 0; i--) {
                                    series.remove((int)indexesToRemove.get(i));
                                }
                            }
                        }
                    }
                }
            }

            if (series.size() == 0) {
                System.out.println("Could not find any series!!!");
            } else {
                for (String[] currentSeries : series) {
                    System.out.println("Found Series " + currentSeries[0] + " " + currentSeries[1]);
                }
            }

        }

        if (resultObject.isActor()) {
            int sizeBefore = people.size();
            // Search actor
            for (String actorName : resultObject.getActorsList()) {
                ArrayList<String[]> currentActorsFound = searchActors(actorName);

                for (String[] aCurrentActor : currentActorsFound) {
                    people.add(aCurrentActor);
                }

                if (people.size() == sizeBefore) {
                    System.out.println("Could not find any actors!!!");
                }

                for (int i = sizeBefore; i < people.size(); i++) {
                    System.out.println("Found Actor " + people.get(i)[0] + " " + people.get(i)[1]);
                }
            }
        }

        if (resultObject.isCreator()) {
            int sizeBefore = people.size();
            // Search creator
            for (String creatorName : resultObject.getCreatorsList()) {
                ArrayList<String[]> currentCreatorsFound = searchCreators(creatorName);

                for (String[] aCurrentCreator : currentCreatorsFound) {
                    people.add(aCurrentCreator);
                }

                if (people.size() == sizeBefore) {
                    System.out.println("Could not find any creators!!!");
                }

                for (int i = sizeBefore; i < people.size(); i++) {
                    System.out.println("Found Creator " + people.get(i)[0] + " " + people.get(i)[1]);
                }
            }
        }

        if (resultObject.isPerson()) {
            int sizeBefore = people.size();
            // Search people
            for (String name : resultObject.getPeopleList()) {
                ArrayList<String[]> currentPeopleFound = searchPeople(name);

                for (String[] aCurrentPerson : currentPeopleFound) {
                    people.add(aCurrentPerson);
                }
            }

            if (people.size() == sizeBefore) {
                System.out.println("Could not find any people!!!");
            }

            for (int i = sizeBefore; i < people.size(); i++) {
                System.out.println("Found Person " + people.get(i)[0] + " " + people.get(i)[1]);
            }
        }

        OperationResult operationResult = new OperationResult();
        operationResult.setSeries(series);
        operationResult.setPeople(people);

        return operationResult;
    }

    private void joinSeries(ArrayList<String[]> series1, ArrayList<String[]> series2) {

        for (String[] temp : series1) {
            boolean contains = false;

            for (String[] temp2 : series2) {
                if (temp2[1].equals(temp[1])) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                series2.add(temp);
            }
        }
    }

    private List<String> getGenresNames() {
        String queryString = queryPrefix +
                "SELECT ?subject WHERE { " +
                "?subject rdfs:subClassOf my:SeriesGenre. " +
                "?subject a ?class. " +
                "} ORDER BY ASC(?class)";

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet results = qExe.execSelect();
        List<String> genres = new ArrayList<>();

        while(results.hasNext()) {
            QuerySolution result = results.next();
            RDFNode subjectNode = result.get("?subject");

            if (subjectNode.isResource()) {
                Resource subject = subjectNode.asResource();
                String genreName = subject.getLocalName().toLowerCase();

                if (!genres.contains(genreName)) {
                    genres.add(genreName);
                }
            }
        }

        return genres;
    }

    private ArrayList<String[]> searchPeople(String name) {
        ArrayList<String[]> peopleList = new ArrayList<>();

        String queryString = queryPrefix +
                "SELECT ?personName ?personID ?personImageURL " +
                "WHERE {\n" +
                "     ?person my:hasPersonId ?personID .\n" +
                "     ?person my:hasPersonImageURL ?personImageURL .\n" +
                "     ?person my:hasName ?personName FILTER regex(?personName, '" + name + "', 'i') .\n" +
                "} ORDER BY ASC(?personName)";

        // System.out.println("\n===============================================\n" + queryString + "\n");

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet results = qExe.execSelect();

        while (results.hasNext()) {
            QuerySolution result = results.next();
            RDFNode personNameNode = result.get("?personName");
            RDFNode personIdNode = result.get("?personID");
            RDFNode personImageUrlNode = result.get("?personImageURL");

            addPersonToList(personNameNode, personIdNode, personImageUrlNode, peopleList);
        }

        return peopleList;
    }

    private ArrayList<String[]> searchCreators(String name) {
        ArrayList<String[]> creatorsList = new ArrayList<>();

        String queryString = queryPrefix +
                "SELECT ?creatorName ?creatorID ?creatorImageURL " +
                "WHERE {\n" +
                "     ?creator rdf:type my:Creator .\n" +
                "     ?creator my:hasPersonId ?creatorID .\n" +
                "     ?creator my:hasPersonImageURL ?creatorImageURL .\n" +
                "     ?creator my:hasName ?creatorName FILTER regex(?creatorName, '" + name + "', 'i') .\n" +
                "} ORDER BY ASC(?creatorName)";

        //System.out.println("\n===============================\n" + queryString + "\n");

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet results = qExe.execSelect();

        while (results.hasNext()) {
            QuerySolution result = results.next();
            RDFNode creatorNameNode = result.get("?creatorName");
            RDFNode creatorIdNode = result.get("?creatorID");
            RDFNode creatorImageNode = result.get("?creatorImageURL");

            addPersonToList(creatorNameNode, creatorIdNode, creatorImageNode, creatorsList);
        }

        return creatorsList;
    }

    private ArrayList<String[]> searchActors(String name) {
        ArrayList<String[]> actorsList = new ArrayList<>();

        String queryString = queryPrefix +
                "SELECT ?actorName ?actorID ?actorImageURL " +
                "WHERE {\n" +
                "     ?actor rdf:type my:Actor .\n" +
                "     ?actor my:hasPersonId ?actorID .\n" +
                "     ?actor my:hasPersonImageURL ?actorImageURL .\n" +
                "     ?actor my:hasName ?actorName FILTER regex(?actorName, '" + name + "', 'i') .\n" +
                "} ORDER BY ASC(?actorName)";

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet results = qExe.execSelect();

        while (results.hasNext()) {
            QuerySolution result = results.next();
            RDFNode actorNameNode = result.get("?actorName");
            RDFNode actorIdNode = result.get("?actorID");
            RDFNode actorImageNode = result.get("?actorImageURL");

            addPersonToList(actorNameNode, actorIdNode, actorImageNode, actorsList);
        }

        return actorsList;
    }

    private void addPersonToList(RDFNode nameNode, RDFNode idNode, RDFNode imageNode, ArrayList<String[]> list) {
        String[] temp = new String[3];

        if (nameNode.isLiteral() && idNode.isLiteral()) {

            Literal name = nameNode.asLiteral();
            String personName = name.getString();

            Literal id = idNode.asLiteral();
            String person_id = id.getString().toLowerCase();

            String image_url = "";
            if (imageNode != null && imageNode.isLiteral()) {
                Literal imageUrl = imageNode.asLiteral();
                image_url = imageUrl.getString();
            }

            temp[0] = personName;
            temp[1] = person_id;
            temp[2] = image_url;

            list.add(temp);
        }
    }

    private ArrayList<String[]> searchSeries(String title, List<String> actors, List<String> creators) {
        ArrayList<String[]> seriesList = new ArrayList<>();
        int counter = 0;

        String queryString = queryPrefix +
                "SELECT DISTINCT ?seriesTitle ?seriesID ?imageURL " +
                "WHERE {\n" +
                "     ?series my:hasSeriesId ?seriesID .\n" +
                "     ?series my:hasSeriesImageURL ?imageURL .\n";

        if (!title.equals("")) {
            queryString += "     ?series my:hasTitle ?seriesTitle FILTER regex(?seriesTitle, '" + title + "', 'i') .\n";
        } else {
            queryString += "     ?series my:hasTitle ?seriesTitle .\n";
        }

        // Add genre
        for (String currentGenre : resultObject.getGenreList()) {
            // Convert genre type to upper string, handling special cases
            switch (currentGenre) {
                case "sci-fi":
                    currentGenre = "Sci-Fi";
                    break;
                case "film-noir":
                    currentGenre = "Film-Noir";
                    break;
                default:
                    currentGenre = Character.toUpperCase(currentGenre.charAt(0)) + currentGenre.substring(1);
                    break;
            }

            queryString += "     ?series rdf:type my:" + currentGenre + " .\n";
        }

        // Add starting year
        if (resultObject.getStartedYear() != null && resultObject.getStartedYear().length > 0) {

            if (resultObject.getStartedYear().length >= 1) {
                queryString += "     ?series my:hasPilotYear ?pilotYear FILTER(?pilotYear >= " +
                        resultObject.getStartedYear()[0] + ") .\n";
            }

            if (resultObject.getStartedYear().length >= 2) {
                queryString += "     ?series my:hasPilotYear ?pilotYear FILTER(?pilotYear <= " +
                        resultObject.getStartedYear()[1] + ") .\n";
            }

        } else if (resultObject.getStillRunning() != null &&
                   resultObject.getStillRunning() == ResultObject.Running.STILL_RUNNING) {
            // Add series has started - Has pilot year but does not have finish year
            queryString += "     ?series my:hasPilotYear ?pilotYear .\n";
            queryString += "     FILTER NOT EXISTS { ?series my:hasFinishYear ?finishYear } .\n";
        }

        // Add finish year
        if (resultObject.getFinishYear() !=  null && resultObject.getFinishYear().length > 0) {

            if (resultObject.getFinishYear().length >= 1) {
                queryString += "      ?series my:hasFinishYear ?finishYear FILTER(?finishYear >= " +
                        resultObject.getFinishYear()[0] + ") .\n";
            }

            if (resultObject.getFinishYear().length >= 2) {
                queryString += "      ?series my:hasFinishYear ?finishYear FILTER(?finishYear <= " +
                        resultObject.getFinishYear()[1] + ") .\n";
            }

        } else if (resultObject.getStillRunning() != null &&
                   resultObject.getStillRunning() == ResultObject.Running.ALREADY_FINISHED) {
            // Add series has finished
            queryString += "     ?series my:hasFinishYear ?finishYear .\n";
        }

        // Add score
        if (resultObject.getScore() != null && resultObject.getScore() != ResultObject.ScoreSearch.NOT_SET) {
            if (resultObject.getScore() == ResultObject.ScoreSearch.SET ||
                resultObject.getScore() == ResultObject.ScoreSearch.SET_EQUAL) {
                queryString += "     ?series my:hasRating ?rating FILTER(?rating = " + resultObject.getScoreValue() +
                               ") .\n";
            } else if (resultObject.getScore() == ResultObject.ScoreSearch.SET_LOWER) {
                queryString += "     ?series my:hasRating ?rating FILTER(?rating < " + resultObject.getScoreValue() +
                        ") .\n";
            } else if (resultObject.getScore() == ResultObject.ScoreSearch.SET_HIGHER) {
                queryString += "     ?series my:hasRating ?rating FILTER(?rating > " + resultObject.getScoreValue() +
                        ") .\n";
            }
        }

        // Add born year
        if (resultObject.getBrithYear() != null && resultObject.getBrithYear() != ResultObject.BirthYear.NOT_SET) {
            queryString += "     ?series my:hasActor ?actor" + counter + " .\n" +
                           "     ?actor" + counter + " my:hasBirthDate ?birthDate FILTER regex(?birthDate, '" +
                           resultObject.getBornYearValue() + "', 'i') .\n";
            counter++;
        }

        // Add actors, creators
        if (actors.size() > 0) {
            // Add restriction - Series must have actors
            for (String actorName : actors) {
                queryString += "     ?series my:hasActor ?actor" + counter + " .\n";
                queryString += "     ?actor" + counter + " my:hasName ?actorName" + counter +
                        " FILTER regex(?actorName" + counter + ", '"  + actorName + "', 'i') .\n";
                counter++;
            }
        }

        if (creators.size() > 0) {
            // Add restriction - Series must have creators
            for (String creatorName : creators) {
                queryString += "     ?series my:hasCreator ?creator" + counter + " .\n";
                queryString += "     ?creator" + counter + " my:hasName ?creatorName" + counter +
                        " FILTER regex(?creatorName" + counter +", '"  + creatorName + "', 'i') .\n";
                counter++;
            }
        }

        queryString += "} ORDER BY ASC(?seriesTitle)";

        System.out.println("\n===============================\n" + queryString + "\n");

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet results = qExe.execSelect();

        while (results.hasNext()) {
            QuerySolution result = results.next();
            RDFNode seriesTitleNode = result.get("?seriesTitle");
            RDFNode seriesIdNode = result.get("?seriesID");
            RDFNode seriesImageNode = result.get("?imageURL");
            String[] temp = new String[3];

            if (seriesTitleNode.isLiteral() && seriesIdNode.isLiteral()) {
                Literal seriesTitle = seriesTitleNode.asLiteral();
                String series_title = seriesTitle.getString();

                Literal seriesId = seriesIdNode.asLiteral();
                String series_id = seriesId.getString().toLowerCase();

                String image_url = "";
                if (seriesImageNode.isLiteral()) {
                    Literal imageUrl = seriesImageNode.asLiteral();
                    image_url = imageUrl.getString();
                }

                temp[0] = series_title;
                temp[1] = series_id;
                temp[2] = image_url;

                seriesList.add(temp);
            }
        }

        return seriesList;
    }

    private boolean checkIfEquals(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        } else if (list1.size() == 0 || list2.size() == 0) {
            return false;
        }

        for (int i=0; i<list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }


    public OperationResult performRecommendation(ArrayList<String> lastChecked, int numItemsToCheck,
                                                 int numSeriesToRecommend, int numPeopleToRecommend) {

        OperationResult operationResult = new OperationResult();

        if (lastChecked.size() == 0) {
            handleEmptyRecommendation(lastChecked, operationResult, numSeriesToRecommend, numPeopleToRecommend);
        } else {

            // Go to the last "numItemsToCheck" items of the lastChecked array and get the people and the series.
            PeopleAndSeries peopleAndSeries = getSeriesAndPeopleFromLastChecked(lastChecked, numItemsToCheck);
            ArrayList<String> peopleIds = peopleAndSeries.getPeopleList();
            ArrayList<String> seriesIds = peopleAndSeries.getSeriesList();

            // Now, for each person in the peopleIds list, get their series, counting their frequency and store it in an
            // array
            if (peopleIds != null && peopleIds.size() > 0) {
                ArrayList<SeriesFromPeopleFrequency> seriesFromPeopleFrequency = getPeopleSeriesFrequency(peopleIds);

                // Sort this array by score in descending order
                Collections.sort(seriesFromPeopleFrequency, (o1, o2) -> {
                    if (o2.getSeriesRating() > o1.getSeriesRating())
                        return 1;
                    else if (o2.getSeriesRating() == o1.getSeriesRating()) {
                        return o2.getFrequency() - o1.getFrequency();
                    }
                    return -1;
                });

                for (SeriesFromPeopleFrequency current : seriesFromPeopleFrequency) {
                    System.out.println(current);
                }

                System.out.println("==========================================STEP1==================================");
                performRecommendationStep1(operationResult, lastChecked, seriesFromPeopleFrequency);

                System.out.println("==========================================STEP2==================================");
                performRecommendationStep2(operationResult, lastChecked, seriesFromPeopleFrequency);
            }

            if (seriesIds != null && seriesIds.size() > 0) {
                System.out.println("==========================================STEP3==================================");
                performRecommendationStep3(operationResult, lastChecked, seriesIds);
            }

            // Perform last recommendation step
            performLastRecommendationStep(lastChecked, operationResult, numSeriesToRecommend, numPeopleToRecommend);
        }

        return operationResult;
    }

    private PeopleAndSeries getSeriesAndPeopleFromLastChecked(ArrayList<String> lastChecked, int numItemsToCheck) {

        if (lastChecked == null || lastChecked.size() == 0) {
            return null;
        }

        int i = lastChecked.size()-1;
        ArrayList<String> series = new ArrayList<>();
        ArrayList<String> people = new ArrayList<>();
        while (i >= 0 && numItemsToCheck >= 0) {
            String temp = lastChecked.get(i);

            if(temp.contains("tt")) {
                // Series
                series.add(temp);
            } else {
                // People
                people.add(temp);
            }

            i--;
            numItemsToCheck--;
        }

        return new PeopleAndSeries(people, series);
    }

    private ArrayList<SeriesFromPeopleFrequency> getPeopleSeriesFrequency(ArrayList<String> peopleIds) {

        ArrayList<SeriesFromPeopleFrequency> result = new ArrayList<>();

        for (String personId : peopleIds) {
            // Get the series from the person and add it to the result ArrayList
            ArrayList<String[]> personSeries = getSeriesFromPerson(personId);

            for (String[] currentSeries : personSeries) {
                String seriesId = currentSeries[0];
                double seriesRating = Double.parseDouble(currentSeries[1]);
                String seriesTitle = currentSeries[2];
                String seriesImageURL = currentSeries[3];

                addToPeopleSeriesFrequency(result, seriesId, seriesTitle, seriesImageURL, seriesRating);
            }
        }

        return result;
    }

    private int addSeriesToOperationResult(OperationResult operationResult, SeriesFromPeopleFrequency current,
                                           int added) {
        String[] temp = new String[3];
        temp[0] = current.getSeriesTitle();
        temp[1] = current.getSeriesId();
        temp[2] = current.getSeriesImageURL();

        boolean result = operationResult.addSeries(temp);
        if (result) {
            System.out.println("Added " + current.getSeriesTitle());
            return added+1;
        }
        return added;
    }

    private boolean addPersonToOperationResult(OperationResult operationResult, PersonFromSeriesFrequency current) {
        String[] temp = new String[3];
        temp[0] = current.getPersonName();
        temp[1] = current.getPersonId();
        temp[2] = current.getPersonImageURL();

        return operationResult.addPerson(temp);
    }

    private void addRandomPeopleToOperationResult(OperationResult operationResult,
                                                  ArrayList<PersonFromSeriesFrequency> peopleInCommonNotSeen,
                                                  int start, int finish, int limitPeopleAdd) {
        int added = 0;

        while (added < limitPeopleAdd) {
            int index = (int) (Math.random() * (finish - start) + start);

            if (addPersonToOperationResult(operationResult, peopleInCommonNotSeen.get(index))) {
                System.out.println("Added " + peopleInCommonNotSeen.get(index).getPersonId() + " with name " +
                        peopleInCommonNotSeen.get(index).getPersonName());
                added++;
            }
        }
    }

    private void mergeActorsCreators(ArrayList<String[]> actors, ArrayList<String[]> creators,
                                     ArrayList<PersonFromSeriesFrequency> peopleList) {

        boolean exists;

        // Start with actors
        for (String[] currentActor : actors) {
            exists = false;

            for (PersonFromSeriesFrequency currentPerson : peopleList) {
                if (currentPerson.getPersonId().equals(currentActor[1])) {
                    exists = true;
                    currentPerson.increaseFrequency();
                    break;
                }
            }

            if (!exists) {
                peopleList.add(new PersonFromSeriesFrequency(currentActor[1], currentActor[0], currentActor[2]));
            }
        }
    }

    private void performRecommendationStep1(OperationResult operationResult, ArrayList<String> lastChecked,
                                            ArrayList<SeriesFromPeopleFrequency> seriesFromPeopleFrequency) {
        // Get series in common (frequency > 1) that have not been seen and add them to the list of series to
        // recommend (in operationResult) -- Max 3

        int MAX_LIMIT = 3;
        int added = 0;

        for (SeriesFromPeopleFrequency current : seriesFromPeopleFrequency) {

            if (added >= MAX_LIMIT) {
                return;
            }

            // Check if series has more than one person
            if (current.getFrequency() > 1) {

                // Check if current series has been checked and we can still add more series
                if (!lastChecked.contains(current.getSeriesId())) {
                    added = addSeriesToOperationResult(operationResult, current, added);
                }
            }
        }
    }

    private void performRecommendationStep2(OperationResult operationResult, ArrayList<String> lastChecked,
                                            ArrayList<SeriesFromPeopleFrequency> seriesFromPeopleFrequency) {
        // Go through the ordered array of seriesFromPeopleFrequency and get the top 2 series with higher score that
        // have not been seen and recommend them

        int MAX_LIMIT = 2;
        int added = 0;

        for (SeriesFromPeopleFrequency current : seriesFromPeopleFrequency) {

            if (added >= MAX_LIMIT) {
                return;
            }

            // Check if current series has been seen
            if (!lastChecked.contains(current.getSeriesId())) {
                added = addSeriesToOperationResult(operationResult, current, added);
            }
        }
    }

    private void performRecommendationStep3(OperationResult operationResult, ArrayList<String> lastChecked,
                                            ArrayList<String> seriesIds) {

        // Recommend actors in common from the last viewed series that have not been checked nor recommended yet
        // In fact gonna do something different

        int MAX_LIMIT = 3;
        ArrayList<PersonFromSeriesFrequency> peopleInCommonNotSeen = new ArrayList<>();
        ArrayList<String[]> actorsNotSeenCurrentSeries;
        ArrayList<String[]> creatorsNotSeenCurrentSeries;

        if (seriesIds == null || seriesIds.size() == 0) {
            return ;
        }


        for (String seriesId : seriesIds) {
            // Get list of actors and creators not already seen from the current series
            actorsNotSeenCurrentSeries = getActorsOrCreatorsFromSeries(seriesId, true, lastChecked);
            creatorsNotSeenCurrentSeries = getActorsOrCreatorsFromSeries(seriesId, false, lastChecked);

            // Merge these two arrays into the "peopleInCommonNotSeen" array
            mergeActorsCreators(actorsNotSeenCurrentSeries, creatorsNotSeenCurrentSeries, peopleInCommonNotSeen);
        }

        // Sort the array by frequency in descending order
        Collections.sort(peopleInCommonNotSeen, (o1, o2) -> o2.getFrequency() - o1.getFrequency());

        // Now process this array as stated: From the people with frequency higher than one get up to 2 random people
        // From the rest of the people get 1 random person (or more, if less than 2 people have frequency > 1)
        if (peopleInCommonNotSeen.size() == 0) {
            return ;
        }

        if (peopleInCommonNotSeen.get(0).getFrequency() > 1) {
            int lastIndex =  peopleInCommonNotSeen.size();

            for(int i=0; i < peopleInCommonNotSeen.size(); i++) {
                if (peopleInCommonNotSeen.get(i).getFrequency() == 1) {
                    lastIndex = i+1;
                    break;
                }
            }

            if (lastIndex == 1) {
                // Only one case -- Don't bother checking if we were able to add because we only have one possibility
                addPersonToOperationResult(operationResult, peopleInCommonNotSeen.get(0));
            } else if (lastIndex == 2) {
                // Only two cases -- Don't bother checking if we were able to add because we only have one possibility
                addPersonToOperationResult(operationResult, peopleInCommonNotSeen.get(1));
                addPersonToOperationResult(operationResult, peopleInCommonNotSeen.get(2));

                // Generate another random person with frequency = 1
                addRandomPeopleToOperationResult(operationResult, peopleInCommonNotSeen, lastIndex,
                        peopleInCommonNotSeen.size(), 1);

            } else {
                // Generate 2 random people with frequency > 1
                addRandomPeopleToOperationResult(operationResult, peopleInCommonNotSeen, 0, lastIndex, MAX_LIMIT-1);

                // Generate another random person with frequency = 1
                addRandomPeopleToOperationResult(operationResult, peopleInCommonNotSeen, lastIndex,
                        peopleInCommonNotSeen.size(), 1);
            }
        } else {
            // No people with frequency higher than 1 - Get 3 random people
            addRandomPeopleToOperationResult(operationResult, peopleInCommonNotSeen, 0, peopleInCommonNotSeen.size(),
                                             MAX_LIMIT);
        }

    }

    private void performLastRecommendationStep(ArrayList<String> lastChecked, OperationResult operationResult,
                                               int numSeriesToRecommend, int numPeopleToRecommend) {
        int remainingSeries = numSeriesToRecommend - operationResult.getSeries().size();
        if (remainingSeries < 0) {
            remainingSeries = 0;
        }
        int remainingPeople = numPeopleToRecommend - operationResult.getPeople().size();
        if (remainingPeople < 0) {
            remainingPeople = 0;
        }
        handleEmptyRecommendation(lastChecked, operationResult, remainingSeries, remainingPeople);
    }

    /**
     * Gets all the series information from a given person
     * @param personId The id of the person
     * @return A list of String[] objects, containing the id of the series, rating, title and imageurl
     */
    private ArrayList<String[]> getSeriesFromPerson(String personId) {
        ArrayList<String[]> seriesFromPerson = new ArrayList<>();

        String queryString = queryPrefix +
                "SELECT DISTINCT ?seriesId ?seriesRating ?seriesTitle ?seriesImageURL\n" +
                "WHERE {\n" +
                "     ?series my:hasSeriesId ?seriesId .\n" +
                "     ?series my:hasRating ?seriesRating .\n" +
                "     ?series my:hasTitle ?seriesTitle .\n" +
                "     OPTIONAL {?series my:hasSeriesImageURL ?seriesImageURL . }\n" +
                "     ?person my:hasPersonId ?personId FILTER regex(?personId, '" + personId + "') .\n" +
                "     { ?series my:hasActor ?person . } UNION { ?series my:hasCreator ?person . }\n" +
                "}";

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet results = qExe.execSelect();

        while (results.hasNext()) {
            QuerySolution result = results.next();
            RDFNode seriesIdNode = result.get("?seriesId");
            RDFNode seriesRatingNode = result.get("?seriesRating");
            RDFNode seriesTitleNode = result.get("?seriesTitle");
            RDFNode seriesImageURLNode = result.get("?seriesImageURL");

            if (seriesIdNode != null && seriesIdNode.isLiteral() && seriesRatingNode != null &&
                    seriesRatingNode.isLiteral() && seriesTitleNode != null && seriesTitleNode.isLiteral()) {
                Literal seriesIdLiteral = seriesIdNode.asLiteral();
                Literal seriesRatingLiteral = seriesRatingNode.asLiteral();
                Literal seriesTitleLiteral = seriesTitleNode.asLiteral();

                String[] temp = new String[4];
                temp[0] = seriesIdLiteral.getString();
                temp[1] = String.valueOf(seriesRatingLiteral.getDouble());
                temp[2] = seriesTitleLiteral.getString();
                temp[3] = "";
                if (seriesImageURLNode != null && seriesImageURLNode.isLiteral()) {
                    temp[3] = seriesImageURLNode.asLiteral().getString();
                }
                seriesFromPerson.add(temp);
            }
        }

        return seriesFromPerson;
    }

    private void addToPeopleSeriesFrequency(ArrayList<SeriesFromPeopleFrequency> result, String seriesId,
                                            String seriesName, String seriesImageURL , double seriesRating) {

        for (SeriesFromPeopleFrequency current : result) {
            if (current.getSeriesId().equals(seriesId)) {
                current.increaseFrequency();
                return;
            }
        }

        result.add(new SeriesFromPeopleFrequency(seriesId, seriesRating, seriesName, seriesImageURL));
    }

    private void handleEmptyRecommendation(ArrayList<String> lastChecked, OperationResult operationResult,
                                           int remainingSeries, int remainingPeople) {

        if ( (remainingSeries + remainingPeople) == 0) {
            return ;
        }

        // Sacar as 10 séries mais populares; Random para 6 séries e das outras 4 ir sacar actor random

        //================================================ Series =====================================================

        // Get "remainingSeries" (6 by default) most popular series that have not been seen by the user
        ArrayList<String[]> mostPopularSeries = getMostPopularSeriesNotSeen(lastChecked);

        int i = 0;
        int count = 0;
        while (i < mostPopularSeries.size() && count < remainingSeries) {
            boolean result = operationResult.addSeries(mostPopularSeries.get(i));
            if (result) {
                count++;
            }
            i++;
        }

        //================================================ Actors =====================================================

        if (i >= mostPopularSeries.size()) {
            // We have no more series that the user hasn't seen -- Go back to the top and we will pick the actors from
            // there
            i = 0;
        }

        // Get "remainingPeople" (4 by default) from the next "remainingPeople" most rated series that the user has not
        // yet seen (do not forget that, if we entered in the previous if statement then we will be doing this in the
        // "remainingPeople" most rated series, but that's life, we have to search somewhere)
        count = 0;
        ArrayList<String> seriesIds = new ArrayList<>();
        while (i < mostPopularSeries.size() && count < remainingPeople) {
            seriesIds.add(mostPopularSeries.get(i)[1]);
            count++;
            i++;
        }

        // Get random people from this list of series
        getRandomPeopleFromSeries(operationResult, seriesIds, remainingPeople);
    }

    private void getRandomPeopleFromSeries(OperationResult operationResult, ArrayList<String> seriesIds,
                                           int remainingPeople) {

        // Get a list of people in the mentioned series
        int currentNumberPeople = 0;
        while (currentNumberPeople < remainingPeople) {
            String[] temp = getRandomPersonFromSeries(seriesIds);

            boolean result = operationResult.addPerson(temp);
            if (result) {
                currentNumberPeople++;
            }
        }

    }

    private String[] getRandomPersonFromSeries(ArrayList<String> seriesIds) {

        OntProperty hasTotalNumberOfPeople = ontologyModel.getDatatypeProperty(namespace + "hasTotalNumberOfPeople");

        String queryString = queryPrefix +
                "SELECT ?creatorName ?creatorId ?creatorImageURL ?actorName ?actorId ?actorImageURL \n" +
                "WHERE {\n";

        boolean firstTime = true;
        int partialNumberOfPeople = 0;
        for (String id : seriesIds) {

            Individual currentSeries = seriesList.get(id);
            RDFNode hasTotalNumberOfPeopleNode = currentSeries.getPropertyValue(hasTotalNumberOfPeople);
            if (hasTotalNumberOfPeopleNode.isLiteral()) {
                Literal hasTotalNumberOfPeopleLiteral = hasTotalNumberOfPeopleNode.asLiteral();
                partialNumberOfPeople += hasTotalNumberOfPeopleLiteral.getInt();
            }

            if (!firstTime) {
                queryString += "     UNION\n";
            } else {
                firstTime = false;
            }

            // Filter initial id with 0
            String topId = id.substring(0, 2);
            String remainingId = id.substring(2);
            while (remainingId.charAt(0) == '0') {
                remainingId = remainingId.substring(1);
            }

            queryString +=
                    "     {\n" +
                    "          ?subject my:hasSeriesId ?id FILTER( regex(?id, '" + topId + remainingId + "') ) .\n" +
                    "          {\n" +
                    "                    ?subject my:hasActor ?actor .\n" +
                    "                    ?actor my:hasPersonId ?actorId .\n" +
                    "                    ?actor my:hasName ?actorName .\n" +
                    "                    OPTIONAL{ ?actor my:hasPersonImageURL ?actorImageURL . } \n" +
                    "          }\n" +
                    "          UNION \n" +
                    "          {\n" +
                    "                    ?subject my:hasCreator ?creator .\n" +
                    "                    ?creator my:hasName ?creatorName .\n" +
                    "                    ?creator my:hasPersonId ?creatorId .\n" +
                    "                    OPTIONAL{ ?creator my:hasPersonImageURL ?creatorImageURL . } \n" +
                    "           } .\n" +
                    "     }\n";

        }


        Random random = new Random();
        queryString += "} OFFSET " + random.nextInt(partialNumberOfPeople) + " LIMIT 1";


        /**
         * Correr a query, depois para o resultado que der (só vai dar uma linha) ver se temos actor ou criador
         * (nos 3 campos) e no que houver extrair a informação
         */
        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet results = qExe.execSelect();

        if (results.hasNext()) {
            QuerySolution result = results.next();
            RDFNode creatorNameNode = result.get("?creatorName");
            RDFNode creatorIdNode = result.get("?creatorId");
            RDFNode creatorImageNode = result.get("?creatorImageURL");
            RDFNode actorNameNode = result.get("?actorName");
            RDFNode actorIdNode = result.get("?actorId");
            RDFNode actorImageNode = result.get("?actorImageURL");

            if (creatorNameNode != null && creatorNameNode.isLiteral() &&
                    creatorIdNode != null && creatorIdNode.isLiteral()) {
                return processPerson(creatorNameNode, creatorIdNode, creatorImageNode);
            } else {
                return processPerson(actorNameNode, actorIdNode, actorImageNode);
            }
        }

        return null;
    }

    private String[] processPerson(RDFNode nameNode, RDFNode idNode, RDFNode imageNode) {

        String[] temp = new String[3];

        Literal nameLiteral = nameNode.asLiteral();
        Literal idLiteral = idNode.asLiteral();

        temp[0] = nameLiteral.getString();
        temp[1] = idLiteral.getString();
        temp[2] = "";

        if (imageNode != null && imageNode.isLiteral()) {
            Literal imageURLLiteral = imageNode.asLiteral();
            temp[2] = imageURLLiteral.getString();
        }
        return temp;
    }

    private ArrayList<String[]> getMostPopularSeriesNotSeen(ArrayList<String> lastChecked) {
        ArrayList<String[]> popularSeries = new ArrayList<>();

        String queryString = queryPrefix +
                "SELECT DISTINCT ?seriesTitle ?seriesID ?imageURL ?numberPeople \n" +
                "WHERE {\n" +
                "     ?series my:hasSeriesId ?seriesID .\n" +
                "     ?series my:hasTitle ?seriesTitle .\n" +
                "     ?series my:hasSeriesImageURL ?imageURL .\n" +
                "     ?series my:hasRating ?seriesRating .\n" +
                "     ?series my:hasTotalNumberOfPeople ?numberPeople .\n" +
                "} ORDER BY DESC(?seriesRating)";

        Query queryObject = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.create(queryObject, ontologyModel);
        ResultSet results = qExe.execSelect();

        while (results.hasNext()) {
            QuerySolution result = results.next();
            RDFNode seriesTitleNode = result.get("?seriesTitle");
            RDFNode seriesIdNode = result.get("?seriesID");
            RDFNode seriesImageNode = result.get("?imageURL");
            String[] temp = new String[3];

            if (seriesTitleNode.isLiteral() && seriesIdNode.isLiteral()) {
                Literal seriesTitle = seriesTitleNode.asLiteral();
                String series_title = seriesTitle.getString();

                Literal seriesId = seriesIdNode.asLiteral();
                String series_id = seriesId.getString().toLowerCase();

                if (lastChecked.contains(series_id)){
                    // Series already seen
                    continue;
                }

                String image_url = "";
                if (seriesImageNode != null && seriesImageNode.isLiteral()) {
                    Literal imageUrl = seriesImageNode.asLiteral();
                    image_url = imageUrl.getString();
                }

                temp[0] = series_title;
                temp[1] = series_id;
                temp[2] = image_url;

                popularSeries.add(temp);
            }
        }

        return popularSeries;
    }
}
