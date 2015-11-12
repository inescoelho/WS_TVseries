package IMDBCrawler;

/**
 * Created by user on 08/11/2015.
 */
public class Main {

    public static void main(String[] args)
    {
        FileLoader fileLoader = new FileLoader();
        Crawler crawler = new Crawler(fileLoader.getFileNameMap());

        boolean result = fileLoader.loadIDsFromFile("TV_Show_Series.csv");

        if (result) {
            System.out.println("Successfully loaded ids from file!");
        } else {
            System.out.println("Failed to load ids from file!");
            return;
        }

        //crawler.getIMDBdata();
        String name = "Segurança Nacional (TV Series 2011– ) - IMDb";
        String[] aux = name.split(" ");
        for (int i = 0; i < aux.length; i++)
        {

        }
    }
}
