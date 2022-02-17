package com.habitmining.graphicviewerintegrated.graphicsviewer.Utility;

import java.awt.MediaTracker;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import javax.swing.ImageIcon;
//import logconverter.converter.Converter;
//import logconverter.converter.dto.EventLogDTO;
//import logconverter.converter.dto.SamplingLogDTO;

//import log.converter.logconverter_new.converter.Converter;
//import log.converter.logconverter_new.converter.dto.EventLogDTO;
//import log.converter.logconverter_new.converter.dto.SamplingLogDTO;
import com.habitmining.logconverter.converter.Converter;
import com.habitmining.logconverter.converter.dto.EventLogDTO;
import com.habitmining.logconverter.converter.dto.SamplingLogDTO;
import java.util.List;

/**
 *
 * @author Giovanni
 */
public class Utility {
    // error message
    private final static String ERROR_MESSAGGE_IMAGE = "Loading Image error: %s";
    private final static String ERROR_MESSAGGE_EMPTY = "Invalid input, empty file: %s";
    private final static String ERROR_MESSAGGE_FORMAT = "Invalid input, invalid line format: line %d, file %s";

    // sensor file
    private final static int SENSOR_LINE_WITHOUT_OBJECT = 5;
    private final static int SENSOR_LINE_WITH_OBJECT = 6;
    private final static String SENSOR_LINE_REGEX_COORDINATE = "\\d+";
    private final static String SENSOR_LINE_END_VALUE = "\n";
    private final static String SENSOR_SPLITTER_VALUE = "\t";
    
    private final static int SENSOR_ID_INDEX = 0;
    private final static int SENSOR_X_POSITION_INDEX = 1;
    private final static int SENSOR_Y_POSITION_INDEX = 2;
    private final static int SENSOR_FLOOR_POSITION_INDEX = 3;
    private final static int SENSOR_ROOM_POSITION_INDEX = 4;
    private final static int SENSOR_OBJECT_POSITION_INDEX = 5;
    
    // log file
    private final static int LOG_LINE_TOTAL_VALUES_WITHOUT_NOTE = 4;
    private final static int LOG_LINE_TOTAL_VALUES_WITH_NOTE = 5;
    private final static String LOG_LINE_END_VALUE = "\n";
    private final static String LOG_LINE_SPLIT_VALUE = "\t";

    private final static int LOG_SENSOR_DATE_INDEX = 0;
    private final static int LOG_SENSOR_TIME_INDEX = 1;
    private final static int LOG_SENSOR_ID_INDEX = 2;
    private final static int LOG_SENSOR_STATUS_INDEX = 3;
    private final static int LOG_SENSOR_NOTE_INDEX = 4;




    
    // load the background image
    public static ImageIcon loadImage(String path) throws FileNotFoundException {
        ImageIcon imageIcon = new ImageIcon(path);
        
        /*
         * Returns the status of the image loading operation
         * MediaTracker.COMPLETE indicating that the downloading of media was completed successfully.
         */
        if(imageIcon.getImageLoadStatus() !=  MediaTracker.COMPLETE) {
            String message = String.format(ERROR_MESSAGGE_IMAGE, path);
            throw new FileNotFoundException(message);
        }

        return imageIcon;
    }
    
    
    // load the sensors from the sensor file
    public static HashMap<String, Sensor> loadSensors(String path) throws FileNotFoundException, IOException, Exception {
        HashMap<String, Sensor> sensorMap = new HashMap<>();
        
        FileReader fr = null;
        BufferedReader br = null;
        int lineCounter = 0;
        
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);

            String currentLine = br.readLine(); // the first line is the header
            lineCounter++;
            if( currentLine == null ) {
                String message = String.format(ERROR_MESSAGGE_EMPTY, path);
                throw new Exception(message);
            }
            
            
            while( (currentLine = br.readLine()) != null ) { // sensors start from the second line
                currentLine = currentLine.replaceAll(SENSOR_LINE_END_VALUE, "");
                lineCounter++;

                // split the information of the sensor
                String[] splittedLine = currentLine.split(SENSOR_SPLITTER_VALUE);


                // chek if it is a valid input file
                if( (splittedLine.length != SENSOR_LINE_WITH_OBJECT && splittedLine.length != SENSOR_LINE_WITHOUT_OBJECT)
                        || !splittedLine[SENSOR_X_POSITION_INDEX].matches(SENSOR_LINE_REGEX_COORDINATE)
                        || !splittedLine[SENSOR_Y_POSITION_INDEX].matches(SENSOR_LINE_REGEX_COORDINATE) ) {
                    String message = String.format(ERROR_MESSAGGE_FORMAT, lineCounter, path);
                    throw new Exception(message);
                }

                // get the information
                String sensorID = splittedLine[SENSOR_ID_INDEX];
                int sensorPositionX = Integer.parseInt( splittedLine[SENSOR_X_POSITION_INDEX] );
                int sensorPositionY = Integer.parseInt( splittedLine[SENSOR_Y_POSITION_INDEX] );
                String sensorFloor = splittedLine[SENSOR_FLOOR_POSITION_INDEX];
                String sensorRoom = splittedLine[SENSOR_ROOM_POSITION_INDEX];
                String sensorObject = "";
                if( splittedLine.length == SENSOR_LINE_WITH_OBJECT ) sensorObject = splittedLine[SENSOR_OBJECT_POSITION_INDEX];


                
                // create the data structure for the sensor
                Sensor sensor = new Sensor(sensorID, sensorPositionX, sensorPositionY, sensorFloor, sensorRoom, sensorObject);
                sensorMap.put(sensorID, sensor); 
            }
            
        } finally {
            if( fr != null ) fr.close();
            if( br != null ) br.close();
        }
        
        return sensorMap;
    }
    
    
    // load the log files
    public static List<LogLine> loadLog(String path, Set<String> sensors) throws FileNotFoundException, IOException, ParseException, Exception {
        return loadLogs( new String[]{path}, sensors );
    }
    
    //public static ArrayList<LogLine> loadLogs(String[] paths, LogFileType fileType) throws FileNotFoundException, ParseException, IOException, Exception {
    public static List<LogLine> loadLogs(String[] paths, Set<String> sensors) throws FileNotFoundException, ParseException, IOException, Exception {
        ArrayList<EventLogDTO> eventLog = new ArrayList<>(); // contains the ordered sequence of the sensors status
        
        for(String path : paths) {
            FileReader fr = null;
            BufferedReader br = null;
            int lineCounter = 0;
            
            try {
                fr = new FileReader(path);
                br = new BufferedReader(fr);

                String currentLine;
                while( (currentLine = br.readLine()) != null ) {
                    lineCounter++;
                    
                    currentLine = currentLine.replaceAll(LOG_LINE_END_VALUE, "");

                    // split the information of the sensor
                    String[] splittedLine = currentLine.split(LOG_LINE_SPLIT_VALUE);
                    
                    // chek if it is a valid input file
                    if( splittedLine.length != LOG_LINE_TOTAL_VALUES_WITHOUT_NOTE && splittedLine.length != LOG_LINE_TOTAL_VALUES_WITH_NOTE ) {
                        String message = String.format(ERROR_MESSAGGE_FORMAT, lineCounter, path);
                        throw new Exception(message);
                    } 
                    
                    // get the information
                    String sensorID = splittedLine[LOG_SENSOR_ID_INDEX];
                    String sensorStatus = splittedLine[LOG_SENSOR_STATUS_INDEX];
                    String sensorDate = splittedLine[LOG_SENSOR_DATE_INDEX];
                    String sensorTime = splittedLine[LOG_SENSOR_TIME_INDEX];
                    String sensorNote = "";
                    if( splittedLine.length == LOG_LINE_TOTAL_VALUES_WITH_NOTE ) sensorNote = splittedLine[LOG_SENSOR_NOTE_INDEX];

                    if( sensors.contains(sensorID) && sensorStatus.equals(Sensor.MOTION_SENSOR_STATUS_ON) ) {
                        EventLogDTO e = new EventLogDTO(sensorID, sensorDate, sensorTime, sensorStatus, sensorNote);
                        eventLog.add(e);
                    }
                    
                }
           
            } finally {
                if( fr != null ) fr.close();
                if( br != null ) br.close();
            }
            
            // chek if it is an empty file
            if(lineCounter == 0) {
                String message = String.format(ERROR_MESSAGGE_EMPTY, lineCounter, path);
                throw new Exception(message);
            }
        }
        
        
        List<LogLine> log = new ArrayList<>(); // contains the ordered sequence of the sensors status

        
        int sampling = PropertyReader.getSamplingValue();
        List<SamplingLogDTO> samplingLog = Converter.samplingLog(eventLog, sampling);
        for( SamplingLogDTO s : samplingLog ) {
            Date logLineDate = LogLine.castStringToDate(s.getDate(), s.getTime());
            LogLine ll = new LogLine(logLineDate, s.getSensorID(), s.getValue(), s.getNote(), s.getTimestamp());
            log.add(ll);
        }
        
       
        return log;
    }
    
    

}

