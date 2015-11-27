package server.data.ontology;

import java.util.ArrayList;

/**
 * Class that stores all the relevant information of a given Series to be displayed in the web interface
 */
public class Series {

    private String id;
    private String title;
    private String description;
    private String storyline;
    // FIXME: private String imageURL; -- Optional
    private int episodeDuration;
    private int pilotYear; // -1 if no information is provided!!!
    private int finishYear; // -1 if no information is provided!!!
    // FIXME: private int impactFactor; -- Mandatory
    private ArrayList<String[]> actors; // first position has name, second has id and third has imageURL
    private ArrayList<String[]> creators; // first position has name, second has id and third has imageURL

    public Series(String title, String description, String storyline, String id, int episodeDuration, int pilotYear,
                  int finishYear, ArrayList<String[]> actors, ArrayList<String[]> creators) {
        this.title = title;
        this.description = description;
        this.storyline = storyline;
        this.episodeDuration = episodeDuration;
        this.pilotYear = pilotYear;
        this.finishYear = finishYear;
        this.id = id;
        this.actors = actors;
        this.creators = creators;
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
}
