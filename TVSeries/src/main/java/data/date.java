package data;

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

    public String toString() {
        String result = "";

        if (day < 10) {
            result += "0" + day + "/";
        } else {
            result += day + "/";
        }

        if (month < 10) {
            result += "0" + month + "/";
        } else {
            result += month + "/";
        }

        result += year;

        return result;
    }
}
