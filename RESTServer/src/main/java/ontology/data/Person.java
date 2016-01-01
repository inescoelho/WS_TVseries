package ontology.data;

import java.util.ArrayList;

/**
 * Class that stores all the relevant information of a given Person to be displayed in the web interface
 */
public class Person {

    private String id;
    private String name;
    private String biography;
    private String birthDate;
    private String wikiURL;
    private String imageURL; // Empty string if no information is available
    private ArrayList<String[]> seriesActed; // contains id, name and image url of the series where the person acted
    private ArrayList<String[]> seriesCreated; // contains id, name and image url of the series created by the person

    public Person(String id, String name, String biography, String birthDate, String wikiURL, String imageURL,
                  ArrayList<String[]> seriesActed, ArrayList<String[]> seriesCreated) {
        this.id = id;
        this.name = name;
        this.biography = biography;
        this.birthDate = birthDate;
        this.wikiURL = wikiURL;
        this.imageURL = imageURL;
        this.seriesActed = seriesActed;
        this.seriesCreated = seriesCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getWikiURL() {
        return wikiURL;
    }

    public void setWikiURL(String wikiURL) {
        this.wikiURL = wikiURL;
    }

    public ArrayList<String[]> getSeriesActed() {
        return seriesActed;
    }

    public void setSeriesActed(ArrayList<String[]> seriesActed) {
        this.seriesActed = seriesActed;
    }

    public ArrayList<String[]> getSeriesCreated() {
        return seriesCreated;
    }

    public void setSeriesCreated(ArrayList<String[]> seriesCreated) {
        this.seriesCreated = seriesCreated;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
