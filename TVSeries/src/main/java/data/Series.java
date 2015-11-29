package data;

import java.util.ArrayList;


public class Series {
    private String seriesId;
    private String title;
    private String description;
    private String storyline;
    private ArrayList<Person> creatorList;
    private ArrayList<Person> actorList;
    private String startYear;
    private String finishYear;
    private String duration;
    private ArrayList<String> genres;
    private String image;
    private float score;

    public  Series(String id)
    {
        setSeriesId(id);
        title = "";
        description = "";
        setStoryline("");
        creatorList = new ArrayList<>();
        actorList = new ArrayList<>();
        startYear = "";
        finishYear = "";
        setDuration("");
        setGenres(new ArrayList<>());
        setImage("");
        setScore(0);
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
                " Score: " + this.getScore() +
                " Duration: " + this.getDuration() + "\n" +
                " Description: " + this.getDescription() + "\n" +
                " Storyline: " + this.getStoryline() + "\n" +
                " Creators: " + this.getCreatorList().toString() + "\n" +
                " Actors: " + this.getActorList().toString() + "\n" +
                " Images: " + this.getImage();
        return result;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void addGenre(Genre genre) {
        // Remove the whitespace
        this.getGenres().add(genre.getType().replaceAll(" ", ""));
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
