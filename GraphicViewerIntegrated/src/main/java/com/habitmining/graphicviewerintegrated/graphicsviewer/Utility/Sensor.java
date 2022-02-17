package com.habitmining.graphicviewerintegrated.graphicsviewer.Utility;

/**
 *
 * @author Giovanni
 */
public class Sensor {
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
}
