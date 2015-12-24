package server.data.utils;

public class GenreFromSeriesFrequency {

    private String genreName;
    private int frequency;

    public GenreFromSeriesFrequency(String genreName) {
        this.genreName = genreName;
        this.frequency = 1;
    }

    public String getGenreName() {
        return genreName;
    }

    public int getFrequency() {
        return frequency;
    }

    public void increaseFrequency() {
        frequency++;
    }

    public String toString() {
        return "Genre " + genreName + " with frequency " + frequency;
    }
}
