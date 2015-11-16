package data;

/**
 * Created by user on 09/11/2015.
 */
public class Person {
    private String id;
    private String name;
    private String biography;
    private Date birthday;

    public Person (String nm, String identif)
    {
        setName(nm);
        setId(identif);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId(String identif) {
        this.id = identif;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        String result = "Name: " + name + "\n" +
                        "ID: " + id + "\n" +
                        "Birthday " + this.getBirthday() + "\n" +
                        "Biography " + this.getBiography();

        return result;
    }
}
