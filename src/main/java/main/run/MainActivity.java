package main.run;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import main.calendarlib.CalendarOperations;
import main.weatherlib.PobieraczPogody;
import com.softeq.android.prepopdb.R;

import java.util.Date;
import java.util.List;

import main.additional.*;

public class MainActivity extends AppCompatActivity {

    private EditText editText1, editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = (EditText)findViewById(R.id.editText);
        editText2 = (EditText)findViewById(R.id.editText2);

    }

    public void dzialaj(View view){
        PobieraczPogody stacja = new PobieraczPogody();

        CalendarOperations kalendarz = new CalendarOperations();
        List<CalendarEvent> eventy = kalendarz.zwrocWydarzeniaPomiedzyDatami("2016-04-22 00:00:00", "2016-04-22 23:59:59", this);

        for (CalendarEvent event : eventy) {
            System.out.println(event.getTitle() + " " + event.getEventID()+ " " + event.getCalendarID() + " " + Data.convertMilisToData(event.getStartDate()) + " " + Data.convertMilisToData(event.getEndDate()));
        }
    }
}
