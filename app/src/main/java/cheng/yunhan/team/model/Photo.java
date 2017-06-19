package cheng.yunhan.team.model;

import android.location.Location;

import java.net.URL;

/**
 * Created by D060753 on 19.06.2017.
 */

public class Photo {
    private Location location;
    private URL url;
    private int day;
    private int month;
    private int year;

    public Photo(Location location, URL url, int day, int month, int year) {
        this.location = location;
        this.url = url;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Location getLocation() {
        return location;
    }

    public URL getUrl() {
        return url;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

}
