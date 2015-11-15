package IMDBCrawler;

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
            return;
        }

        //get data from IMDB
        crawler.getIMDBdata();

        ArrayList<Series> series = crawler.getSeriesList();

        for (Series currentSeries : series) {
            // Add series
            ontologyCreator.createSeries(currentSeries);
        }

        ontologyCreator.writeModelToFile("MyTest.rdf", "RDF/XML");
    }
}
