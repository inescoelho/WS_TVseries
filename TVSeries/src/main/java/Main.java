import IMDBCrawler.Crawler;
import IMDBCrawler.FileLoader;
import data.Person;
import data.Series;
import ontology.OntologyCreator;
import org.apache.log4j.varia.NullAppender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
                    writeToLogFile("Error while adding series " + currentSeries.getTitle() + " to creator " +
                                   currentCreator.getName());
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
                        ontologyCreator.makeCreatorActor(currentActor);
                    }
                }

                // Link the actor with the series
                result = ontologyCreator.addSeriesToActor(currentSeries, currentActor);
                if (!result) {
                    writeToLogFile("Error while adding series " + currentSeries.getTitle() + " to actor " +
                                   currentActor.getName());
                }
            }

        }

        ontologyCreator.addWikiURLs();
        ontologyCreator.updateSeriesActors();

        ontologyCreator.writeModelToFile("tv_series_ontology_current.rdf", "RDF/XML");
    }

    private static void writeToLogFile(String message) {
        try {
            // Write to file
            File file = new File("MyLogFile.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(message + "\n");
            bufferedWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Series> getMoreIMDBSeriesInfo()
    {
        FileLoader fileLoader = new FileLoader();
        Crawler crawler = new Crawler(fileLoader.getFileNameMap());

        //load tv series list from cvs file
        boolean result = fileLoader.loadIDsFromFile("TV_Show_Series.csv");

        if (result) {
            System.out.println("Successfully loaded ids from file!");

            //get data from IMDB
            crawler.getIMDBSeriesAdditionaldata();
            return crawler.getSeriesAdditionalInfo();
        } else {
            System.out.println("Failed to load ids from series file!");
            Thread.dumpStack();
            return null;
        }
    }

    private static ArrayList<Person> getMoreIMDBPersonInfo()
    {
        FileLoader fileLoader = new FileLoader();
        Crawler crawler = new Crawler(fileLoader.getFileNameMap());

        //load tv series list from cvs file
        boolean result = fileLoader.loadIDsFromFile("People_Data.csv");

        if (result) {
            System.out.println("Successfully loaded ids from file!");

            //get data from IMDB
            crawler.getIMDBPeopleAdditionaldata();
            return crawler.getPeopleAdditionalInfo();
        } else {
            System.out.println("Failed to load ids from people file!");
            Thread.dumpStack();
            return null;
        }
    }
}
