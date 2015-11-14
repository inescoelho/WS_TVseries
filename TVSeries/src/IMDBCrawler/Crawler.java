package IMDBCrawler;

import data.Genre;
import data.Person;
import data.Series;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Crawls the IMDB website in order to retrieve information for each serie on the hashmap
 */
public class Crawler {
    private ArrayList<Genre> genreList;
    private HashMap<String, String> fileNameMap;

    /**
     * Class constructor
     *
     * @param map list of series name and IMDB ids
     */
    public Crawler(HashMap<String, String> map) {
        setGenreList(new ArrayList<>());
        setFileNameMap(map);
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

        for (Map.Entry<String, String> entry : getFileNameMap().entrySet()) {

            series = new Series(entry.getValue());
            seriesURL = mainURL + entry.getValue();
            System.out.printf("Name : %s ID: %s URL: %s %n", entry.getKey(), entry.getValue(), seriesURL);

            try {
                doc = Jsoup.connect(seriesURL).userAgent("Mozilla").get();

                //get series genre
                this.getGenre(doc, series);

                //get series title, description, start and end year
                this.getTitleAndDate(doc, series);
                this.getDescription(doc, series);

                // get series storyline
                auxSeriesURL = seriesURL + "/plotsummary";
                this.getStoryline(auxSeriesURL, series);

                // get series duration
                auxSeriesURL = seriesURL + "/technical";
                this.getDuration(auxSeriesURL, series);

            } catch (IOException var12) {
                System.out.println("Timeout while connecting to :" + seriesURL + "!");
            }

            //System.out.println(series.toString());

           //break;
        }

        //list series by genre
        for (Genre genre: this.getGenreList()
             ) {
            System.out.println(genre.toString());
        }
    }

    public void getGenre(Document doc, Series series){
        String genre;

        Elements list = doc.select("div[itemprop=genre]");
        Elements links = list.select("a");

        for (Element link : links)
        {
            genre = link.toString();

            //remove <a> tag
            int start=0, stop=0;
            for (int i=0; i< genre.length()-1; i++)
            {
                start = genre.lastIndexOf('<');
                stop = genre.indexOf('>');
            }
            genre = genre.substring(stop+1, start);

            //add series to the respective genre
            addSeriesToGenre(genre, series);
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

    private void getDuration(String url, Series series) {
        Document doc;
        String duration;

        try {
            doc = Jsoup.connect(url).userAgent("Mozilla").get();

            Element table = doc.select("table").get(0);
            Element row = table.select("tr").get(0);
            Element column = row.select("td").get(1);

            duration = column.toString();
            duration = duration.replace("<td>", "");
            duration = duration.replace("</td>", "");
            series.setDuration(duration);
        } catch (IOException var12) {
            System.out.println("Timeout while connecting to :" + url + "!");
        }
    }

    /**
     * list of series genres, each genre contains a list with series that are categorized as such
     */
    public ArrayList<Genre> getGenreList() {
        return genreList;
    }

    public void setGenreList(ArrayList<Genre> genreList) {
        this.genreList = genreList;
    }

    /**
     * Map the series name with the respective IMDB id
     */
    public HashMap<String, String> getFileNameMap() {
        return fileNameMap;
    }

    public void setFileNameMap(HashMap<String, String> fileNameMap) {
        this.fileNameMap = fileNameMap;
    }

    private void addSeriesToGenre (String genre, Series series)
    {
        ArrayList<Genre> list = this.getGenreList();
        boolean result = false;

        //check if genre is on the list
        for(Genre genreInList : list)
        {
            //add serie to genre
            if (genreInList.getType().equals(genre))
            {
                genreInList.getSeries().add(series);
                result = true;
                break;
            }

        }

        //create new genre and add series to the new genre series list
        if(!result)
        {
            Genre newGenre = new Genre(genre);
            newGenre.getSeries().add(series);
            list.add(newGenre);
        }

        return;
    }
}
