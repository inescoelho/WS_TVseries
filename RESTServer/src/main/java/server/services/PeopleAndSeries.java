package server.services;


import java.util.ArrayList;

public class PeopleAndSeries {
    private ArrayList<String> peopleList;
    private ArrayList<String> seriesList;

    public PeopleAndSeries(ArrayList<String> peopleList, ArrayList<String> seriesList) {
        this.peopleList = peopleList;
        this.seriesList = seriesList;
    }

    public ArrayList<String> getPeopleList() {
        return peopleList;
    }

    public ArrayList<String> getSeriesList() {
        return seriesList;
    }
}
