package server.data;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultObject {

    public enum Running {
        NOT_SET,
        STILL_RUNNING,
        ALREADY_FINISHED;
    }

    private List<String> genreList;
    private List<String> seriesTiles;
    private List<String> actorsList;
    private List<String> creatorsList;
    private List<String> peopleList;
    private boolean isSeries;
    private boolean isPerson;
    private boolean isCreator;
    private boolean isActor;
    private int startedYear;
    private int finishYear;
    private Enum stillRunning;


    public ResultObject() {
        genreList = new ArrayList<>();
        seriesTiles = new ArrayList<>();
        actorsList = new ArrayList<>();
        creatorsList = new ArrayList<>();
        peopleList = new ArrayList<>();
        startedYear = -1;
        finishYear = -1;
        stillRunning = Running.NOT_SET;
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
        if(!peopleList.contains(person) && !creatorsList.contains(person) && !actorsList.contains(person)) {
            peopleList.add(person);
        }
    }

    public List<String> getCreatorsList() {
        return creatorsList;
    }

    public void addCreator(String creator) {
        if (!creatorsList.contains(creator)) {
            this.creatorsList.add(creator);

            if (peopleList.contains(creator)) {
                peopleList.remove(creator);
            }
        }
    }

    public List<String> getActorsList() {
        return actorsList;
    }

    public void addActor(String actor) {
        if (!actorsList.contains(actor)) {
            this.actorsList.add(actor);

            if (peopleList.contains(actor)) {
                peopleList.remove(actor);
            }
        }
    }

    public List<String> getSeriesTiles() {
        return seriesTiles;
    }

    public void addSeriesTitle(String title) {
        if (!seriesTiles.contains(title)) {
            this.seriesTiles.add(title);
        }
    }

    public List<String> getGenreList() {
        return genreList;
    }

    public void addToGenreList(String genre) {
        if (!genreList.contains(genre)) {
            this.genreList.add(genre);
        }
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

    public int getStartedYear() {
        return startedYear;
    }

    public void setStartedYear(int startedYear) {
        this.startedYear = startedYear;
    }

    public int getFinishYear() {
        return finishYear;
    }

    public void setFinishYear(int finishYear) {
        this.finishYear = finishYear;
    }

    public Enum getStillRunning() {
        return stillRunning;
    }

    public void setStillRunning(boolean stillRunning) {
        if (stillRunning) {
            this.stillRunning = Running.STILL_RUNNING;
        } else {
            this.stillRunning = Running.ALREADY_FINISHED;
        }
    }
}
