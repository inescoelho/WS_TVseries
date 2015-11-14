package data;

import java.util.ArrayList;

/**
 * Created by user on 09/11/2015.
 */
public class Person {
    private String name;
    private date birthday;
    private ArrayList<Series> series;

    Person (String nm, date bd)
    {
        setName(nm);
        setBirthday(bd);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public date getBirthday() {
        return birthday;
    }

    public void setBirthday(date birthday) {
        this.birthday = birthday;
    }

    public ArrayList<Series> getSeries() {
        return series;
    }

    public void setSeries(ArrayList<Series> series) {
        this.series = series;
    }
}
