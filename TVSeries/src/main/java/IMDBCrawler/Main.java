package IMDBCrawler;

import data.Person;
import data.Series;
import ontology.OntologyCreator;
import org.apache.log4j.varia.NullAppender;

import java.util.ArrayList;

/**
 * Loads the list of tv series to analyze and starts the IMDB crawler on that list
 */
public class Main {

    public static void main(String[] args)
    {
        // Configure log4j (in our case we just ignore it) because of Jena!
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());

        FileLoader fileLoader = new FileLoader();
        Crawler crawler = new Crawler(fileLoader.getFileNameMap());
        OntologyCreator ontologyCreator = new OntologyCreator("tv_series_ontology.rdf", "RDF/XML");

        //load tv series list from cvs file
        boolean result = fileLoader.loadIDsFromFile("TV_Show_Series.csv");

        if (result) {
            System.out.println("Successfully loaded ids from file!");
        } else {
            System.out.println("Failed to load ids from file!");
            Thread.dumpStack();
            return;
        }

        //get data from IMDB
        crawler.getIMDBdata();

        ArrayList<Series> series = crawler.getSeriesList();

        for (Series currentSeries : series) {
            // Add series
            ontologyCreator.createSeries(currentSeries);

            // Add creator to series
            ArrayList<Person> creators = currentSeries.getCreatorList();
            for (Person currentCreator : creators) {

                // First add creator to the list of people
                if (!ontologyCreator.checkPerson(currentCreator)) {
                    // Add creator
                    ontologyCreator.createCreator(currentCreator);
                }

                // Link the creator with the series
                result = ontologyCreator.addSeriesToCreator(currentSeries, currentCreator);
                if (!result) {
                    Thread.dumpStack();
                    return ;
                }
            }

            // Add actor to series
            ArrayList<Person> actors = currentSeries.getActorList();
            for (Person currentActor : actors) {

                // First check if the person is already in our list
                if (!ontologyCreator.checkPerson(currentActor)) {
                    // Add actor
                    ontologyCreator.createActor(currentActor);
                } else {
                    // If person already in our list check if is a creator
                    if (ontologyCreator.checkCreator(currentActor)) {
                        // Make creator actor
                        result = ontologyCreator.makeCreatorActor(currentActor);
                        if (!result) {
                            Thread.dumpStack();
                            return ;
                        }
                    }
                }

                // Link the actor with the series
                result = ontologyCreator.addSeriesToActor(currentSeries, currentActor);
                if (!result) {
                    Thread.dumpStack();
                    return ;
                }
            }

        }

        ontologyCreator.writeModelToFile("MyTest.rdf", "RDF/XML");
    }
}
