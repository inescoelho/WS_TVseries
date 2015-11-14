package data;

import java.util.ArrayList;

/**
 * Created by user on 09/11/2015.
 */
public class Series {
    private String seriesId;
    private String title;
    private String description;
    private String storyline;
    private ArrayList<Person> creatorList;
    private ArrayList<Person> actorList;
    private String seasonNumber;
    private String startYear;
    private String finishYear;
    private String duration;

    public  Series(String id)
    {
        setSeriesId(id);
        title = "";
        description = "";
        setStoryline("");
        creatorList = new ArrayList<Person>();
        actorList = new ArrayList<Person>();
        seasonNumber = "";
        startYear = "";
        finishYear = "";
        setDuration("");
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

    public ArrayList<Person> getCreatorList() {
        return creatorList;
    }

    public void setCreatorList(ArrayList<Person> creatorList) {
        this.creatorList = creatorList;
    }

    public ArrayList<Person> getActorList() {
        return actorList;
    }

    public void setActorList(ArrayList<Person> actorList) {
        this.actorList = actorList;
    }

    public String getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(String seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getFinishYear() {
        return finishYear;
    }

    public void setFinishYear(String finishYear) {
        this.finishYear = finishYear;
    }

    public String getStoryline() {
        return storyline;
    }

    public void setStoryline(String storyline) {
        this.storyline = storyline;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        String result;

        result = "Title: " + this.getTitle() +
                " Start: " + this.getStartYear() +
                " Finished: "  + this.getFinishYear() +
                " Duration: " + this.getDuration() + "\n" +
                " Description: " + this.getDescription() + "\n" +
                " Storyline: " + this.getStoryline();

        return result;
    }
}