package data;

import java.util.ArrayList;


public class Genre {
    private String type;
    private ArrayList<Series> series;

    public Genre(String tp)
    {
        type = tp;
        series = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Series> getSeries() {
        return series;
    }

    public void setSeries(ArrayList<Series> series) {
        this.series = series;
    }

    @Override
    public String toString() {
        String result = "Type " + this.getType() + "\n";
        for (Series series: this.getSeries()
             ) {
            result += "\t" + series.getTitle() + "\n";
        }
        return result;
    }
}
