package pl.me.sqlitetest3.timelibrary;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.me.sqlitetest3.additional.Point;

/**
 * Created by Piotr1 on 2016-04-25.
 */
public class PobieraczCzasu {
    private static final String API_KEY = "AIzaSyAP7dMX1At-NL62WICuMgb1s-kAX6BLrH4";
    public PobieraczCzasu() {   }

    public String getRealTime(Point homePlace, Point chosenPlace) {
        String url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                homePlace.getLatitude() +"," +
                homePlace.getLongitude() + "&destinations=" + chosenPlace.getLatitude()+ ", " + chosenPlace.getLongitude()+
                "&mode=bicycling&language=en-EN&key="
                + API_KEY;
        GoogleMatrixRequest request = new GoogleMatrixRequest();
        try {
            String res = request.execute(url_request).get();
            JSONObject root = new JSONObject(res);
            JSONArray array = root.getJSONArray("rows");
            JSONObject array2 = array.getJSONObject(0);
            //JSONObject array3 = array2.getJSONObject("duration");
            JSONArray array3 = array2.getJSONArray("elements");
            JSONObject array4 = array3.getJSONObject(0);
            JSONObject array5 = array4.getJSONObject("duration");
            return (String)array5.get("text");
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        return "---";
    }
}
