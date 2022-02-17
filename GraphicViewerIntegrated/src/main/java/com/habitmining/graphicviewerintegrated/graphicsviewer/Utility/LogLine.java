package com.habitmining.graphicviewerintegrated.graphicsviewer.Utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Giovanni
 */
public class LogLine implements Comparable<LogLine> {
    private final static String LOG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
      
    private final Date date;
    private final String sensorID;
    private final String sensorValue;
    private final String note;
    private final long timestamp;

    public LogLine(Date date, String sensorID, String sensorValue, String note, long timestamp) {
        this.date = date;
        this.sensorID = sensorID;
        this.sensorValue = sensorValue;
        this.note = note;
        this.timestamp = timestamp;
    }

    public Date getDate() {
        return date;
    }
    
    public long getTimestamp() {
        return timestamp;
    }

    public String getSensorID() {
        return sensorID;
    }

    public String getSensorValue() {
        return sensorValue;
    }

    public String getNote() {
        return note;
    }
    
    @Override
    public int compareTo(LogLine o) {
        return getDate().compareTo(o.getDate());
    }

    
    /*
     * return a date if String date and String time have a valid format
     * throws ParseException otherwise 
     * 
     * NOTA
     * the input time as the microseconds but are considered only the milliseconds
     * java only manages until milliseconds
    */
    public static Date castStringToDate(String date, String time) throws ParseException {
        String datetime = date + " " + time;
        
        if( !datetime.contains(".") ) datetime += ".0";
        if( datetime.length() > LOG_DATE_FORMAT.length() ) datetime = datetime.substring(0, LOG_DATE_FORMAT.length());

        DateFormat dateFormat = new SimpleDateFormat(LOG_DATE_FORMAT); 
        Date d = dateFormat.parse( datetime );

        return d;
        
    }
}
