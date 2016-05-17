package pl.me.sqlitetest3.smoglibrary;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import pl.me.sqlitetest3.additional.Point;

/**
 * Created by Piotr1 on 2016-03-28.
 *
 */
public class PobieraczSmogu implements PobieraczSmoguInt{
    private MeasuredSubstance measuredSubstance; //1 - PM10, 2 - PM2,5
    private String numberValue;
    private String textValue;
    private MeasurementStation nearestStation;
    //1 - al. Krasinskiego, 2 - Bujaka, 3 - Bulwarowa
    // 1: long: 19.926955 latit: 50.057704
    // 2: long: 19.952148 latit: 50.010741
    // 3: long: 20.045405 latit: 50.080542

    public static final String pm10Url = "http://www.malopolska" +
            ".pl/Obywatel/EKO-prognozaMalopolski/Malopolska/Strony/PylPM10.aspx";
    private static final String pm25Url = "http://www.malopolska" +
            ".pl/Obywatel/EKO-prognozaMalopolski/Malopolska/Strony/PylPM25.aspx";
    private static final Point krasinskiegoStation = new Point(19.926955,50.057704);
    private static final Point bujakaStation = new Point(19.952148,50.010741);
    private static final Point bulwarowaStation = new Point(20.045405,50.080524);

    public PobieraczSmogu(MeasuredSubstance measuredSubstance) {
        this.measuredSubstance = measuredSubstance;
    }

    @Override
    public String computeNumberValue(Point selectedPoint) {
        if (numberValue != null && !numberValue.equals("-")) return numberValue;
        double krasinskiegoDistance = krasinskiegoStation.countDistance(selectedPoint);
        double bujakaDistance = bujakaStation.countDistance(selectedPoint);
        double bulwarowaDistance = bulwarowaStation.countDistance(selectedPoint);
        if (krasinskiegoDistance < bujakaDistance && krasinskiegoDistance < bulwarowaDistance)
            nearestStation = MeasurementStation.KRK_KRASINSKIEGO;
        else if (bujakaDistance < krasinskiegoDistance && bujakaDistance < bulwarowaDistance)
            nearestStation = MeasurementStation.KRK_BUJAKA;
        else
            nearestStation = MeasurementStation.KRK_BULWAROWA;
        System.out.println("APP: nearestStation = "+nearestStation);

        if (measuredSubstance == MeasuredSubstance.PM10) {
            if (nearestStation == MeasurementStation.KRK_KRASINSKIEGO)
                new HTMLDownloader().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pm10Url, "Kraków, Al. Krasińskiego");
            else if (nearestStation == MeasurementStation.KRK_BUJAKA)
                new HTMLDownloader().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pm10Url, "Kraków, ul. Bujaka");
            else
                new HTMLDownloader().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pm10Url, "Kraków, ul. Bulwarowa");
        }
        else {
            if (nearestStation == MeasurementStation.KRK_KRASINSKIEGO)
                new HTMLDownloader().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pm25Url, "Kraków, Al. Krasińskiego");
            else if (nearestStation == MeasurementStation.KRK_BUJAKA)
                new HTMLDownloader().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pm25Url, "Kraków, ul. Bujaka");
            else
                new HTMLDownloader().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pm25Url, "Kraków, ul. Bulwarowa");
        }
        while (numberValue == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("DEBUG: koniec smogu");
        return numberValue;
    }


    class HTMLDownloader extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            URLConnection urlConnection = null;
            InputStream is = null;
            try {
                url = new URL(params[0]);
                urlConnection = url.openConnection();
                try {
                    is = urlConnection.getInputStream();
                } catch (UnknownHostException e) {
                    numberValue = "No Connection!";
                    return numberValue;
                }
                if (is == null) {
                    numberValue = "No Connection!";
                    return numberValue;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ( (line = br.readLine() ) != null) {
                    if ((line.trim()).equals(params[1])) {
                        br.readLine();
                        br.readLine();
                        line = br.readLine().trim();
                        String [] parts = line.split(" ");
                        numberValue = parts[0];
                        return parts[0];
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error";
            }
            return null;
        }
    }
}
