package com.habitmining.graphicviewerintegrated.graphicsviewer.Utility;

import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.StartFrame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giovanni
 */
public class PropertyReader {
//    private final static String PROPERTIES_PATH = "/com/habitmining/graphicviewerintegrated/graphicsviewer/parametres.properties";

    private final static String PROPERTIES_PATH = "parametres.properties";

    private final static String SAMPLING_VALUE = "sampling_values";
    private final static String MDL_COST_ADVANTAGE = "mdl_cost_advantage";
    private final static String THRESHOLD_STOP_AREA = "threshold_stop_area";
    private final static String THRESHOL_AREA_MOVEMEMENT = "threshold_area_movement";
    private final static String WEIGHT_INDEX_STOP = "weight_index_stop";
    private final static String WEIGHT_INDEX_AREA = "weight_index_area";
    private final static String WEIGHT_INDEX_MOVEMENT = "weight_index_movement";

    private final static int DEFAULT_SAMPLING_VALUE = 25;
    private final static int DEFAULT_MDL_COST_ADVANTAGE = 25;
    private final static double DEFAULT_THRESHOLD_STOP_AREA = 0.33;
    private final static double DEFAULT_THRESHOL_AREA_MOVEMEMENT = 0.66;
    private final static double DEFAULT_WEIGHT_INDEX_STOP = 0.33;
    private final static double DEFAULT_WEIGHT_INDEX_AREA = 0.33;
    private final static double DEFAULT_WEIGHT_INDEX_MOVEMENT = 0.33;

    public static int getSamplingValue() {
        int mdl_cost_advantage = DEFAULT_SAMPLING_VALUE;

        Properties properties = new Properties();
        System.err.println(PROPERTIES_PATH);
        try {
//            properties.load( PropertyReader.class.getResource( PROPERTIES_PATH ).openStream() );
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            //URL url = cl.getResource( PROPERTIES_PATH );
//            //InputStream is = url.openStream();
            InputStream is = cl.getResourceAsStream(PROPERTIES_PATH);
            properties.load(is);
            mdl_cost_advantage = Integer.parseInt(properties.getProperty(SAMPLING_VALUE));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mdl_cost_advantage;
    }

    public static int getMDLcostAdvantage() {
        int mdl_cost_advantage = DEFAULT_MDL_COST_ADVANTAGE;

        Properties properties = new Properties();
        try {
//            properties.load( PropertyReader.class.getResource( PROPERTIES_PATH ).openStream() );
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            //URL url = cl.getResource( PROPERTIES_PATH );
//            //InputStream is = url.openStream();
            InputStream is = cl.getResourceAsStream(PROPERTIES_PATH);
            properties.load(is);
            mdl_cost_advantage = Integer.parseInt(properties.getProperty(MDL_COST_ADVANTAGE));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mdl_cost_advantage;
    }

    public static double getThresholdStopArea() {
        double threshold_stop_area = DEFAULT_THRESHOLD_STOP_AREA;

        Properties properties = new Properties();
        try {
//            properties.load(PropertyReader.class.getResource(PROPERTIES_PATH).openStream());
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            //URL url = cl.getResource( PROPERTIES_PATH );
//            //InputStream is = url.openStream();
            InputStream is = cl.getResourceAsStream(PROPERTIES_PATH);
            properties.load(is);
            threshold_stop_area = Double.parseDouble(properties.getProperty(THRESHOLD_STOP_AREA));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        return threshold_stop_area;
    }

    public static double getThresholdAreaMovement() {
        double threshold_area_movement = DEFAULT_THRESHOL_AREA_MOVEMEMENT;

        Properties properties = new Properties();
        try {
//            properties.load(PropertyReader.class.getResource(PROPERTIES_PATH).openStream());
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            //URL url = cl.getResource( PROPERTIES_PATH );
//            //InputStream is = url.openStream();
            InputStream is = cl.getResourceAsStream(PROPERTIES_PATH);
            properties.load(is);
            threshold_area_movement = Double.parseDouble(properties.getProperty(THRESHOL_AREA_MOVEMEMENT));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        return threshold_area_movement;
    }

    public static double getWeightIndexStop() {
        double weight_index_stop = DEFAULT_WEIGHT_INDEX_STOP;

        Properties properties = new Properties();
        try {
//            properties.load(PropertyReader.class.getResource(PROPERTIES_PATH).openStream());
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            //URL url = cl.getResource( PROPERTIES_PATH );
//            //InputStream is = url.openStream();
            InputStream is = cl.getResourceAsStream(PROPERTIES_PATH);
            properties.load(is);
            weight_index_stop = Double.parseDouble(properties.getProperty(WEIGHT_INDEX_STOP));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        return weight_index_stop;
    }

    public static double getWeightIndexArea() {
        double weight_index_area = DEFAULT_WEIGHT_INDEX_AREA;

        Properties properties = new Properties();
        try {
//            properties.load(PropertyReader.class.getResource(PROPERTIES_PATH).openStream());
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            //URL url = cl.getResource( PROPERTIES_PATH );
//            //InputStream is = url.openStream();
            InputStream is = cl.getResourceAsStream(PROPERTIES_PATH);
            properties.load(is);
            weight_index_area = Double.parseDouble(properties.getProperty(WEIGHT_INDEX_AREA));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        return weight_index_area;
    }

    public static double getWeightIndexMovement() {
        double weight_index_movement = DEFAULT_WEIGHT_INDEX_MOVEMENT;

        Properties properties = new Properties();
        try {
//            properties.load(PropertyReader.class.getResource(PROPERTIES_PATH).openStream());
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            //URL url = cl.getResource( PROPERTIES_PATH );
//            //InputStream is = url.openStream();
            InputStream is = cl.getResourceAsStream(PROPERTIES_PATH);
            properties.load(is);
            weight_index_movement = Double.parseDouble(properties.getProperty(WEIGHT_INDEX_MOVEMENT));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        return weight_index_movement;
    }
}
