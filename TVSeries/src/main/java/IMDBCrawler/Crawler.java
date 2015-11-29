package IMDBCrawler;

import data.Genre;
import data.Person;
import data.Series;
import data.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Crawls the IMDB website in order to retrieve information for each serie on the hashmap
 */
public class Crawler {
    private HashMap<String, String> fileNameMap;
    private ArrayList<Series> seriesList;
    private ArrayList<Genre> genreList;
    private ArrayList<Person> peopleAdditionalInfo;
    private ArrayList<Series> seriesAdditionalInfo;

    /**
     * Class constructor
     *
     * @param map list of series name and IMDB ids
     */
    public Crawler(HashMap<String, String> map) {
        setFileNameMap(map);
        setGenreList(new ArrayList<>());
        setSeriesList(new ArrayList<>());
        setPeopleAdditionalInfo(new ArrayList<>());
        setSeriesAdditionalInfo(new ArrayList<>());
    }

    public ArrayList<Series> getSeriesList() {
        return seriesList;
    }

    public void setSeriesList(ArrayList<Series> seriesList) {
        this.seriesList = seriesList;
    }

    public ArrayList<Person> getPeopleAdditionalInfo() {
        return peopleAdditionalInfo;
    }

    public void setPeopleAdditionalInfo(ArrayList<Person> peopleAdditionalInfo) {
        this.peopleAdditionalInfo = peopleAdditionalInfo;
    }

    public ArrayList<Series> getSeriesAdditionalInfo() {
        return seriesAdditionalInfo;
    }

    public void setSeriesAdditionalInfo(ArrayList<Series> seriesAdditionalInfo) {
        this.seriesAdditionalInfo = seriesAdditionalInfo;
    }

    public void getIMDBdata() {
        String mainURL = "http://www.imdb.com/title/tt";
        String seriesURL;
        String auxSeriesURL;
        Series series;
        Document doc;

        for (Map.Entry<String, String> entry : getFileNameMap().entrySet()) {

            series = new Series(entry.getValue());
            seriesURL = mainURL + entry.getValue();
            System.out.printf("Name : %s ID: %s URL: %s %n", entry.getKey(), entry.getValue(), seriesURL);

            boolean state = true;
            try {
                doc = Jsoup.connect(seriesURL).userAgent("Mozilla").get();

                //get series title, description, start and end year
                this.getTitleAndDate(doc, series);
                this.getDescription(doc, series);

                // get series storyline
                auxSeriesURL = seriesURL + "/plotsummary";
                state = this.getStoryline(auxSeriesURL, series);

                // get series duration
                if (state) {
                    auxSeriesURL = seriesURL + "/technical";
                    state = this.getDuration(auxSeriesURL, series);
                }

                // get series creators list
                if (state) {
                    auxSeriesURL = seriesURL + "/fullcredits";
                    state = this.getCreators(auxSeriesURL, series);
                }

                // get series actors list
                if (state) {
                    try {
                        state = this.getActors(auxSeriesURL, series);
                    } catch(Exception e) {
                        e.printStackTrace();
                        writeToFile(series);
                        continue;
                    }
                }

                //get series genre
                if (state)
                {
                    this.getGenre(doc, series);
                }
            } catch (IOException var12) {
                System.out.println("Timeout while connecting to :" + seriesURL + "!");
                state = false;
            }

            //add series to list of series
            if (state)
            {
                seriesList.add(series);
            }
            else
            {
                System.out.println("OUT " + series.getTitle());
                writeToFile(series);
            }
            //break;
        }
    }

    private void getGenre(Document doc, Series series){
        String genre;

        Elements list = doc.select("div[itemprop=genre]");
        Elements links = list.select("a");

        for (Element link : links)
        {
            genre = link.toString();

            //remove <a> tag
            int start = genre.lastIndexOf('<');
            int stop = genre.indexOf('>');
            genre = genre.substring(stop+1, start);

            //add series to the respective genre
            addSeriesToGenre(genre, series);
        }
    }

    private void getTitleAndDate(Document doc, Series series) {
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
    }

    private void getDescription(Document doc, Series series) {
        Elements el = doc.select("meta[name=description]");
        String content = el.attr("content");
        String[] aux = content.split("\\.");

        series.setDescription(aux[aux.length-1] + ".");
    }

    private boolean getStoryline(String url, Series series) {
        Document doc;
        String storyline;
        boolean state = true;

        try {
            doc = Jsoup.connect(url).userAgent("Mozilla").get();

            Elements el = doc.getElementsByClass("plotSummary").select("*");

            storyline = el.get(0).toString();
            storyline = storyline.replace("<p class=\"plotSummary\"> ", "");
            storyline = storyline.replace("</p>", "");
            series.setStoryline(storyline);
        } catch (IOException var12) {
            System.out.println("Timeout while connecting to :" + url + "!");
            state = false;
        }

        return state;
    }

    private boolean getDuration(String url, Series series) {
        Document doc;
        String duration;
        boolean state = true;

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
            state = false;
        }

        return state;
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
                // Add genre to series
                series.addGenre(genreInList);
                result = true;
                break;
            }
        }

        //create new genre and add series to the new genre series list
        if(!result)
        {
            Genre newGenre = new Genre(genre);
            newGenre.getSeries().add(series);
            // Add genre to series
            series.addGenre(newGenre);
            list.add(newGenre);
        }
    }

    private boolean getCreators(String auxSeriesURL, Series series) {
        Document doc;
        boolean state = true;

        try {
            doc = Jsoup.connect(auxSeriesURL).userAgent("Mozilla").get();

            Element table = doc.select("table").get(1);
            Elements rows = table.select("tr");
            //System.out.println(rows);

            for (Element row: rows)
            {
                Elements columns = row.select("td");

                boolean isCreator = false;
                for (Element column: columns)
                {
                    String aux = column.toString();
                    if (aux.contains("creator"))
                        isCreator = true;
                }

                if (isCreator)
                {
                    Element creatorRef = row.select("td").get(0);

                    //get IMDB id
                    String absHref = (creatorRef.select("a")).attr("href");
                    String id = absHref.substring(8,15);

                    //get name
                    String creator = creatorRef.toString();
                    //remove unwanted tags
                    creator = creator.replace(" </a> </td>", "");
                    int stop= creator.lastIndexOf('>');
                    creator = creator.substring(stop+2);

                    Person person = new Person(creator, id);
                    state = this.getPersonData(id, person);

                    if (state) {
                        //System.out.println(person.toString());
                        series.getCreatorList().add(person);
                    }
                    else
                        return false;
                }
            }

        } catch (IOException var12) {
            System.out.println("Timeout while connecting to :" + auxSeriesURL + "!");
            state = false;
        }
        return state;
    }

    private boolean getPersonData(String id, Person person) {
        Document doc;
        String url = "http://www.imdb.com/name/nm" + id + "/bio";
        Date birthday = null;
        String bio;
        boolean state = true;

        try {
            doc = Jsoup.connect(url).userAgent("Mozilla").get();

            //get birthday
            Elements table = doc.select("table#overviewTable");
            Elements row = table.select("tr");
            Elements column = row.select("td");
            if(column.size() > 0)
                birthday = this.convertToDate(column.toString());
            person.setBirthday(birthday);
            //System.out.println(birthday);

            //get bio
            Elements el = doc.select("h4.li_group");
            for(Element elAux : el)
            {
                if (elAux.toString().contains("Mini Bio"))
                {
                    Element el2 = elAux.nextElementSibling();
                    bio = el2.toString();
                    bio = bio.replace("\n", "");

                    boolean existTags = true;
                    while(existTags)
                    {
                        int start = bio.indexOf('<');
                        int stop = bio.indexOf('>');
                        if (start == -1 && stop == -1)
                            existTags = false;
                        else
                            bio = bio.replace(bio.substring(start, stop+1), "");
                    }
                    person.setBiography(bio);
                   // System.out.println(bio);
                }
            }

        } catch (IOException var12) {
            System.out.println("Timeout while connecting to :" + url + "!");
            state = false;
        }

        return state;
    }

    private Date convertToDate(String info) {
        String date;
        String day;
        String month;
        String year;
        Date birthday = null;

        if (info.contains("birth_monthday") && info.contains("birth_year"))
        {
            //clean first tag
            info = info.replace("<td class=\"label\">Date of Birth</td>\n<td> ", "");
            //remove second tag
            int stop = info.indexOf('>') + 1;
            info = info.substring(stop);

            //get day and month
            stop = info.indexOf('<');
            date = info.substring(0, stop);
            date = date.replace("&nbsp;", " ");
            String[] dayMonth = date.split(" ");
            day = dayMonth[0];
            month = dayMonth[1];

            //clean other tags
            info = info.replace("</a>", "");
            stop = info.indexOf('>') + 1;
            info = info.substring(stop);

            //get year
            year = info.substring(0, 4);
            //System.out.println(year);

            birthday = new Date(day, month, year);
        }

        return birthday;
    }

    private boolean getActors(String auxSeriesURL, Series series)  throws Exception {
        Document doc;
        boolean state = true;

        try {
            doc = Jsoup.connect(auxSeriesURL).userAgent("Mozilla").get();

            Element table = doc.select("table").get(2);
            Elements rows = table.select("tr");
            rows.remove(0);
            //System.out.println(rows);

            for (Element row: rows)
            {

                Elements columns = row.select("td");
                Element column = columns.get(0);
                //System.out.println(column);

                if (column.toString().contains("castlist_label"))
                    break;

                Elements href = column.select("a");
                String id = href.toString();
                id = id.substring(17,24);
                //System.out.println(id);

                Elements img = column.select("img");
                String name = img.attr("title");
                //System.out.println(name);

                Person person = new Person(name, id);
                this.getPersonData(id, person);

                //System.out.println(person.toString());
                series.getActorList().add(person);
            }

        } catch (IOException var12) {
            System.out.println("Timeout while connecting to :" + auxSeriesURL + "!");
            state = false;
        }

        return state;
    }

    private void writeToFile(Series series) {
        boolean result;
        try {
            // Write to file
            File file = new File("RemovedSeries.txt");
            if (!file.exists()) {
                result = file.createNewFile();
                if (!result) {
                    System.out.println("Failed to create log file! Message = Removed Series " + series.getTitle());
                }
            }

            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Removed Series " + series.getTitle() + "\n");
            bufferedWriter.close();
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Crawls IMDB site in order to get series scores and images from the series in the TV_Show_Series.csv file
     */
    public void getIMDBSeriesAdditionaldata() {
        String mainURL = "http://www.imdb.com/title/tt";
        String seriesURL;
        Series series;
        Document doc;

        for (Map.Entry<String, String> entry : getFileNameMap().entrySet()) {

            series = new Series(entry.getValue());
            seriesURL = mainURL + entry.getValue();
            series.setTitle(entry.getKey());
            //System.out.printf("Name : %s ID: %s URL: %s %n", entry.getKey(), entry.getValue(), seriesURL);

            try {
                doc = Jsoup.connect(seriesURL).userAgent("Mozilla").get();

                //get score
                Elements el = doc.select("span[itemprop = ratingValue]");
                String score = el.toString();
                //remove unwanted tags
                score = score.replace("</span>", "");
                int stop= score.lastIndexOf('>');
                score = score.substring(stop+1);
                series.setScore(Float.parseFloat(score));

                //get images
                Element el1 = doc.select("div.image").get(0).select("a").first().select("img").first();
                String url = el1.absUrl("src");
                series.setImage(url);

                getSeriesAdditionalInfo().add(series);
            }
            catch (IOException var12) {
                System.out.println("Timeout while connecting to :" + seriesURL + "!");
                writeToFile(series);
            }

            //break;
        }
    }


    /**
     * Crawls IMDB site in order to get additional information about actors and directors in the People_Data.csv
     */
    public void getIMDBPeopleAdditionaldata() {
        String url;
        Person person;
        Document doc;

        for (Map.Entry<String, String> entry : getFileNameMap().entrySet()) {
            String id = entry.getValue().replace("\"", "");
            String name = entry.getKey().replace("\"", "");
            url = "http://www.imdb.com/name/" + id + "/bio";
            person = new Person(name, id);
            //System.out.println(url);

            try {
                doc = Jsoup.connect(url).userAgent("Mozilla").get();

                //get images
                Elements images = doc.select("div.image");
                if (images.size()>0)
                {
                    Element link = images.get(0).select("a").first().select("img").first();
                    String image_url = link.absUrl("src");
                    person.setImage(image_url);
                    peopleAdditionalInfo.add(person);
                }
                else
                {
                    Elements image = doc.select("img[itemprop = image]").select("img");

                    if (image.size()!=0)
                    {
                        Element link = image.first();
                        String image_url = link.absUrl("src");
                        person.setImage(image_url);
                        peopleAdditionalInfo.add(person);
                    }
                }

                //System.out.println(person.toString());
            }
            catch (IOException var12) {
                System.out.println("Timeout while connecting to :" + url + "!");
            }
        }
    }
}

