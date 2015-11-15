package data;

/**
 * Created by user on 09/11/2015.
 */
public class date {
    private int day;
    private int month;
    private int year;

    public date(int d, int m, int y)
    {
        setDay(d);
        setMonth(m);
        setYear(y);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
