package IMDBCrawler;

import data.Genre;
import data.Person;
import data.Series;
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
     *
     * @param map list of series name and IMDB ids
     */
    public Crawler(HashMap<String, String> map) {
        genreList = new ArrayList<>();
        fileNameMap = map;
    }

    public void getIMDBdata() {
        String mainURL = "http://www.imdb.com/title/tt";
        String seriesURL;
        String auxSeriesURL;
        Series series;
        String title = "";
        String description;
        ArrayList<Person> creatorList;
        ArrayList<Person> actorList;
        String seasonNumber;
        String yearStart = "";
        String yearFinished = "";
        boolean hasFinished = false;
        Document doc;

        for (Map.Entry<String, String> entry : fileNameMap.entrySet()) {

            series = new Series(entry.getValue());
            seriesURL = mainURL + entry.getValue();
            System.out.printf("Name : %s ID: %s URL: %s %n", entry.getKey(), entry.getValue(), seriesURL);

            //get series title, description, start and end year
            try {
                doc = Jsoup.connect(seriesURL).userAgent("Mozilla").get();

                this.getTitleAndDate(doc, series);
                this.getDescription(doc, series);
            } catch (IOException var12) {
                System.out.println("Timeout while connecting to :" + seriesURL + "!");
            }

            // get series storyline
            auxSeriesURL = seriesURL + "/plotsummary";
            this.getStoryline(auxSeriesURL, series);

            System.out.println("Title: " + series.getTitle() + " start: " + series.getStartYear() + " finished: "
                    + series.getFinishYear() + " Description: " + series.getDescription() + " Storyline: " + series.getStoryline());

           break;
        }
    }

    public void getTitleAndDate(Document doc, Series series) {
        Elements el = doc.select("meta[name=title]");
        String content = el.attr("content");
        String[] aux = content.split(" ");

        boolean isTitle = true;
        boolean isDate = false;
        String title = "";
        String date = "";
        for (int i = 0; i < aux.length; i++) {

            if (isTitle) {
                if (aux[i].equals("(TV"))
                    isTitle = false;
                else
                    title += aux[i] + " ";
            } else {
                if (aux[i].equals("Series"))
                    isDate = true;

                else if (isDate) {
                    date += aux[i];
                    isDate = false;
                }
            }
        }

        series.setTitle(title);
        series.setStartYear(date.substring(0, 4));
        if (date.length() > 5)
            series.setFinishYear(date.substring(5, 9));

        return;
    }

    public void getDescription(Document doc, Series series) {
        Elements el = doc.select("meta[name=description]");
        String content = el.attr("content");
        String[] aux = content.split("\\.");

        series.setDescription(aux[aux.length-1] + ".");
    }

    public void getStoryline(String url, Series series) {
        Document doc;
        String storyline;

        try {
            doc = Jsoup.connect(url).userAgent("Mozilla").get();

            Elements el = doc.getElementsByClass("plotSummary").select("*");


            storyline = el.get(0).toString();
            storyline = storyline.replace("<p class=\"plotSummary\"> ", "");
            storyline = storyline.replace("</p>", "");
            series.setStoryline(storyline);
        } catch (IOException var12) {
            System.out.println("Timeout while connecting to :" + url + "!");
        }

    }
}
