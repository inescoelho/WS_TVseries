package IMDBCrawler;

/**
 * Loads the list of tv series to analyze and starts the IMDB crawler on that list
 */
public class Main {

    public static void main(String[] args)
    {
        FileLoader fileLoader = new FileLoader();
        Crawler crawler = new Crawler(fileLoader.getFileNameMap());

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

    }
}
