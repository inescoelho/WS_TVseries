package ontology.data;

import java.util.ArrayList;

/**
 * Class that stores all the relevant information of a given Series to be displayed in the web interface
 */
public class Series {

    private String id;
    private String title;
    private String description;
    private String storyline;
    private String imageURL; // Empty string if no information if provided
    private int episodeDuration;
    private int pilotYear; // -1 if no information is provided!!!
    private int finishYear; // -1 if no information is provided!!!
    private double rating; // -1 if no information is provided!!!
    private ArrayList<String[]> actors; // first position has name, second has id and third has imageURL
    private ArrayList<String[]> creators; // first position has name, second has id and third has imageURL

    public Series(String title, String description, String storyline, String id, int episodeDuration, int pilotYear,
                  int finishYear, String imageURL, double rating, ArrayList<String[]> actors,
                  ArrayList<String[]> creators) {
        this.title = title;
        this.description = description;
        this.storyline = storyline;
        this.episodeDuration = episodeDuration;
        this.pilotYear = pilotYear;
        this.finishYear = finishYear;
        this.id = id;
        this.imageURL = imageURL;
        this.actors = actors;
        this.creators = creators;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoryline() {
        return storyline;
    }

    public void setStoryline(String storyline) {
        this.storyline = storyline;
    }

    public int getEpisodeDuration() {
        return episodeDuration;
    }

    public void setEpisodeDuration(int episodeDuration) {
        this.episodeDuration = episodeDuration;
    }

    public int getPilotYear() {
        return pilotYear;
    }

    public void setPilotYear(int pilotYear) {
        this.pilotYear = pilotYear;
    }

    public int getFinishYear() {
        return finishYear;
    }

    public void setFinishYear(int finishYear) {
        this.finishYear = finishYear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String[]> getActors() {
        return actors;
    }

    public void setActors(ArrayList<String[]> actors) {
        this.actors = actors;
    }

    public ArrayList<String[]> getCreators() {
        return creators;
    }

    public void setCreators(ArrayList<String[]> creators) {
        this.creators = creators;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
