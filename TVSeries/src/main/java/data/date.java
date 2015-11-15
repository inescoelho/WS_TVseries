package data;

public class Date {
    private int day;
    private int month;
    private int year;


    public Date(String y)
    {
        day = 0;
        month = 0;
        setYear(Integer.parseInt(y));
    }


    public Date(String d, String m, String y)
    {
        setDay(Integer.parseInt(d));
        setMonth(this.transformToMonth(m));
        setYear(Integer.parseInt(y));
    }

    private int transformToMonth(String m) {
        int month = 1;

        switch (m)
        {
            case "January":
                month = 1;
                break;
            case "February":
                month = 2;
                break;
            case "March":
                month = 3;
                break;
            case "April":
                month = 4;
                break;
            case "May":
                month = 5;
                break;
            case "June":
                month = 6;
                break;
            case "July":
                month = 7;
                break;
            case "August":
                month = 8;
                break;
            case "September":
                month = 9;
                break;
            case "October":
                month = 10;
                break;
            case "November":
                month = 11;
                break;
            case "December":
                month = 12;
                break;
        }
        return month;
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
