package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager;

import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.graphpanel.Edge;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.graphpanel.Vertex;
import com.habitmining.graphicviewerintegrated.graphicsviewer.Utility.LogLine;
import com.habitmining.graphicviewerintegrated.graphicsviewer.Utility.Sensor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Giovanni
 */
public class GraphCreatorManager {
    private final static int VERTEX_MIN_DIMENSION = 10;
    private final static int VERTEX_MAX_DIMENSION = 30;
    
    private final static Color VERTEX_ON_COLOR = Color.GREEN;
    private final static Color VERTEX_OFF_COLOR = Color.BLUE;
    private final static int VERTEX_ALPHA_COLOR = 127;
    
    
    /*
     * filter the LogLine ArrayList returning:
     * all the LogLines whose sensors are in the map
     * all the LogLines that have motion sensor
     * all the LogLines whose motion sensors are ON
     * 
     * the returned list is in the sameorder of the input list
     */
    public static ArrayList<LogLine> filterOnMotionSensor(List<LogLine> log, HashMap<String, Sensor> sensorMap) {
        ArrayList<LogLine> filteredValues = new ArrayList<>();
        for( LogLine ll : log ) {
            String sensorID = ll.getSensorID();
            String sensorValue = ll.getSensorValue();
            if(sensorMap.containsKey(sensorID)
                    && sensorValue.equals(Sensor.MOTION_SENSOR_STATUS_ON)) filteredValues.add(ll);   
        }
        return filteredValues;
    }
    
    
    
    /*
     * create the graph
    */
    public static GraphInformations createOnMotionSensorGraph(ArrayList<LogLine> logsList, HashMap<String, Sensor> sensorsMap, HashSet<String> noDrawSensorSet) {
        HashSet<Vertex> graphVertices = new HashSet<>();
        HashSet<Edge> graphEdges = new HashSet<>();
        
        String lastOnSensorID = ""; // it represent the ON sensor
        Date lastOnSensorDate = null;
        String lastOnSensorNote = "";
        long lastOnSensorMillisecond = -1;
        HashMap<String, Long> vertexTotalTimeON = new HashMap<>(); // it maps sensors and thier ON time, it is used to create the Verticies
        ArrayList<String> orderedVertex = new ArrayList(); // the ordered list of the sensor id, it is used to create the edges
        
        
        Iterator<LogLine> iteratorLogLine = logsList.iterator();
        LogLine previousLogLine = null;
        while( iteratorLogLine.hasNext() ) {
            LogLine currentLogLine = iteratorLogLine.next();
            
            Date sensorDate = currentLogLine.getDate();
            String sensorID = currentLogLine.getSensorID();
            String sensorValue = currentLogLine.getSensorValue();
            
            Sensor sensor = sensorsMap.get(sensorID);
            
            if( sensor != null && !noDrawSensorSet.contains(sensorID) 
                    && sensorValue.equals(Sensor.MOTION_SENSOR_STATUS_ON) ) {
                
                // if there is the vertex ON time, create a vertex with ON time = 0
                if( !vertexTotalTimeON.containsKey(sensorID) ) vertexTotalTimeON.put(sensorID, 0L);
                
                // if the current vertex is different from the previous vertex add the current vertex to the list 
                if( previousLogLine == null || !sensorID.equals(previousLogLine.getSensorID()) )
                    orderedVertex.add(sensorID);

                
                if( previousLogLine != null ) {
                    String previousSensorID = previousLogLine.getSensorID();
                    
                    // update the ON time of the previous
                    long previousTimeON = sensorDate.getTime() - previousLogLine.getDate().getTime();
                    long totalPreviousTimeON = previousTimeON + vertexTotalTimeON.get(previousSensorID);
                    vertexTotalTimeON.put(sensorID, totalPreviousTimeON);
                }
                
                // set the last ON motion sensor
                lastOnSensorID = sensorID;
                lastOnSensorDate = sensorDate;
                lastOnSensorNote = currentLogLine.getNote();
                lastOnSensorMillisecond = currentLogLine.getTimestamp();
                
                previousLogLine = currentLogLine;
            }
        }
        
        // create the verteces data structures to pass to the graph panel
        long minTimeON = Collections.min(vertexTotalTimeON.values());
        long maxTimeON = Collections.max(vertexTotalTimeON.values());
        for(Map.Entry<String, Long> entry : vertexTotalTimeON.entrySet()) {
            // create the vertex
            String sensorID = entry.getKey();
            int sensorPositionX = sensorsMap.get(sensorID).getSensorPositionX();
            int sensorPositionY = sensorsMap.get(sensorID).getSensorPositionY();
            int sensorDimension = castTimeToDimensionExponential(entry.getValue(), minTimeON, maxTimeON, VERTEX_MIN_DIMENSION, VERTEX_MAX_DIMENSION);
            Color sensorColor = calculateVertexToColor( lastOnSensorID.equals(sensorID) );
            graphVertices.add( new Vertex(sensorID, sensorPositionX, sensorPositionY, sensorDimension, sensorColor) );
        }
        
        
        // create the edges data structures to pass to the graph panel
        int totalEdge = orderedVertex.size() + 1;
        int edgesCounter = 1;
        String previousID = null;
        Iterator<String> iteratorOrderedVertex = orderedVertex.iterator();
        while( iteratorOrderedVertex.hasNext() ) {
            String currentID = iteratorOrderedVertex.next();
            
            // create the edge
            if( previousID != null ) {
                int edgeNumber = edgesCounter;
                String edgeID = edgesCounter + "";
                String edgeFrom = previousID;
                String edgeTo = currentID;
                boolean edgeIsDirect = true;
                Color edgeColor = calculateEdgeToColor(edgesCounter, totalEdge);
                
                graphEdges.add( new Edge(edgeNumber, edgeID, edgeFrom, edgeTo, edgeIsDirect, edgeColor) );

                edgesCounter++;
            }
            
            previousID = currentID;
        }
        
        
        // create the graph note
        String graphNote = "Sensor " + lastOnSensorID + " at time " + lastOnSensorDate + 
                " switch on. Note: " + lastOnSensorNote + " Timestamp=" + lastOnSensorMillisecond;

        return new GraphInformations(graphVertices, graphEdges, graphNote);
    }
    
    
    /*
     * y = e^(a * x + b) passing through p1 = (t, d) and p2 = (T, D)
     * t: minimum time, T: maximum time, d: minimum dimension, D: maximum dimension
     *
     * log(y) = (a * x + b)
     * using cramer solve the system and find a and b with the points p1 and p2
     * delta = t - T
     * delta A = log(d) - log(D)
     * delta B = t * log(D) - T * log(d)
     * a = delta A / delta
     * b = delta B / delta
     * 
     */
    private static int castTimeToDimensionExponential(long time, long minTime, long maxTime, int minDimension,int maxDimension) {
        /*
         * if minTime == maxTime
         * there is only one ON sensor, so it returns the max possible dimension
         * all sensors have the same ON time, so it returns the max possible dimension
         */
        if( minTime == maxTime ) return maxDimension;
        
        
        double delta = minTime - maxTime;
        double deltaA = Math.log(minDimension) - Math.log(maxDimension);
        double deltaB = minTime * Math.log(maxDimension) - maxTime * Math.log(minDimension);
        
        double a = deltaA / delta;
        double b = deltaB / delta;
        double exsponent = a * time + b;
        
        int dimension = (int) Math.round( Math.pow( Math.E, exsponent ) );

        return dimension;
    }
    
    
    private static int castTimeToDimensionLinear(long time, long minTime, long maxTime, int minDimension,int maxDimension) {
        int dimension;
        if(minTime == maxTime) {
            dimension = minDimension;
        } else {
            dimension = (int) (minDimension + (time - minTime) * (maxDimension - minDimension) / (maxTime - minTime));
        }
        return dimension;
    }
    
    
    /*
     * calculate the color of a vertex
     */
    private static Color calculateVertexToColor(boolean isON) {
        // define the color of the sensor
        int R = VERTEX_OFF_COLOR.getRed();
        int G = VERTEX_OFF_COLOR.getGreen();
        int B = VERTEX_OFF_COLOR.getBlue();
        int A = VERTEX_ALPHA_COLOR;

        if( isON ) {
            R = VERTEX_ON_COLOR.getRed();
            G = VERTEX_ON_COLOR.getGreen();
            B = VERTEX_ON_COLOR.getBlue();
        }

        return new Color(R, G, B, A);
    }
    
    
    /*
     * calculate the color of an edge
     * the color scale go from RED to GREEN
     * biggest numbers have green color
     * smallest numbers have red color
     * 
     * R = 255 * (total_edges - edge_identifier) / (total_edges - 1) 
     * G = 255 * (edge_identifier - 1) / (total_edges - 1)
     * B = 0
     */
    private static Color calculateEdgeToColor(int edgeNumber, int totalEdge) {
        // set the RGBA values
        int R = 0; // red value of RGB
        int G = 0; // green value of RGB
        int B = 0; // blue value of RGB
        if( totalEdge == 1 ) { // set the green color
            R = 0;
            G = 255;
            B = 0;
        } else if( totalEdge > 1 ) {
            R = 255 * (totalEdge - edgeNumber) / (totalEdge - 1);
            G = 255 * (edgeNumber - 1) / (totalEdge - 1);
        }

        return new Color(R, G, B);
    }
}
