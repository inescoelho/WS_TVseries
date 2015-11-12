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
    private date pilotDate;
    private boolean hasFinished;

    public Serie(String ttl, String desc, ArrayList<Person> creatL, ArrayList<Person> actL, String sn, date pd, boolean finish)
    {
        title = ttl;
        description = desc;
        creatorList = creatL;
        actorList = actL;
        seasonNumber = sn;
        pilotDate = pd;
        hasFinished = finish;
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

    public date getPilotDate() {
        return pilotDate;
    }

    public void setPilotDate(date pilotDate) {
        this.pilotDate = pilotDate;
    }

    public boolean isHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        this.hasFinished = hasFinished;
    }
}
