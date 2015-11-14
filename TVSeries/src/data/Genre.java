package data;

import java.util.ArrayList;

/**
 * Created by user on 09/11/2015.
 */
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
}
