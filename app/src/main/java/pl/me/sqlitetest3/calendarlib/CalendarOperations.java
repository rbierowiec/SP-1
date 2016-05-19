package pl.me.sqlitetest3.calendarlib;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import pl.me.sqlitetest3.additional.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java.util.ArrayList;

/**
 * Created by Rafael on 2016-04-22.
 */
public class CalendarOperations {
    private Context mContext;

    public CalendarOperations(Context _mContext){
        this.mContext = _mContext;
    }

    public List<AndroidCalendar> zwrocKalendarze() {
        String[] projection = new String[]{"_id", "name", "account_type"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = this.mContext.getContentResolver();
        Cursor cursor = contentResolver.query(calendars, null, null, null, null);

        List<AndroidCalendar> kalendarze = new ArrayList<AndroidCalendar>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                AndroidCalendar kalendarz = new AndroidCalendar();

                kalendarz.setCalendarID(cursor.getString(0));
                kalendarz.setCalendarName(cursor.getString(1));
                kalendarz.setCalendarType(cursor.getString(2));

                kalendarze.add(kalendarz);

            } while (cursor.moveToNext());
        }

        return kalendarze;
    }

    public List<CalendarEvent> zwrocWszystkieWydarzenia() {
        String[] projection = new String[]{"_id", "title", "dtstart", "dtend", "calendar_id"};
        Uri calendars = Uri.parse("content://com.android.calendar/events");

        ContentResolver contentResolver = this.mContext.getContentResolver();
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

    public List<CalendarEvent> zwrocWydarzeniaDlaKalendarza(String calendarID){
        List<CalendarEvent> eventy = new ArrayList<CalendarEvent>();
        List<CalendarEvent> eventyWszystkie = this.zwrocWszystkieWydarzenia();

        for (CalendarEvent event : eventyWszystkie) {
            if(event.getCalendarID().equals(calendarID)) {
                eventy.add(event);
            }
        }

        return eventy;
    }

    public List<CalendarEvent> zwrocWydarzeniaPomiedzyDatami(String dataOd, String dataDo){
        List<CalendarEvent> eventy = new ArrayList<CalendarEvent>();
        List<CalendarEvent> eventyWszystkie = this.zwrocWszystkieWydarzenia();

        for (CalendarEvent event : eventyWszystkie) {
            if(event.getStartDate() < Data.convertDataToMilis(dataDo) && event.getEndDate() > Data.convertDataToMilis(dataOd)) {
                eventy.add(event);
            }
        }

        return eventy;
    }

    public int zwrocCzasWolnyOdDaty(int okresPoszukiwan){
        final long ONE_HOUR_IN_MILLIS=60000*60; //millisecs
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int czasWolny = 24*60;
        okresPoszukiwan *= ONE_HOUR_IN_MILLIS;
        Date curDate = new Date();

        java.util.Calendar date = java.util.Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date endDate = new Date(t + (okresPoszukiwan));
        System.out.println(curDate);
        System.out.println(endDate);
        List<CalendarEvent> eventy = this.zwrocWydarzeniaPomiedzyDatami(df.format(curDate), df.format(endDate));

        for (CalendarEvent event : eventy) {
            czasWolny = Math.round((event.getStartDate() - curDate.getTime()) / (ONE_HOUR_IN_MILLIS/60));
            break;
        }

        return czasWolny;
    }

    public int wyliczSredniCzasAktywnosci(String slowoKluczowe){
        final long ONE_MINUTE_IN_MILLIS=60000; //millisecs
        float sumaCzasow = 0;
        int iloscWydarzen = 0;

        List<CalendarEvent> eventy = new ArrayList<CalendarEvent>();
        List<CalendarEvent> eventyWszystkie = this.zwrocWszystkieWydarzenia();

        for (CalendarEvent event : eventyWszystkie) {
            if(event.getTitle().toLowerCase().contains(slowoKluczowe.toLowerCase())) {
                sumaCzasow += (event.getEndDate() - event.getStartDate());
                iloscWydarzen++;
            }
        }

        if (iloscWydarzen > 0)
            return Math.round(sumaCzasow / iloscWydarzen / ONE_MINUTE_IN_MILLIS);
        else
            return 0;
    }


}
