package main.additional;

public class Point {
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