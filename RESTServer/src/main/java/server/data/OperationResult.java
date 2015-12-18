package server.data;

import java.util.ArrayList;

public class OperationResult {

    private ArrayList<String[]> series;// String[2] - first is name, second is id, third is imageurl
    private ArrayList<String[]> people;

    public OperationResult() {

    }

    public OperationResult(ArrayList<String[]> series, ArrayList<String[]> people) {
        this.series = series;
        this.people = people;
    }

    public ArrayList<String[]> getSeries() {
        return series;
    }

    public void setSeries(ArrayList<String[]> series) {
        this.series = series;
    }

    public ArrayList<String[]> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<String[]> people) {
        this.people = people;
    }
}
