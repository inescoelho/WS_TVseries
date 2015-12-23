package server.data.utils;

public class PeopleSeriesFrequency {
    private String seriesId;
    private String seriesTitle;
    private String seriesImageURL;
    private int frequency;
    private double seriesRating;

    public PeopleSeriesFrequency(String seriesId, double seriesRating, String seriesTitle, String seriesImageURL) {
        this.seriesId = seriesId;
        this.seriesRating = seriesRating;
        this.seriesTitle = seriesTitle;
        this.seriesImageURL = seriesImageURL;
        frequency = 1;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public double getSeriesRating() {
        return seriesRating;
    }

    public void increaseFrequency() {
        frequency++;
    }

    public String toString() {
        return "Series: " + seriesId + " with title: " + seriesTitle  + " with rating: " + seriesRating +
                " and frequency " + frequency;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public String getSeriesImageURL() {
        return seriesImageURL;
    }
}
