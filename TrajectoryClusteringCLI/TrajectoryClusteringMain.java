import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.io.InputStream;
import java.util.Properties;


/**
 *
 * @author Lucia
 */

public class TrajectoryClusteringMain {
    
    private static String path_sensors;
    private static String[] path_logs;
    private static String[] path_day_logs;

    // parameters to change
    public static int SAMPLING_PERIOD;
    public static int MDL_COST_ADVANTAGE;

    public static double THRESHOLD_STOP_AREA;
    public static double THRESHOLD_AREA_MOVEMENT;

    public static double WEIGHT_INDEX_AREA;
    public static double WEIGHT_INDEX_MOVEMENT;
    public static double WEIGHT_INDEX_STOP;

    
    public static void main(String[] args) throws Exception { 
        if(args.length != 7){
		System.out.println("Usage: TrajectoryClusteringMain.java SAMPLING_PERIOD MDL_COST_ADVANTAGE THRESHOLD_STOP_AREA THRESHOLD_AREA_MOVEMENT WEIGHT_INDEX_AREA WEIGHT_INDEX_MOVEMENT WEIGHT_INDEX_STOP");
		System.out.println("example (standard parameters): TrajectoryClusteringMain.java 5000 25 0.33 0.66 0.33 0.33 0.33");
		System.exit(1);
	} 
	if(Double.parseDouble(args[3]) > 1){
		System.out.println("THRESHOLD_AREA_MOVEMENT must be less than or equal to 1");
                System.exit(1);
	}
	if(Double.parseDouble(args[2]) > Double.parseDouble(args[3])){
		System.out.println("THRESHOLD_AREA_MOVEMENT must be greater than or equal to THRESHOLD_STOP_AREA");
                System.exit(1);
	}
        if(Double.parseDouble(args[4])+Double.parseDouble(args[5])+Double.parseDouble(args[6]) != 1){
		if(Double.parseDouble(args[4])+Double.parseDouble(args[5])+Double.parseDouble(args[6]) == 0.99){
			if(!(args[4].equals(args[5])) || !(args[4].equals(args[6]))){
				System.out.println("WEIGHT_INDEX_AREA + WEIGHT_INDEX_MOVEMENT + WEIGHT_INDEX_STOP must be equal to 1");
                		System.exit(1);
			}
		}
		else{
			System.out.println("WEIGHT_INDEX_AREA + WEIGHT_INDEX_MOVEMENT + WEIGHT_INDEX_STOP must be equal to 1");
                	System.exit(1);
		}
	}


	SAMPLING_PERIOD = Integer.parseInt(args[0]);
	MDL_COST_ADVANTAGE = Integer.parseInt(args[1]);
	THRESHOLD_STOP_AREA = Double.parseDouble(args[2]);
	THRESHOLD_AREA_MOVEMENT = Double.parseDouble(args[3]);
	WEIGHT_INDEX_AREA = Double.parseDouble(args[4]);
	WEIGHT_INDEX_MOVEMENT = Double.parseDouble(args[5]);
	WEIGHT_INDEX_STOP = Double.parseDouble(args[6]);

	System.out.println("SAMPLING_PERIOD		" + args[0] + "\n" +
			   "MDL_COST_ADVANTAGE	" + args[1] + "\n" +
			   "THRESHOLD_STOP_AREA	" + args[2] + "\n" +
			   "THRESHOLD_AREA_MOVEMENT	" + args[3] + "\n" +
			   "WEIGHT_INDEX_AREA	" + args[4] + "\n" +
			   "WEIGHT_INDEX_MOVEMENT	" + args[5] + "\n" +
			   "WEIGHT_INDEX_STOP	" + args[6]);

	System.out.println("\nExecuting...\n");
		

        path_sensors = "aruba_sensors_map.csv";

	path_logs = new String[1];
	path_logs[0] = "data"; 

        executeOpenCommand();	// create file "dataset_clustered.csv"

	addStartComplete();	// create file "dataset_clustered_with_start_complete.csv"
	
    }
       
    private static void executeOpenCommand() throws FileNotFoundException, ParseException, Exception {
        final HashMap<String, Sensor> sensorMap = Utility.loadSensors(path_sensors);
        final List<LogLine> logsList = Utility.loadLogs(path_logs, sensorMap.keySet());
        
        // create the simulation manager
        SimulationManager simulationManager = new SimulationManager(sensorMap, logsList);
        
        ButtonTrajectoryListener btl = new ButtonTrajectoryListener(simulationManager);
        btl.getSubtrajectories();
        btl.saveDisco(path_day_logs);
    }

    private static void addStartComplete() {
	String inputFile = "results\\dataset_clustered.csv";
	String outputFile = "results\\dataset_clustered_with_start_complete.csv";

	FileReader fr = null;
        BufferedReader br = null;
        
	List<String> listEventsStartComplete = new ArrayList<String>();
        try {
            fr = new FileReader(inputFile);
            br = new BufferedReader(fr);

            String currentLine;
            br.readLine();
            while( (currentLine = br.readLine()) != null ) {    
                String[] splittedLine = currentLine.split(",");

		String eventStart = splittedLine[0] + "," + splittedLine[1] + "," + splittedLine[3] + "," + "start";
		String eventComplete = splittedLine[0] + "," + splittedLine[2] + "," + splittedLine[3] + "," + "complete";
                
		listEventsStartComplete.add(eventStart);
   		listEventsStartComplete.add(eventComplete);
            }    
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(fr != null){
                    fr.close();
                } 
                if(br != null){
                    br.close();
                }
            } catch (IOException ex) {
                    Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        PrintWriter pw = null;
        FileWriter fw = null;
        try {
            File workingFile = new File(outputFile);
            workingFile.createNewFile();
            System.err.println(workingFile.getAbsolutePath());
            pw = new PrintWriter(new FileWriter(workingFile));
	    pw.println("case,timestamp,event,transactionType");
            
            for(int i=0; i<listEventsStartComplete.size(); i++) {
                pw.println(listEventsStartComplete.get(i));
                pw.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(fw != null) {
                    fw.close();
                }
                if(pw != null) {
                    pw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

	File fileToDelete = new File("dataset_clustered.csv");
	fileToDelete.delete(); 
    }  

}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class ButtonTrajectoryListener {

    private final static String EXTENSION_CSV = ".csv";

    public final static String CLASSIFICATION_TYPE_STOP = "STOP";
    public final static String CLASSIFICATION_TYPE_AREA = "AREA";
    public final static String CLASSIFICATION_TYPE_MOVEMENT = "MOVEMENT";
    public final static String CLASSIFICATION_TYPE_NOT_DEFINED = "NOT DEFINED";

    private final String WORKING_FOLDER = "results";

    private final ArrayList<SubtrajectoryDTO> subtrajectories;

    private final SimulationManager simulationManager;


    public ButtonTrajectoryListener(SimulationManager simulationManager) {
        this.subtrajectories = new ArrayList<>();
        this.simulationManager = simulationManager;
    }

    
    private String matchType(String subtrajectoryType) {
        String type = CLASSIFICATION_TYPE_NOT_DEFINED;
        if (subtrajectoryType.equals(SubtrajectoryDTO.TYPE_STAY)) {
            type = CLASSIFICATION_TYPE_STOP;
        } else if (subtrajectoryType.equals(SubtrajectoryDTO.TYPE_AREA)) {
            type = CLASSIFICATION_TYPE_AREA;
        } else if (subtrajectoryType.equals(SubtrajectoryDTO.TYPE_MOVEMENT)) {
            type = CLASSIFICATION_TYPE_MOVEMENT;
        }
        return type;
    }

    public void getSubtrajectories() {         
        subtrajectories.clear();   
	if (simulationManager.calculateSubtrajectories() != null) {
        	subtrajectories.addAll(simulationManager.calculateSubtrajectories());
	}
        /*for(SubtrajectoryDTO subtraj : subtrajectories) {
            System.out.println("Subtrajectory: start index: " + subtraj.getStartIndex() +
                                " end index: " + subtraj.getEndIndex() +
                                " type: " + subtraj.getType() +
                                " descrption: " + subtraj.getDescription() +
                                " start ms: " + subtraj.getStartMillisecond() +
                                " end ms: " + subtraj.getEndMillisecond());
            for(PointDTO point : subtraj.getSubtrajectoryPoints()) {
                System.out.println("\tPoint: point id: " + point.getPointID() + 
                                    " point dimension: " + point.getPointDimension() +
                                    " point coordinates (x,y): " + point.getPointCoordinates()[0] + "," + point.getPointCoordinates()[1] +
                                    " point timestamp: " + point.getTimestamp());
            }
        }*/
    }

    public void saveDisco(String[] paths) throws Exception {
        String path = null;
        //************** PROM_PATCH**************
        String workingPath = WORKING_FOLDER;
        PrintWriter promWriter = null;
        //***************************************
        FileWriter fw = null;
        try {
            path = "dataset_clustered.csv";
            //*****************************************************
            workingPath += File.separator + "dataset_clustered.csv";
            //*****************************************************
            fw = new FileWriter(path, false);
            //*******************************************
            File workingFile = new File(workingPath);
            workingFile.getParentFile().mkdirs();
            workingFile.createNewFile();
            System.err.println(workingFile.getAbsolutePath());
            promWriter = new PrintWriter(new FileWriter(workingFile));
	    promWriter.println("case,timein,timeend,event");
            //*******************************************
            BufferedWriter bw = new BufferedWriter(fw);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime ldt = null;
            int id = 0;
            for (SubtrajectoryDTO st : subtrajectories) {
                if (st.getType().equals(SubtrajectoryDTO.TYPE_MOVEMENT)) {
                    continue;
                }
                LocalDateTime startDateTime = Instant.ofEpochMilli(st.getStartMillisecond()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime endDateTime = Instant.ofEpochMilli(st.getEndMillisecond()).atZone(ZoneId.systemDefault()).toLocalDateTime();

//                    id = 0;
                String startDate = startDateTime.format(formatter);
                String endDate = endDateTime.format(formatter);
                String type = matchType(st.getType());
                String description = st.getDescription();
                

                if (ldt != null) {                    
                    if (startDateTime.getDayOfMonth() != ldt.getDayOfMonth()) {
                        id = id + 1;
                    }
                }

                ///// String[] allSensorStatus = getAllSensorStatus(paths, startDate, endDate);
                
                ldt = startDateTime;

                String riga = id + "," + startDate + "," + endDate + "," + "< " + type + " " + description + " >";
                bw.write(riga);
                bw.flush();
                //****************************************
                promWriter.println(riga);
                promWriter.flush();
                //****************************************
            }

        } catch (IOException ex) {
            Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
                //*****************************************
                if (promWriter != null) {
                    promWriter.close();
                }
                //*****************************************
            } catch (IOException ex) {
                Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class SimulationManager {

    private final HashMap<String, Sensor> sensorMap;
    private final List<LogLine> log;
    

    public SimulationManager(HashMap<String, Sensor> sensorMap, List<LogLine> logs) {
        this.sensorMap = sensorMap;
        this.log = logs;
    }
    
    
    public synchronized ArrayList<SubtrajectoryDTO> calculateSubtrajectories() {
        
        ArrayList<PointDTO> trajectory = new ArrayList<>();
        for( LogLine ll : log ) {
            String pointID = ll.getSensorID();
            double[] pointCoordinates = new double[]{ sensorMap.get(pointID).getSensorPositionX(), sensorMap.get(pointID).getSensorPositionY() };
            long timestamp = ll.getTimestamp();
            PointDTO p = new PointDTO(pointID, pointCoordinates, timestamp);
            /* System.out.println("Point: point id: " + p.getPointID() + 
                                " point dimension: " + p.getPointDimension() +
                                " point coordinates (x,y): " + p.getPointCoordinates()[0] + "," + p.getPointCoordinates()[1] +
                                " point timestamp: " + p.getTimestamp()); */
            trajectory.add(p);
        }
        
        SensorMappingDTO mapping = new SensorMappingDTO();
        for(Sensor s : sensorMap.values() ) {
            mapping.put(s.getSensorID(), s.getRoom(), s.getObject());
        }
         
        
        ArrayList<Integer> listaCaracteristicPoints = TraclusSplitTrajectory.partitionTrajectory( trajectory, TrajectoryClusteringMain.MDL_COST_ADVANTAGE);
        /*for(int i=0; i<listaCaracteristicPoints.size(); i++){
            System.out.println(listaCaracteristicPoints.get(i));
        }*/
	ArrayList<SubtrajectoryDTO> listaSubtrajectories = null;
        if (trajectory.size() != 0) {
        	listaSubtrajectories = ClassificatorTrajectory.classifyTrajectory(
                	trajectory, listaCaracteristicPoints, mapping,
                	TrajectoryClusteringMain.WEIGHT_INDEX_STOP, TrajectoryClusteringMain.WEIGHT_INDEX_MOVEMENT, TrajectoryClusteringMain.WEIGHT_INDEX_AREA, TrajectoryClusteringMain.THRESHOLD_STOP_AREA, TrajectoryClusteringMain.THRESHOLD_AREA_MOVEMENT
        	);
        }
        return listaSubtrajectories;
    }

}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////

class LogLine implements Comparable<LogLine> {
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
    
    public String printInfo() {
        return "Logline: sensor id: " + getSensorID() +
                " sensor value: " + getSensorValue() +
                " date: " + getDate() +
                " timestamp: " + getTimestamp() +
                " note: " + getNote();
    }
}


/////////////////////////////////////////////////////////////////////////////////////////////////////////////

class Sensor {
    public final static String MOTION_SENSOR_STATUS_ON = "ON";
    public final static String MOTION_SENSOR_STATUS_OFF = "OFF";
    
    public final static String DOOR_SENSOR_STATUS_OPEN = "OPEN"; /////
    public final static String DOOR_SENSOR_STATUS_CLOSE = "CLOSE"; /////


    
    private final String sensorID;
    private final int sensorPositionX;
    private final int sensorPositionY;
    private final String sensorFloor;
    private final String room;
    private final String object;

    public Sensor(String sensorID, int sensorPositionX, int sensorPositionY, String sensorFloor, String room, String object) {
        this.sensorID = sensorID;
        this.sensorPositionX = sensorPositionX;
        this.sensorPositionY = sensorPositionY;
        this.sensorFloor = sensorFloor;
        this.room = room;
        this.object = object;
    }

    public String getSensorID() {
        return sensorID;
    }

    public int getSensorPositionX() {
        return sensorPositionX;
    }

    public int getSensorPositionY() {
        return sensorPositionY;
    }

    public String getSensorFloor() {
        return sensorFloor;
    }

    public String getRoom() {
        return room;
    }

    public String getObject() {
        return object;
    }
    
    public String printInfo(){
        return "Sensor id: " + getSensorID() +
                " X: " + getSensorPositionX() +
                " Y: " + getSensorPositionY() +
                " floor: " + getSensorFloor() +
                " room: " + getRoom() +
                " object: " + getObject();
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class Utility {
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
                        /* System.out.println("Event: sensor id: " + e.getSensorID() +
                                            " date time: " + e.getDatatimeString() +
                                            " value: " + e.getValue() +
                                            " note: " + e.getNote()); */
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

        
        List<SamplingLogDTO> samplingLog = Converter.samplingLog(eventLog, TrajectoryClusteringMain.SAMPLING_PERIOD);
        for( SamplingLogDTO s : samplingLog ) {
            Date logLineDate = LogLine.castStringToDate(s.getDate(), s.getTime());
            LogLine ll = new LogLine(logLineDate, s.getSensorID(), s.getValue(), s.getNote(), s.getTimestamp());
            //System.out.println(ll.printInfo());
            log.add(ll);
        }
        
       
        return log;
    }
}

class Converter {

    public static List<SamplingLogDTO> samplingLog(ArrayList<EventLogDTO> lista, int sample) {
        List<SamplingLogDTO> listaCampionata = new LinkedList<SamplingLogDTO>();

        //testa( lista );
        Collections.sort(lista, new Comparator<EventLogDTO>() {
            @Override
            public int compare(EventLogDTO e1, EventLogDTO e2) {
                return e1.getDatatime().compareTo(e2.getDatatime());
            }
        });

        
        //PATCH DANIELE SORA _ 20180209 _ WORKING
        int index = 0; //element list
        EventLogDTO currentEvent = lista.get(index); //current element
        long last = lista.get(lista.size() - 1).getDatatimeMilliseconds(); //last timestamp
        long curr = currentEvent.getDatatimeMilliseconds(); // current element tmestamp

        index++;
	if(lista.size()==1) index = 0;
        EventLogDTO next = lista.get(index); //next element
        while (curr < last) {
            while (true) {
		if (curr < next.getDatatimeMilliseconds()) {
                	//the next element has to come, coping current to sample
                	listaCampionata.add(new SamplingLogDTO(currentEvent.getSensorID(), currentEvent.getDatatime().toLocalDate().toString(),
                		currentEvent.getDatatime().toLocalTime().toString(), currentEvent.getValue(), curr, ""));

	                curr += sample;		    
                } else {
                    //we are ready for the next, let's switch
                    curr = next.getDatatimeMilliseconds();

                    currentEvent = next;
                    if (index < lista.size() - 1) {
                        //It's not the last element
                        index++;
                        next = lista.get(index);
                        break;
                    } else {
                        //managing last element and return the list
                        listaCampionata.add(new SamplingLogDTO(currentEvent.getSensorID(), currentEvent.getDatatime().toLocalDate().toString(),
                                currentEvent.getDatatime().toLocalTime().toString(), currentEvent.getValue(), curr, ""));

                        return listaCampionata;
                    }

                }
            }
        }

        return listaCampionata;
    }

   
    public static ArrayList<EventLogDTO> loadEventLog(String path) throws FileNotFoundException, IOException {
        ArrayList<EventLogDTO> lista = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(path));

        String line;
        while ((line = br.readLine()) != null) {
            line = line.replace("\n", "");
            String[] splittedLine = line.split("\t");

            String sensorID = splittedLine[2];
            String date = splittedLine[0];
            String time = splittedLine[1];
            String value = splittedLine[3];
            String note = "";
            if (splittedLine.length == 4) {
                note = splittedLine[3];
            }

            if (value.equals("ON") && sensorID.subSequence(0, 1).equals("M")) {
                EventLogDTO e = new EventLogDTO(sensorID, date, time, value, note);
                lista.add(e);
            }
        }

        br.close();

        return lista;
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class ClassificatorTrajectory {

    public static ArrayList<SubtrajectoryDTO> classifyTrajectory(ArrayList<PointDTO> trajectoryPoints, ArrayList<Integer> caracteristicPoints, SensorMappingDTO mapping,
            double weightIndexStop, double weightIndexMovement, double weightIndexArea, double threshold_stop_area, double threshold_area_movement) {
       
        ArrayList<SubtrajectoryDTO> list = new ArrayList<>();
        
        int startIndex = 0;
        for(int n = 1; n < caracteristicPoints.size(); n++ ) {
            int endIndex = caracteristicPoints.get(n);
            
            ArrayList<PointDTO> subtrajectoryPoints = new ArrayList<>(trajectoryPoints.subList(startIndex, endIndex+1));
           
            SubtrajectoryDTO subtrajectory = classifySubtrajectory( subtrajectoryPoints, mapping, startIndex, endIndex, weightIndexStop, weightIndexMovement, weightIndexArea, threshold_stop_area, threshold_area_movement );
            list.add( subtrajectory );
            
            startIndex = endIndex;
        }

        return list;
    }
    
    
    private static SubtrajectoryDTO classifySubtrajectory(ArrayList<PointDTO> subtrajectory, SensorMappingDTO mapping, int startIndex, int endIndex, 
            double weightIndexStop, double weightIndexMovement, double weightIndexArea, double threshold_stop_area, double threshold_area_movement) {
        
        String description = "";
        String type = "";
        long startMillisecond = subtrajectory.get(0).getTimestamp();
        long endMillisecond = subtrajectory.get(subtrajectory.size()-1).getTimestamp();
        
        double indiceStop = weightIndexStop * calculateStopIndex(subtrajectory);
        double indiceMovement = weightIndexMovement * calculateMovementIndex(subtrajectory);
        double indiceArea = weightIndexArea * calculateAreaIndex(subtrajectory);
        
        double indice = indiceStop + indiceMovement + indiceArea;
        if( indice <= threshold_stop_area ) {
            HashMap<String, Integer> compressed = compressSubrajectory( subtrajectory );
            String maxKey = Collections.max(compressed.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
            String room = mapping.getFromK1(maxKey);
            String object = mapping.getFromK2(maxKey);

            type = SubtrajectoryDTO.TYPE_STAY;
            description = room + "_" + object + "_" + maxKey;
            
        } else if( indice > threshold_stop_area && indice < threshold_area_movement ){
            HashMap<String, Integer> compressed = compressSubrajectory( subtrajectory );
            String maxKey = Collections.max(compressed.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
            String room = mapping.getFromK1(maxKey);
            
            type = SubtrajectoryDTO.TYPE_AREA;    
            description = room;
            
        } else if( indice >= threshold_area_movement ) {
            type = SubtrajectoryDTO.TYPE_MOVEMENT;
            description = "";
            
        } else{
            type = SubtrajectoryDTO.TYPE_NOT_DEFINED;
            description = "";
        }
        
        return new SubtrajectoryDTO(startIndex, endIndex, subtrajectory, startMillisecond, endMillisecond, type, description);
    }
    
    private static HashMap<String, Integer> compressSubrajectory(ArrayList<PointDTO> subtrajectory) {
        HashMap<String, Integer> compressed = new HashMap<>();
        
        for(PointDTO p : subtrajectory) {
            int conta = 1;
            if( compressed.containsKey(p.getPointID()) ) conta += compressed.get(p.getPointID());
            compressed.put(p.getPointID(), conta);
        }
        
        return compressed;
    }
    
    
    private static double calculateStopIndex(ArrayList<PointDTO> subtrajectory) {
        double max = 0;
        double total = (double) subtrajectory.size();
        
        HashMap<String, Integer> map = new HashMap<>();
        for(PointDTO p : subtrajectory) {
            int conta = 1;
            if( map.containsKey(p.getPointID()) ) conta += map.get( p.getPointID() );
            map.put( p.getPointID(), conta );
            
            if( conta > max ) max = conta;
        }

        double indice = 1 - ( max / total );
        return indice;
    }
    
    
    private static double calculateMovementIndex(ArrayList<PointDTO> subtrajectory) {
        HashSet<String> distinctSensor = new HashSet<>();
        for(PointDTO p : subtrajectory) {
            distinctSensor.add( p.getPointID() );
        }

        double totalDistinctSensor = distinctSensor.size() - 1;
        double totalSensor = subtrajectory.size() - 1;

        double index = totalDistinctSensor / totalSensor;
        return index;
    }

    
    private static double calculateAreaIndex(ArrayList<PointDTO> subtrajectory) {
        HashMap<String, Integer> map = new HashMap<>();
        for(PointDTO p : subtrajectory) {
            int conta = 1;
            if( map.containsKey(p.getPointID()) ) conta += map.get( p.getPointID() );
            map.put( p.getPointID(), conta );
        }
        
        ArrayList<Integer> values = new ArrayList<>( map.values() );
        Collections.sort(values);
        
        double total = values.size();
        double numeratore = 0;
        double denominatore = 0;
        for( int n = 0; n < total; n++ ) {
            double value = values.get(n);
            numeratore += (n+1) * value;
            denominatore += value;
        }
        
        double giniIndex = ( 2 * numeratore ) / ( total * denominatore );
        giniIndex -= ( total +1 ) / total;
        double index = 1 - giniIndex;
        return index;
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class PointDTO implements Comparable<PointDTO>{
    private final String pointID;
    private final int pointDimension;    // the number of dimensions of a point
    private final double[] pointCoordinates;    // the coordinate of a point
    private final long timestamp;

    public PointDTO(String pointID, double[] pointCoordinates, long timestamp) {
        this.pointID = pointID;
        this.pointDimension = pointCoordinates.length;
        this.pointCoordinates = new double[pointDimension];
        System.arraycopy(pointCoordinates, 0, this.pointCoordinates, 0, pointCoordinates.length);
        this.timestamp = timestamp;
    }

    public String getPointID() {
        return pointID;
    }

    public int getPointDimension() {
        return pointDimension;
    }

    public double[] getPointCoordinates() {
        return pointCoordinates;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public int compareTo(PointDTO o) {
        
        if(getTimestamp() < o.getTimestamp()){
            return -1;
        }else if(getTimestamp() > o.getTimestamp()){
            return 1;
        }
        else return 0;
    }
    
    public String printInfo() {
        return "Point: point id: " + getPointID() + 
                " point dimension: " + getPointDimension() +
                " point coordinates (x,y): " + getPointCoordinates()[0] + "," + getPointCoordinates()[1] +
                " point timestamp: " + getTimestamp();
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class SensorMappingDTO {
    private final HashMap<String, String> mappingRoom;
    private final HashMap<String, String> mappingObject;

    public SensorMappingDTO() {
        this.mappingRoom = new HashMap<>();
        this.mappingObject = new HashMap<>();
    }
    
    public void put(String key, String v1, String v2) {
        mappingRoom.put(key, v1);
        mappingObject.put(key, v2);
    }
    
    public String getFromK1(String key) {
        return mappingRoom.get(key);
    }
    
    public String getFromK2(String key) {
        return mappingObject.get(key);
    }
    
    public Set<String> getKeys() {
        return mappingRoom.keySet();
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class SubtrajectoryDTO {
    public final static String TYPE_MOVEMENT = "trajectoryanalyzer.dto.SubtrajectoryDTO.TYPE_MOVEMENT";
    public final static String TYPE_STAY = "trajectoryanalyzer.dto.SubtrajectoryDTO.TYPE_STAY";
    public final static String TYPE_AREA = "trajectoryanalyzer.dto.SubtrajectoryDTO.TYPE_AREA";
    public final static String TYPE_NOT_DEFINED = "trajectoryanalyzer.dto.SubtrajectoryDTO.TYPE_NOT_DEFINED";
    
    private final int startIndex;
    private final int endIndex;
    private final ArrayList<PointDTO> subtrajectoryPoints;
    private final long startMillisecond;
    private final long endMillisecond;
    private final String type;
    private final String description;
    
   // private final String[] allDoorStatus;
   // private final String[] allTemperatureStatus;

    public SubtrajectoryDTO(int startIndex, int endIndex, List<PointDTO> subtrajectoryPoints, long startMillisecond, long endMillisecond, String type, String description/*, String[] allDoorStatus, String[] allTemperatureStatus*/) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.subtrajectoryPoints = new ArrayList<>(subtrajectoryPoints);
        this.startMillisecond = startMillisecond;
        this.endMillisecond = endMillisecond;
        this.type = type;
        this.description = description;
       // this.allDoorStatus = allDoorStatus;
       // this.allTemperatureStatus = allTemperatureStatus;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public String getType() {
        return type;
    }

    public ArrayList<PointDTO> getSubtrajectoryPoints() {
        return new ArrayList<>(subtrajectoryPoints);
    }
    
    public long getStartMillisecond() {
        return startMillisecond;
    }

    public long getEndMillisecond() {
        return endMillisecond;
    }

    public String getDescription() {
        return description;
    } 

    public String printInfo() {
        return "Subtrajectory: start index: " + getStartIndex() +
                " end index: " + getEndIndex() +
                " type: " + getType() +
                " descrption: " + getDescription() +
                " start ms: " + getStartMillisecond() +
                " end ms: " + getEndMillisecond();
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////
class TraclusSplitTrajectory {
    public static ArrayList<Integer> partitionTrajectory(ArrayList<PointDTO> trajectory, int MDL_COST_ADWANTAGE) {
        ArrayList<Integer> cacteriscticPoints = new ArrayList<>();
        /*
        JProgressBar p = new JProgressBar();
        p.setValue(0);
        p.setMinimum(0);
        p.setMaximum(trajectory.size()-1);
        p.setStringPainted(true);
        Thread t = new Thread(() -> {
            JOptionPane.showMessageDialog(null, p);
        });
        t.start();
        */
        

        int nPoints = trajectory.size();
        int startIndex = 0;
        int length;
        int fullPartitionMDLCost;
        int partialPartitionMDLCost;

        cacteriscticPoints.add(0); // add the start point of a trajectory
        
        for(;;) {
            fullPartitionMDLCost = 0;
            partialPartitionMDLCost = 0;

            for( length = 1; startIndex + length < nPoints; length++ ) {
                // compute the total length of a trajectory
                fullPartitionMDLCost += computeModelCost(trajectory, startIndex+length - 1, startIndex + length);

                // compute the sum of (1) the length of a cluster component and 
                // (2) the perpendicular and angle distances
                partialPartitionMDLCost = computeModelCost(trajectory, startIndex, startIndex + length) +
                        computeEncodingCost(trajectory, startIndex, startIndex + length);

                //p.setValue(startIndex+length-1);
                
                if( fullPartitionMDLCost + MDL_COST_ADWANTAGE < partialPartitionMDLCost ) {
                    System.out.println("OK");
                    cacteriscticPoints.add(startIndex+length-1);
                    if (length<2){
                        throw new RuntimeException();
                    }
                    startIndex = startIndex + length -1;
                    length = 0;
                    break;
                }else{
                    ///// System.out.println(fullPartitionMDLCost + " " + MDL_COST_ADWANTAGE + " " + partialPartitionMDLCost); /////
                }
            }

            // if we reach at the end of a trajectory
            if (startIndex+length >= nPoints) break;
        }

        
        cacteriscticPoints.add(nPoints - 1); // add the end point of a trajectory
        //p.setValue(nPoints - 1);

        return cacteriscticPoints;
    }
	
    private static double LOG2(double x) {
        return Math.log(x) / Math.log(2);
    }
	
    private static int computeModelCost(ArrayList<PointDTO> pTrajectory, int startPIndex, int endPIndex) {
        double[] lineSegmentStartCoordinate = pTrajectory.get(startPIndex).getPointCoordinates();
        double[] lineSegmentEndCoordinate = pTrajectory.get(endPIndex).getPointCoordinates();

        double distance = measureDistanceFromPointToPoint(lineSegmentStartCoordinate, lineSegmentEndCoordinate);

        if( distance < 1.0 ) distance = 1.0; // to take logarithm


        return (int)Math.ceil(LOG2(distance));
    }

    private static int computeEncodingCost(ArrayList<PointDTO> pTrajectory, int startPIndex, int endPIndex) {
        PointDTO clusterComponentStart;
        PointDTO clusterComponentEnd;
        PointDTO lineSegmentStart;
        PointDTO lineSegmentEnd;
        double perpendicularDistance;
        double angleDistance;
        int encodingCost = 0;

        clusterComponentStart = pTrajectory.get(startPIndex);
        clusterComponentEnd = pTrajectory.get(endPIndex);

        for( int i = startPIndex; i < endPIndex; i++ ) {
            lineSegmentStart = pTrajectory.get(i);
            lineSegmentEnd = pTrajectory.get(i + 1);

            perpendicularDistance = measurePerpendicularDistance(clusterComponentStart,
                            clusterComponentEnd, lineSegmentStart, lineSegmentEnd);
            angleDistance = measureAngleDisntance(clusterComponentStart,
                            clusterComponentEnd, lineSegmentStart, lineSegmentEnd);

            if (perpendicularDistance < 1.0) perpendicularDistance = 1.0; //  to take logarithm
            if (angleDistance < 1.0) angleDistance = 1.0; //  to take logarithm

            encodingCost += ((int)Math.ceil(LOG2(perpendicularDistance)) + (int)Math.ceil(LOG2(angleDistance)));
        }

        return encodingCost;	
    }
        
    private static double measurePerpendicularDistance(PointDTO s1, PointDTO e1, PointDTO s2, PointDTO e2) {
        double[] coordinateE1 = e1.getPointCoordinates();
        double[] coordinateE2 = e2.getPointCoordinates();
        double[] coordinateS1 = s1.getPointCoordinates();
        double[] coordinateS2 = s2.getPointCoordinates();
        
        //  we assume that the first line segment is longer than the second one
        double distance1 = measureDistanceFromPointToLineSegment(coordinateS1, coordinateE1, coordinateS2); //  the distance from a start point to the cluster component
        double distance2 = measureDistanceFromPointToLineSegment(coordinateS1, coordinateE1, coordinateE2); //  the distance from an end point to the cluster component

        //  if the first line segment is exactly the same as the second one, 
        //  the perpendicular distance should be zero
        if (distance1 == 0.0 && distance2 == 0.0) return 0.0;

        //  return (d1^2 + d2^2) / (d1 + d2) as the perpendicular distance
        return ((Math.pow(distance1, 2) + Math.pow(distance2, 2)) / (distance1 + distance2));
    }
	
    private static double measureDistanceFromPointToLineSegment(double[] coordinatePointS, double[] coordinatePointE, double[] coordinatePointP) {
        int nDimension = coordinatePointS.length;

        //  construct two vectors as follows
        //  1. the vector connecting the start point of the cluster component and a given point
        //  2. the vector representing the cluster component
        double[] coordinateVector1 = new double[nDimension];
        double[] coordinateVector2 = new double[nDimension];
        for( int i = 0; i < nDimension; i++ ) {
            coordinateVector1[i] = coordinatePointP[i] - coordinatePointS[i];
            coordinateVector2[i] = coordinatePointE[i] - coordinatePointS[i];
        }

        //  a coefficient (0 <= b <= 1)
        double m_coefficient = computeInnerProduct(coordinateVector1, coordinateVector2) / computeInnerProduct(coordinateVector2, coordinateVector2);

        //  the projection on the cluster component from a given point
        double[] coordinateprojectionPoint = new double[nDimension];
        for( int i = 0; i < coordinatePointS.length; i++ ) {
            coordinateprojectionPoint[i] = coordinatePointS[i] + m_coefficient * coordinateVector2[i];
        }
        
        //  return the distance between the projection point and the given point
        return measureDistanceFromPointToPoint(coordinatePointP, coordinateprojectionPoint);
    }
    
    private static double measureDistanceFromPointToPoint(double[] coordinateP1, double[] coordinateP2) {
        double squareSum = 0.0;

        for( int i = 0; i < coordinateP1.length; i++ ) {
            squareSum += Math.pow((coordinateP2[i] - coordinateP1[i]), 2);
        }

        return Math.sqrt(squareSum);	
    }
	
    private static double computeVectorLength(double[] coordinateVector) {
        double squareSum = 0.0;

        for( int i = 0; i < coordinateVector.length; i++ ) {
            squareSum += Math.pow(coordinateVector[i], 2);
        }

        return Math.sqrt(squareSum);		
    }
	
    private static double computeInnerProduct(double[] coordinateVector1, double[] coordinateVector2) {
        double innerProduct = 0.0;

        for( int i = 0; i < coordinateVector1.length; i++ ) {
            innerProduct += (coordinateVector1[i] * coordinateVector2[i]);
        }

        return innerProduct;
    }
    
    private static double measureAngleDisntance(PointDTO s1, PointDTO e1, PointDTO s2, PointDTO e2) {
        int nDimensions = s1.getPointDimension();
        
        double[] coordinateE1 = e1.getPointCoordinates();
        double[] coordinateE2 = e2.getPointCoordinates();
        double[] coordinateS1 = s1.getPointCoordinates();
        double[] coordinateS2 = s2.getPointCoordinates();

        double[] coordinateVector1 = new double[nDimensions];
        double[] coordinateVector2 = new double[nDimensions];
        for (int i = 0; i < nDimensions; i++) {
            coordinateVector1[i] = coordinateE1[i] - coordinateS1[i];
            coordinateVector2[i] = coordinateE2[i] - coordinateS2[i];			
        }
		
        //  we assume that the first line segment is longer than the second one
        //  i.e., vectorLength1 >= vectorLength2
        double vectorLength1 = computeVectorLength( coordinateVector1 );
        double vectorLength2 = computeVectorLength( coordinateVector2 );

        //  if one of two vectors is a point, the angle distance becomes zero
        if( vectorLength1 == 0.0 || vectorLength2 == 0.0 ) return 0.0;
		
        //  compute the inner product of the two vectors
        double innerProduct = computeInnerProduct( coordinateVector1, coordinateVector2 );

        //  compute the angle between two vectors by using the inner product
        double cosTheta = innerProduct / ( vectorLength1 * vectorLength2 );
        //  compensate the computation error (e.g., 1.00001)
        //  cos(theta) should be in the range [-1.0, 1.0]
        //  START ...
        if( cosTheta > 1.0 ) cosTheta = 1.0; 
        if( cosTheta < -1.0 ) cosTheta = -1.0;
        //  ... END
        
        double sinTheta = Math.sqrt( 1 - Math.pow(cosTheta, 2) );
        //  if 90 <= theta <= 270, the angle distance becomes the length of the line segment
        //  if (cosTheta < -1.0) sinTheta = 1.0;
        
        return (vectorLength2 * sinTheta);					
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class EventLogDTO {
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final int MIN_SECOND_FRACTION = 0;
    public static final int MAX_SECOND_FRACTION = 6;

    private final DateTimeFormatter formatter;
    
    private final String sensorID;
    private final LocalDateTime datatime;
    private final String value;
    private final String note;
    
    public EventLogDTO(String sensorID, String date, String time, String value, String note) {
        this.formatter = new DateTimeFormatterBuilder()
                .appendPattern(DATE_PATTERN)
                .appendFraction(ChronoField.MICRO_OF_SECOND, MIN_SECOND_FRACTION, MAX_SECOND_FRACTION, true)
                .toFormatter();
        
        this.sensorID = sensorID;
        this.datatime = LocalDateTime.parse(date + " " + time, formatter);
        this.value = value;
        this.note = note;
    }
    
    
    public String getSensorID() {
        return sensorID;
    }

    public LocalDateTime getDatatime() {
        return datatime;
    }
    public String getDatatimeString() {
        return datatime.format(formatter);
    }
    public long getDatatimeMilliseconds() {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = datatime.atZone(zoneId);
        long milliseconds = zdt.toInstant().toEpochMilli();
        return milliseconds;
    }

    public String getValue() {
        return value;
    }

    public String getNote() {
        return note;
    }
    
    public String printInfo(){
        return "Event: sensor id: " + getSensorID() +
                " date time: " + getDatatimeString() +
                " value: " + getValue() +
                " note: " + getNote();
    }
}

//////////////////////////////////////////////////////////////////////////////////////
class SamplingLogDTO {
    private final String sensorID;
    private final String date;
    private final String time;
    private final String value;
    private final long timestamp;
    private final String note;

    public SamplingLogDTO(String sensorID, String date, String time, String value, long timestamp, String note) {
        this.sensorID = sensorID;
        this.date = date;
        this.time = time;
        this.value = value;
        this.timestamp = timestamp;
        this.note = note;
    }

    public String getSensorID() {
        return sensorID;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getNote() {
        return note;
    }
}
