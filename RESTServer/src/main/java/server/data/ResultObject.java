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
    private boolean isPerson;
    private boolean isCreator;
    private boolean isActor;

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

    public void addPerson(String person) {
        this.peopleList.add(person);
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
        return "{\n\tSeries: " + isSeries +
                ",\n\tPerson: " + isPerson +
                ",\n\tCreator: " + isCreator +
                ",\n\tActor: " + isActor +
                ",\n\tGenreList: " + Arrays.toString(genreList.toArray()) +
                ",\n\tSeriesTitles: " + Arrays.toString(seriesTiles.toArray()) +
                ",\n\tActorsList: " + Arrays.toString(actorsList.toArray()) +
                ",\n\tCreatorsList:" + Arrays.toString(creatorsList.toArray()) +
                ",\n\tPeopleList: " + Arrays.toString(peopleList.toArray()) +
                "\n}";
    }

    public boolean isPerson() {
        return isPerson;
    }

    public void setPerson(boolean person) {
        isPerson = person;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    public boolean isActor() {
        return isActor;
    }

    public void setActor(boolean actor) {
        isActor = actor;
    }
}
