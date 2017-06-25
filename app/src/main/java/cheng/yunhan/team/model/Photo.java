package cheng.yunhan.team.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ThumbnailUtils;
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

    public static Bitmap getBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getThumnail(Context context, String path) {
        return ThumbnailUtils.extractThumbnail(
                getBitmapFromFile(path, 300, 300), 300, 300);
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
