package server.services;

import java.util.ArrayList;

public class OperationResult {

    private ArrayList<String[]> series;// String[3] - first is name, second is id, third is imageurl
    private ArrayList<String[]> people;

    public OperationResult() {
        this.people = new ArrayList<>();
        this.series = new ArrayList<>();
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

    public boolean addPerson(String[] person) {
        for (String[] temp : people) {
            // Same id
            if (temp[1].equals(person[1])) {
                return false;
            }
        }
        people.add(person);
        return true;
    }

    public boolean addSeries(String[] series) {
        for (String[] temp : this.series) {
            // Same id
            if (temp[1].equals(series[1])) {
                return false;
            }
        }
        this.series.add(series);
        return true;
    }
}
