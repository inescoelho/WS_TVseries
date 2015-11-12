package IMDBCrawler;

import data.Genre;
import data.Person;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by user on 09/11/2015.
 */
public class Crawler {
    private ArrayList<Genre> genreList;
    private HashMap<String, String> fileNameMap;

    public Crawler(HashMap<String, String> map) {
        genreList = new ArrayList<>();
        fileNameMap = map;
    }

    public void getIMDBdata()
    {
        String mainURL = "http://www.imdb.com/title/tt";
        String serieURL;
        String title;
        String description;
        ArrayList<Person> creatorList;
        ArrayList<Person> actorList;
        String seasonNumber;
        String pilotDate;
        boolean hasFinished;


        Document doc;
        Elements aux;
        for (Map.Entry<String, String> entry : fileNameMap.entrySet())
        {
            serieURL = mainURL + entry.getValue();
            System.out.printf("Name : %s ID: %s URL: %s %n", entry.getKey(), entry.getValue(), serieURL);

            try {
                System.out.println(">>>\n>>>FETCHING " + serieURL + "\n>>>");
                doc = Jsoup.connect(serieURL).userAgent("Mozilla").get();

                title = this.getTitle(doc);

                System.out.print(title);

            } catch (IOException var12) {
                System.out.println("Timeout while connecting to :" + serieURL + "!");
            }

            break;
        }
    }

    public String getTitle(Document doc)
    {
        Elements el = doc.select("meta[name=title]");
        String aux = el.attr("content");


        System.out.println(aux);

        StringTokenizer st = new StringTokenizer(aux, " (TV Series ");

        System.out.println(st.nextElement());
        System.out.println(st.nextElement());

        return aux;
    }
}
