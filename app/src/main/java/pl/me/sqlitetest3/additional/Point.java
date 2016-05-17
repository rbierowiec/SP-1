package pl.me.sqlitetest3.additional;

import java.io.Serializable;

/**
 * Created by Piotr1 on 2016-03-29.
 */
public class Point implements Serializable {
    private double longitude;
    private double latitude;

    public Point(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double countDistance (Point other) {
        return Math.sqrt(Math.pow(longitude-other.getLongitude(),2)+Math.pow(latitude-other.getLatitude(),2));
    }
}
