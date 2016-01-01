package server.utils;


public class PersonFromSeriesFrequency {

    private String personId;
    private String personName;
    private String personImageURL;
    private int frequency;

    public PersonFromSeriesFrequency(String personId, String personName, String personImageURL) {
        this.personId = personId;
        this.personName = personName;
        this.personImageURL = personImageURL;
        this.frequency = 1;
    }


    public String getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return personName;
    }

    public String getPersonImageURL() {
        return personImageURL;
    }

    public int getFrequency() {
        return frequency;
    }

    public void increaseFrequency() {
        frequency++;
    }

    public String toString() {
        return "Person with id: " + personId + " and name: " + personName + " and frequency: " + frequency;
    }
}
