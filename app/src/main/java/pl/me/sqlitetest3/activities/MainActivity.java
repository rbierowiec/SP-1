package pl.me.sqlitetest3.activities;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.softeq.android.prepopdb.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import pl.me.sqlitetest3.ActivityPlace;
import pl.me.sqlitetest3.CustomArrayAdapter;
import pl.me.sqlitetest3.ExternalDbOpenHelper;
import pl.me.sqlitetest3.additional.Point;
import pl.me.sqlitetest3.additional.SmogValuesContainer;
import pl.me.sqlitetest3.calendarlib.CalendarOperations;
import pl.me.sqlitetest3.smoglibrary.MeasuredSubstance;
import pl.me.sqlitetest3.smoglibrary.PobieraczSmogu;

/**
 * Dzialajacy przyklad
 */
public class MainActivity extends ListActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private SQLiteDatabase database;
	private ListView listView;
	public static List<ActivityPlace> places;
	private Point homePlace;
    private GoogleApiClient mGoogleApiClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        SmogValuesContainer.PM10_KRASINSKIEGO = new PobieraczSmogu(MeasuredSubstance.PM10).
                computeNumberValue(new Point(19.926955, 50.057704));
        SmogValuesContainer.PM10_BUJAKA = new PobieraczSmogu(MeasuredSubstance.PM10).
                computeNumberValue(new Point(19.952148, 50.010741));
        SmogValuesContainer.PM10_BULWAROWA = new PobieraczSmogu(MeasuredSubstance.PM10).
                computeNumberValue(new Point(20.045405, 50.080524));
        //Our key helper
		ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, "database_test.db");
		database = dbOpenHelper.openDataBase();
		//That’s it, the database is open!
		fillPlaces();
		setUpList();
        TextView histotyTV = (TextView) findViewById(R.id.history);
        histotyTV.setMovementMethod(new ScrollingMovementMethod());
        histotyTV.setText(getUserHistory());
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.addApi(AppIndex.API).build();
		mGoogleApiClient.connect();

        CalendarOperations Calendar = new CalendarOperations(this);
        System.out.println("Wolny czas: "+ Calendar.zwrocCzasWolnyOdDaty(24));
        System.out.println("Sredni czas aktywnosci: "+ Calendar.wyliczSredniCzasAktywnosci("bieganie"));
	}

	private void setUpList() {
		//We use a standard adapter and an element layout for brevity’s sake
		//setListAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, places));
		//setListAdapter(new ArrayAdapter<>(this,R.layout.row, places));
		setListAdapter(new CustomArrayAdapter(this, places));
		listView = getListView();

		//Let’s set a message shown upon tapping an item
		listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                addUserHistory(places.get(position).getActivityType());
                Intent intent = new Intent(getApplicationContext(), SummaryActivity.class);
                //intent.putExtra("ChosenPlace", places.get(position).clone());
                intent.putExtra("chosenPlaceIndex",String.valueOf(position));
                intent.putExtra("HomePlace", homePlace);
                //database.close();
                startActivity(intent);
            }
        });
	}

	//Extracting elements from the database
	private void fillPlaces() {
		places = new ArrayList<>();
		String [] columns = {"_id","name","longitude","latitude","street","place_type","type"};
		Cursor placesCursor = database.query("Place join Activity on Place._id = Activity.place_id",columns,
				null,null,null,null,null);
		placesCursor.moveToFirst();
		if(!placesCursor.isAfterLast()) {
			do {
				int id = placesCursor.getInt(0);
				String name = placesCursor.getString(1);
				double longitude = placesCursor.getDouble(2);
				double latitude = placesCursor.getDouble(3);
				String street = placesCursor.getString(4);
				String placeType = placesCursor.getString(5);
				String activityType = placesCursor.getString(6);

				places.add(new ActivityPlace(id,name,longitude,latitude,street,placeType,activityType));
			} while (placesCursor.moveToNext());
		}
		placesCursor.close();
	}

    private String getUserHistory() {
        String [] columns = {"activity_type", "day_of_week", "day_time", "choices"};
        Cursor historyCursor = database.query("user_history", columns,null,null,null,null,null);
        StringBuilder sb = new StringBuilder();
        historyCursor.moveToFirst();
        if (!historyCursor.isAfterLast()) {
            String [] results = new String[4];
            do {
                for (int i=0; i<3; i++) {
                    results[i] = historyCursor.getString(i);
                }
                results[3] = String.valueOf(historyCursor.getInt(3));
                sb.append(Arrays.toString(results)).append("\n");
            } while (historyCursor.moveToNext());
        }
        historyCursor.close();
        return sb.toString();
    }

    private void addUserHistory(String activityType) {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String dayTime;
        if (c.get(Calendar.HOUR_OF_DAY) > 21 || c.get(Calendar.HOUR_OF_DAY) < 5) dayTime = "night";
        else if (c.get(Calendar.HOUR_OF_DAY) >= 5 && c.get(Calendar.HOUR_OF_DAY) < 12) dayTime = "am";
        else if (c.get(Calendar.HOUR_OF_DAY) >= 12 && c.get(Calendar.HOUR_OF_DAY) < 17) dayTime = "pm";
        else dayTime = "evening";
        ContentValues contentValues = new ContentValues();
        if (getChoicesCount(activityType) > 0) { //increment counter in database
            contentValues.put("choices", getChoicesCount(activityType) + 1);
            database.update("user_history", contentValues, "activity_type = ? and day_of_week = ? and " +
                    "day_time = ?", new String[]{activityType, String.valueOf(dayOfWeek), dayTime});
        }
       else { //create new entry in user history
            contentValues.clear();
            contentValues.put("activity_type",activityType);
            contentValues.put("day_of_week",String.valueOf(dayOfWeek));
            contentValues.put("day_time",dayTime);
            database.insert("user_history", null, contentValues);
        }
    }

    private int getChoicesCount(String activityType) {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String dayTime;
        if (c.get(Calendar.HOUR_OF_DAY) > 21 || c.get(Calendar.HOUR_OF_DAY) < 5) dayTime = "night";
        else if (c.get(Calendar.HOUR_OF_DAY) >= 5 && c.get(Calendar.HOUR_OF_DAY) < 12) dayTime = "am";
        else if (c.get(Calendar.HOUR_OF_DAY) >= 12 && c.get(Calendar.HOUR_OF_DAY) < 17) dayTime = "pm";
        else dayTime = "evening";
        Cursor cursor = database.query("user_history",new String[] {"choices"},"activity_type = ? and " +
                        "day_of_week = ? and day_time = ?",new String[] {activityType, String.valueOf(dayOfWeek),dayTime},
                null,null,null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            return cursor.getInt(0);
        }
        cursor.close(); return 0;
    }
    ////////////////////////////////////////////////////////////////////
    // Methods connected with Google location API
	/**
	 * Method is invoked after successful connecting of mGoogleApiClient
	 * @param bundle ...
	 */
    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation == null) {
            Toast.makeText(this,"Error while acuiring current localization. Default start localization is " +
                            "set.",
                    Toast.LENGTH_LONG).show();
            homePlace = new Point(19.9233848,50.0644979);
            return;
        }
        homePlace = new Point(mLastLocation.getLongitude(), mLastLocation.getLatitude());
        Toast.makeText(this,"Start localization acquired successfully.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"Error while connecting to Google API. Default start localization is set.",
                Toast.LENGTH_LONG).show();
        homePlace = new Point(19.9233848,50.0644979);
    }
}