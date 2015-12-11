package server.data;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultObject {

    private List<String> genreList;
    private List<String> seriesTiles;
    private List<String> actorsList;
    private List<String> creatorsList;
    private List<String> peopleList;
    private boolean isSeries;

    public ResultObject() {
        genreList = new ArrayList<>();
        seriesTiles = new ArrayList<>();
        actorsList = new ArrayList<>();
        creatorsList = new ArrayList<>();
        peopleList = new ArrayList<>();
    }

    public boolean isSeries() {
        return isSeries;
    }

    public void setSeries(boolean series) {
        isSeries = series;
    }

    public List<String> getPeopleList() {
        return peopleList;
    }

    public void setPeopleList(List<String> peopleList) {
        this.peopleList = peopleList;
    }

    public List<String> getCreatorsList() {
        return creatorsList;
    }

    public void addCreator(String creator) {
        this.creatorsList.add(creator);
    }

    public List<String> getActorsList() {
        return actorsList;
    }

    public void addActor(String actor) {
        this.actorsList.add(actor);
    }

    public List<String> getSeriesTiles() {
        return seriesTiles;
    }

    public void addSeriesTitle(String title) {
        this.seriesTiles.add(title);
    }

    public List<String> getGenreList() {
        return genreList;
    }

    public void addToGenreList(String genre) {
        this.genreList.add(genre);
    }

    public String toString() {
        return "{\n\tSeries: " + isSeries + ",\n\tGenreList: " + Arrays.toString(genreList.toArray()) +
                ",\n\tSeriesTitles: " + Arrays.toString(seriesTiles.toArray()) +
                "\n}";
    }
}
