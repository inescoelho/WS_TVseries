package server.data.ontology;

import java.util.ArrayList;

public class Genre {
    private String type;
    private ArrayList<String[]> series; // String[3] - first is name, second is id, third is image url

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

    public ArrayList<String[]> getSeries() {
        return series;
    }

    public void setSeries(ArrayList<String[]> series) {
        this.series = series;
    }

    @Override
    public String toString() {
        String result = "Type: " + type + "; Series: ";
        for (String[] currentSeries : series) {
            result += currentSeries[0];
        }
        return result;
    }
}
