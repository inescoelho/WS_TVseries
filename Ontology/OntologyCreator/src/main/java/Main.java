import loader.FileLoader;
import ontology.OntologyCreator;
import org.apache.log4j.varia.NullAppender;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // Configure log4j (in our case we just ignore it) because of Jena!
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());

        try{
            testOntology();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void testOntology() throws ParseException {
        OntologyCreator ontologyCreator = new OntologyCreator("tv_series_ontology.rdf", "RDF/XML");

        ArrayList<String> genres = new ArrayList<>();
        genres.add("Comedy");

        // Create series, acotr and creators
        ontologyCreator.createSeries("The Big Bang Theory", "TV Show about physicists", false, 20, 9, 2007, genres);
        ontologyCreator.createActor("Jim Parsons", "Major star in The Big Bang Theory", "24/03/1973");
        ontologyCreator.createCreator("Chuck Lorre", "Co-creator of The Big Bang Theory", "18/10/1952");
        ontologyCreator.createCreator("Bill Prady", "Co-creator of The Big Bang Theory", "07/06/1960");

        // Add series to cast
        boolean result = ontologyCreator.addSeriesToActor("The Big Bang Theory", "Jim Parsons");
        System.out.println("Added series to actor? " + result);

        // Add series to creators
        result = ontologyCreator.addSeriesToCreator("The Big Bang Theory", "Chuck Lorre");
        System.out.println("Added series to creator? " + result);
        result = ontologyCreator.addSeriesToCreator("The Big Bang Theory", "Bill Prady");
        System.out.println("Added series to creator? " + result);


        ontologyCreator.writeModelToFile("MyTest.rdf", "RDF/XML");
    }

    private static void testFileLoader() {
        FileLoader fileLoader = new FileLoader();

        boolean result = fileLoader.loadIDsFromFile("TV_Show_Series.csv");

        if (result) {
            System.out.println("Successfully loaded ids from file!");
        } else {
            System.out.println("Failed to load ids from file!");
        }
    }
}
