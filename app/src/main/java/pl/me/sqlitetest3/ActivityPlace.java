package pl.me.sqlitetest3;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

import pl.me.sqlitetest3.additional.Data;
import pl.me.sqlitetest3.additional.Point;
import pl.me.sqlitetest3.additional.SmogValuesContainer;
import pl.me.sqlitetest3.additional.Weather;
import pl.me.sqlitetest3.timelibrary.PobieraczCzasu;
import pl.me.sqlitetest3.weatherlibrary.PobieraczPogody;
import pl.me.sqlitetest3.weatherlibrary.PobieraczPogodyInt;

/**
 * Created by Piotr1 on 2016-03-30.
 */
public class ActivityPlace implements Serializable, Comparable<ActivityPlace>, Cloneable {
    private int id;
    private String name;
    private double longitude;
    private double latitude;
    private String street;
    private String placeType;
    private String activityType;
    private int score;

    public ActivityPlace(int id, String name, double longitude, double latitude, String street, String placeType, String activityType) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.street = street;
        this.placeType = placeType;
        this.activityType = activityType;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getStreet() {
        return street;
    }

    public String getPlaceType() {
        return placeType;
    }

    public String getActivityType() {
        return activityType;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSmog() {
        double [] distances = new double[3];
        Point thisPoint = new Point(longitude,latitude);
        distances[0] = thisPoint.countDistance(new Point(19.926955, 50.057704)); //krasinskiego
        distances[1] = thisPoint.countDistance(new Point(19.952148, 50.010741)); //bujaka
        distances[2] = thisPoint.countDistance(new Point(20.045405, 50.080524)); //bulwarowa
        if (distances[0] < distances[1] && distances[0] < distances[2]) return SmogValuesContainer.PM10_KRASINSKIEGO;
        else if (distances[1] < distances[0] && distances[1] < distances[2]) return SmogValuesContainer.PM10_BUJAKA;
        else return SmogValuesContainer.PM10_BULWAROWA;
    }

    public int getChoicesCount(SQLiteDatabase db) {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String dayTime;
        if (c.get(Calendar.HOUR_OF_DAY) > 21 || c.get(Calendar.HOUR_OF_DAY) < 5) dayTime = "night";
        else if (c.get(Calendar.HOUR_OF_DAY) >= 5 && c.get(Calendar.HOUR_OF_DAY) < 12) dayTime = "am";
        else if (c.get(Calendar.HOUR_OF_DAY) >= 12 && c.get(Calendar.HOUR_OF_DAY) < 17) dayTime = "pm";
        else dayTime = "evening";
        Cursor cursor = db.query("user_history",new String[] {"choices"},"activity_type = ? and " +
                        "day_of_week = ? and day_time = ?",new String[] {activityType, String.valueOf(dayOfWeek),dayTime},
                null,null,null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            return cursor.getInt(0);
        }
        cursor.close(); return 0;
    }

    public Weather getWeather() {
        PobieraczPogodyInt stacja = new PobieraczPogody();
        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.MINUTE) < 45)
            c.add(Calendar.HOUR_OF_DAY,-1);
        return stacja.pobierzPogodeDlaPunktu(new Point(longitude,latitude),
                new Data(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.HOUR_OF_DAY),0));
    }

    public String getRealRideTime(Point homePlace) {
        return new PobieraczCzasu().getRealTime(homePlace,new Point(longitude,latitude)).split(" ")[0];
    }

    public int computeScore(String smogValue, Weather weather, String realRideTime, String
            optimalRideTime, int choicesCount) {
        int k=1;
        if (activityType.equals("spacer") || activityType.equals("sport")) k=3;
        int smogPart = 0, ridePart = 0;
        try {
            smogPart = 100 - k*Integer.parseInt(smogValue);
        } catch (NumberFormatException e) {}
        try {
            ridePart = (Integer.parseInt(optimalRideTime))/(Integer.parseInt(realRideTime))*100;
        } catch (NumberFormatException e) {}
        int choicesPart = 100*choicesCount;
        int weatherPart;
        if (weather.getRain().equals("jest") && weather.getSurface().equals("mokra")) weatherPart = 0;
        else if (weather.getRain().equals("jest") && weather.getSurface().equals("sucha")) weatherPart = 50;
        else if (weather.getRain().equals("jest") && weather.getSurface().equals("wilgotna")) weatherPart =25;
        else if (weather.getRain().equals("brak") && weather.getSurface().equals("mokra")) weatherPart = 20;
        else if (weather.getRain().equals("brak") && weather.getSurface().equals("sucha")) weatherPart = 70;
        else if (weather.getRain().equals("brak") && weather.getSurface().equals("wilgotna")) weatherPart =45;
        else weatherPart = 100;
        double temperature = Double.parseDouble(weather.getAirTemperature());
        weatherPart -= 10*Math.abs(temperature-22.0);
        double windSpeed = Double.parseDouble(weather.getWindSpeed());
        weatherPart -= 5*windSpeed;
        System.out.println("POINTS: "+name+" s="+smogPart+" r="+ridePart+" c="+choicesPart+" w="+weatherPart);
        return smogPart+ridePart+choicesPart+weatherPart;
    }

    @Override
    public int compareTo(@NonNull ActivityPlace another) {
        if (this.score < another.score) return 11;
        else if (this.score > another.score) return -1;
        else return 0;
    }

    @Override
    public String toString() {
        return "ActivityPlace{" +
                "score=" + score +
                ", activityType='" + activityType + '\'' +
                ", placeType='" + placeType + '\'' +
                ", street='" + street + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                '}';
    }

    public ActivityPlace clone() {
        try {
            super.clone();
            return new ActivityPlace(id,name,longitude,latitude,street,placeType,activityType);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }
}
