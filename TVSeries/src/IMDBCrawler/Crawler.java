package IMDBCrawler;

import data.Genre;
import data.Person;
import data.Serie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Crawls the IMDB website in order to retrieve information for each serie on the hashmap
 */
public class Crawler {
    /**
     * list of series genres, each genre contains a list with series that are categorized as such
     */
    private ArrayList<Genre> genreList;
    /**
     * Map the series name with the respective IMDB id
     */
    private HashMap<String, String> fileNameMap;

    /**
     * Class constructor
     * @param map list of series name and IMDB ids
     */
    public Crawler(HashMap<String, String> map) {
        genreList = new ArrayList<>();
        fileNameMap = map;
    }

    public void getIMDBdata()
    {
        String mainURL = "http://www.imdb.com/title/tt";
        String serieURL;
        String title = "";
        String description;
        ArrayList<Person> creatorList;
        ArrayList<Person> actorList;
        String seasonNumber;
        String yearStart = "";
        String yearFinished = "";
        boolean hasFinished = false;


        Document doc;
        for (Map.Entry<String, String> entry : fileNameMap.entrySet())
        {
            serieURL = mainURL + entry.getValue();
            System.out.printf("Name : %s ID: %s URL: %s %n", entry.getKey(), entry.getValue(), serieURL);

            try {
                System.out.println(">>>\n>>>FETCHING " + serieURL + "\n>>>");
                doc = Jsoup.connect(serieURL).userAgent("Mozilla").get();

                Serie serie = new Serie();
                this.getTitleAndDate(doc, serie);

                System.out.println("Title: " + serie.getTitle() + " start: " + serie.getStartYear() + " finished: "
                        + serie.getFinishYear()  + " Has Finished: " + serie.isHasFinished());

            } catch (IOException var12) {
                System.out.println("Timeout while connecting to :" + serieURL + "!");
            }

            //break;
        }
    }

    public void getTitleAndDate(Document doc, Serie serie)
    {
        Elements el = doc.select("meta[name=title]");
        String content = el.attr("content");
        String[] aux = content.split(" ");

        boolean isTitle = true;
        boolean isDate = false;
        String title = "";
        String date = "";
        for (int i = 0; i < aux.length; i++)
        {

            if (isTitle)
            {
                if (aux[i].equals("(TV"))
                    isTitle = false;
                else
                    title += aux[i] + " ";
            }
            else
            {
                if (aux[i].equals("Series"))
                    isDate = true;

                else if (isDate)
                {
                    date += aux[i];
                    isDate = false;
                }
            }
        }

        serie.setTitle(title);
        serie.setStartYear(date.substring(0,4));

        if (date.length()>5)
        {
            serie.setFinishYear(date.substring(5,9));
            serie.setHasFinished(true);
        }
        else
            serie.setHasFinished(false);

        return;
    }
}
