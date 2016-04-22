package main.calendarlib;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import java.util.ArrayList;
import main.additional.Calendar;
import main.additional.CalendarEvent;
import main.additional.Data;

/**
 * Created by Rafael on 2016-04-22.
 */
public class CalendarOperations {

    public List<Calendar> zwrocKalendarze(Context  mContext) {
        String[] projection = new String[]{"_id", "name", "account_type"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(calendars, null, null, null, null);

        List<Calendar> kalendarze = new ArrayList<Calendar>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Calendar kalendarz = new Calendar();

                kalendarz.setCalendarID(cursor.getString(0));
                kalendarz.setCalendarName(cursor.getString(1));
                kalendarz.setCalendarType(cursor.getString(2));

                kalendarze.add(kalendarz);

            } while (cursor.moveToNext());
        }

        return kalendarze;
    }

    public List<CalendarEvent> zwrocWszystkieWydarzenia(Context  mContext) {
        String[] projection = new String[]{"_id", "title", "dtstart", "dtend", "calendar_id"};
        Uri calendars = Uri.parse("content://com.android.calendar/events");

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(calendars, projection, null, null, null);

        List<CalendarEvent> eventy = new ArrayList<CalendarEvent>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {

                CalendarEvent event = new CalendarEvent();

                event.setEventID(cursor.getString(0));
                event.setTitle(cursor.getString(1));
                event.setStartDate(cursor.getLong(2));
                event.setEndDate(cursor.getLong(3));
                event.setCalendarID(cursor.getString(4));

                eventy.add(event);

            } while (cursor.moveToNext());
        }

        return eventy;
    }

    public List<CalendarEvent> zwrocWydarzeniaDlaKalendarza(String calendarID, Context mContext){
        List<CalendarEvent> eventy = new ArrayList<CalendarEvent>();
        List<CalendarEvent> eventyWszystkie = this.zwrocWszystkieWydarzenia(mContext);

        for (CalendarEvent event : eventyWszystkie) {
            if(event.getCalendarID().equals(calendarID)) {
                eventy.add(event);
            }
        }

        return eventy;
    }

    public List<CalendarEvent> zwrocWydarzeniaPomiedzyDatami(String dataOd, String dataDo, Context mContext){
        List<CalendarEvent> eventy = new ArrayList<CalendarEvent>();
        List<CalendarEvent> eventyWszystkie = this.zwrocWszystkieWydarzenia(mContext);

        for (CalendarEvent event : eventyWszystkie) {
            if(event.getStartDate() < Data.convertDataToMilis(dataDo) && event.getEndDate() > Data.convertDataToMilis(dataOd)) {
                eventy.add(event);
            }
        }

        return eventy;
    }


}
