package pl.me.sqlitetest3.activities;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.softeq.android.prepopdb.R;

import java.util.Collections;

import pl.me.sqlitetest3.ActivityPlace;
import pl.me.sqlitetest3.ExternalDbOpenHelper;
import pl.me.sqlitetest3.additional.Point;


public class SummaryActivity extends Activity {
    private SQLiteDatabase database;
    TextView choiceText, pointsText;
    Point homePlace, chosenPoint;

    TextView bestPlace1, bestPlace2, bestPlace3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        //Our key helper
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "database_test.db");
        database = dbOpenHelper.openDataBase();
        //That’s it, the database is open!

        choiceText = (TextView) findViewById(R.id.choice_value);
        pointsText = (TextView) findViewById(R.id.points);

        //chosenPlace = (ActivityPlace) getIntent().getSerializableExtra("ChosenPlace");
        int chosenPlaceIndex = Integer.parseInt(getIntent().getStringExtra("chosenPlaceIndex"));
        ActivityPlace chosenPlace = MainActivity.places.get(chosenPlaceIndex);
        chosenPoint = new Point(chosenPlace.getLongitude(), chosenPlace.getLatitude());

        homePlace = (Point) getIntent().getSerializableExtra("HomePlace");
        choiceText.setText(chosenPlace.getName() + ", cel: " + chosenPlace.getActivityType());

        for (int i=0; i<MainActivity.places.size(); i++) {
            ActivityPlace ap = MainActivity.places.get(i);
            String optimalRideTime2 = "0";
            try {
                optimalRideTime2 = String.valueOf(Integer.parseInt(ap
                        .getRealRideTime(homePlace))-10);
            } catch (NumberFormatException e) {    }
            ap.setScore(ap.computeScore(ap.getSmog(), ap.getWeather(),
                    ap.getRealRideTime(homePlace), optimalRideTime2, ap.getChoicesCount(database)));
        }
        pointsText.setText(String.valueOf(chosenPlace.getScore()));
        Collections.sort(MainActivity.places);
        bestPlace1 = (TextView) findViewById(R.id.recommendation1);
        bestPlace2 = (TextView) findViewById(R.id.recommendation2);
        bestPlace3 = (TextView) findViewById(R.id.recommendation3);

        if (chosenPlace == MainActivity.places.get(0))
            bestPlace1.setTypeface(null, Typeface.BOLD);
        else if (chosenPlace == MainActivity.places.get(1))
            bestPlace2.setTypeface(null, Typeface.BOLD);
        else if (chosenPlace == MainActivity.places.get(2))
            bestPlace3.setTypeface(null, Typeface.BOLD);
        bestPlace1.setText(MainActivity.places.get(0).getName()+" ("+MainActivity.places.get(0).getStreet()+
                ") "+MainActivity.places.get(0).getActivityType()+" "+MainActivity.places.get(0).getScore());
        bestPlace2.setText(MainActivity.places.get(1).getName()+" ("+MainActivity.places.get(1).getStreet()+
                ") "+MainActivity.places.get(1).getActivityType()+" "+MainActivity.places.get(1).getScore());
        bestPlace3.setText(MainActivity.places.get(2).getName()+" ("+MainActivity.places.get(2).getStreet()+
                ") "+MainActivity.places.get(2).getActivityType()+" "+MainActivity.places.get(2).getScore());

    }
}
