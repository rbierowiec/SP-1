package pl.me.sqlitetest3.additional;

/**
 * Created by Rafael on 2016-04-22.
 */
public class CalendarEvent {
    private String eventID;
    private String Title;
    private String calendarID;
    private Long startDate;
    private Long endDate;

    public String getEventID(){
        return this.eventID;
    }

    public void setEventID(String _eventID){
        this.eventID = _eventID;
    }

    public String getTitle(){
        return this.Title;
    }

    public void setTitle(String _title){
        this.Title = _title;
    }

    public String getCalendarID(){
        return this.calendarID;
    }

    public void setCalendarID(String _calendarName){
        this.calendarID = _calendarName;
    }

    public Long getStartDate(){
        return this.startDate;
    }

    public void setStartDate(Long _startDate){
        this.startDate = _startDate;
    }
    public Long getEndDate(){
        return this.endDate;
    }

    public void setEndDate(Long _endDate){
        this.endDate = _endDate;
    }
}
