package server.data.ontology;

/**
 * Class that stores all the relevant information of a given Person to be displayed in the web interface
 */
public class Person {

    private String id;
    private String name;
    private String biography;
    private String birthDate;
    private String wikiURL;
    private String type; // "actor", "creator" or "actorcreator"

    public Person(String id, String name, String biography, String birthDate, String wikiURL, String type) {
        this.id = id;
        this.name = name;
        this.biography = biography;
        this.birthDate = birthDate;
        this.wikiURL = wikiURL;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getWikiURL() {
        return wikiURL;
    }

    public void setWikiURL(String wikiURL) {
        this.wikiURL = wikiURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
