package server.utils;


import java.util.ArrayList;

public class GetMostPopularSeriesNotSeenReturn {

    private int totalNumberPeople;
    private ArrayList<String[]> mostPopularSeries;

    public GetMostPopularSeriesNotSeenReturn() {
        this.mostPopularSeries = new ArrayList<>();
        this.totalNumberPeople = 0;
    }


    public int getTotalNumberPeople() {
        return totalNumberPeople;
    }

    public void setTotalNumberPeople(int totalNumberPeople) {
        this.totalNumberPeople = totalNumberPeople;
    }

    public ArrayList<String[]> getMostPopularSeries() {
        return mostPopularSeries;
    }

    public void setMostPopularSeries(ArrayList<String[]> mostPopularSeries) {
        this.mostPopularSeries = mostPopularSeries;
    }
}
