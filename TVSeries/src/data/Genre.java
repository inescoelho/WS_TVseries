package data;

import java.util.ArrayList;

/**
 * Created by user on 09/11/2015.
 */
public class Genre {
    private String type;
    private ArrayList<Serie> series;

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

    public ArrayList<Serie> getSeries() {
        return series;
    }

    public void setSeries(ArrayList<Serie> series) {
        this.series = series;
    }
}
