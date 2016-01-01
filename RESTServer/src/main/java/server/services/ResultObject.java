package server.services;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultObject {

    public enum Running {
        NOT_SET,
        STILL_RUNNING,
        ALREADY_FINISHED,
    }

    public enum ScoreSearch {
        NOT_SET,
        SET,
        SET_LOWER,
        SET_HIGHER,
        SET_EQUAL,
    }

    public enum BirthYear {
        NOT_SET,
        SET,
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
    private int[] startedYear;
    private int[] finishYear;
    private Running stillRunning;
    private ScoreSearch score;
    private float scoreValue;
    private BirthYear brithYear;
    private int bornYearValue;

    public ResultObject() {
        genreList = new ArrayList<>();
        seriesTiles = new ArrayList<>();
        actorsList = new ArrayList<>();
        creatorsList = new ArrayList<>();
        peopleList = new ArrayList<>();
        startedYear = null;
        finishYear = null;
        stillRunning = Running.NOT_SET;
        bornYearValue = 1;
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
        String result = "{\n\tSeries: " + isSeries +
                        ",\n\tPerson: " + isPerson +
                        ",\n\tCreator: " + isCreator +
                        ",\n\tActor: " + isActor +
                        ",\n\tGenreList: " + Arrays.toString(genreList.toArray()) +
                        ",\n\tSeriesTitles: " + Arrays.toString(seriesTiles.toArray()) +
                        ",\n\tActorsList: " + Arrays.toString(actorsList.toArray()) +
                        ",\n\tCreatorsList:" + Arrays.toString(creatorsList.toArray()) +
                        ",\n\tPeopleList: " + Arrays.toString(peopleList.toArray()) +
                        ",\n\tStartYear: " + Arrays.toString(startedYear) +
                        ",\n\tFinishYear: " + Arrays.toString(finishYear) +
                        ",\n\tRunning: " + stillRunning +
                        ",\n\tScore: " + score;

        if (score != ScoreSearch.NOT_SET) {
            result += ",\n\tScoreValue: " + scoreValue;
        }
        result += ",\n\tBirthYear: " + brithYear;
        if (brithYear != BirthYear.NOT_SET) {
            result += ",\n\tBirthYearValue: " + bornYearValue;
        }

        result += "\n}";

        return result;
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

    public int[] getStartedYear() {
        return startedYear;
    }

    public void setStartedYear(int[] startedYear) {
        this.startedYear = startedYear;
    }

    public int[] getFinishYear() {
        return finishYear;
    }

    public void setFinishYear(int[] finishYear) {
        this.finishYear = finishYear;
    }

    public Running getStillRunning() {
        return stillRunning;
    }

    public void setStillRunning(boolean stillRunning) {
        System.out.println("AQUI " + stillRunning);
        if (stillRunning) {
            this.stillRunning = Running.STILL_RUNNING;
        } else {
            this.stillRunning = Running.ALREADY_FINISHED;
        }
    }

    public float getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(float scoreValue) {
        this.scoreValue = scoreValue;
    }

    public ScoreSearch getScore() {
        return score;
    }

    public void setScore(ScoreSearch score) {
        this.score = score;
    }

    public BirthYear getBrithYear() {
        return brithYear;
    }

    public void setBrithYear(BirthYear brithYear) {
        this.brithYear = brithYear;
    }

    public int getBornYearValue() {
        return bornYearValue;
    }

    public void setBornYearValue(int bornYearValue) {
        this.bornYearValue = bornYearValue;
    }
}
