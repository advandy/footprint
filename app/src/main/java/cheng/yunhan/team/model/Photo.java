package cheng.yunhan.team.model;

import android.location.Location;
import android.net.Uri;


/**
 * Created by D060753 on 19.06.2017.
 */

public class Photo {
    private Location location;
    private Uri uri;
    private int day;
    private int month;
    private int year;
    private int cw;

    public Photo(Location location, Uri url, int day, int month, int year, int cw) {
        this.location = location;
        this.uri = url;
        this.day = day;
        this.month = month;
        this.year = year;
        this.cw = cw;

    }

    public int getCw() {
        return cw;
    }

    public void setCw(int cw) {
        this.cw = cw;
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

    public Uri getUri() {
        return uri;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

}
