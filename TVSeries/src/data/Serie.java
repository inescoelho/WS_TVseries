package data;

import java.util.ArrayList;

/**
 * Created by user on 09/11/2015.
 */
public class Serie {
    private String title;
    private String description;
    private ArrayList<Person> creatorList;
    private ArrayList<Person> actorList;
    private String seasonNumber;
    private String startYear;
    private String finishYear;
    private boolean hasFinished;

    public  Serie()
    {
        title = "";
        description = "";
        creatorList = new ArrayList<Person>();
        actorList = new ArrayList<Person>();
        seasonNumber = "";
        startYear = "";
        finishYear = "";
        hasFinished = false;
    }

    public Serie(String ttl, String desc, ArrayList<Person> creatL, ArrayList<Person> actL, String sn,
                 String start, String finish, boolean isFinish)
    {
        title = ttl;
        description = desc;
        creatorList = creatL;
        actorList = actL;
        seasonNumber = sn;
        startYear = start;
        finishYear = finish;
        hasFinished = isFinish;
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

    public boolean isHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        this.hasFinished = hasFinished;
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
}
