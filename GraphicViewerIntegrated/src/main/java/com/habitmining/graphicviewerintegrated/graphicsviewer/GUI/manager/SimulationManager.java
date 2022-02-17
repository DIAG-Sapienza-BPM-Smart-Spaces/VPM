package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager;

import com.bric.multislider.*;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.SimulationFrame;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.graphpanel.GraphPanel;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.ButtonSimulationListener;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.SliderRangeListener;
import com.habitmining.graphicviewerintegrated.graphicsviewer.Utility.LogLine;
import com.habitmining.graphicviewerintegrated.graphicsviewer.Utility.PropertyReader;
import com.habitmining.graphicviewerintegrated.graphicsviewer.Utility.Sensor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import com.habitmining.trajectoryanalyzer.classifier.ClassificatorTrajectory;
import com.habitmining.trajectoryanalyzer.dto.PointDTO;
import com.habitmining.trajectoryanalyzer.dto.SensorMappingDTO;
import com.habitmining.trajectoryanalyzer.dto.SubtrajectoryDTO;
import com.habitmining.trajectoryanalyzer.traclus.TraclusSplitTrajectory;


/**
 *
 * @author Giovanni
 */
public class SimulationManager {
    private final static String SAVE_IMAGE_ERROR_DESCRIPTION = "Save Error";
    private final static String SAVE_IMAGE_ERROR_MESSAGE = "Impossible to save the image";
             
    public final static int MIN_SPEED_FACTOR = 1;
    public final static int MAX_SPEED_FACTOR = 1000;
    public final static int MIN_SPEED_SLIDER_VALUE = 1;
    public final static int MAX_SPEED_SLIDER_VALUE = 100;
    public final static int DEFAULT_SPEED_FACTOR = 1;

    // swing component to manage the simulation
    private final GraphPanel image_panel;
    private final JSlider log_slider;
    private final MultiThumbSlider<String> range_slider;
    private final JButton start_button;
    private final JLabel speed_label;
    private final JLabel description_label;
    
    // data structures used to create the graph
    private final HashMap<String, Sensor> sensorMap;
    private final List<LogLine> log;
    private final HashSet<String> noDraw;
    
    // parametres for the simulation managment
    private double speedFactor;
    private boolean threadIsDrawing;
    private boolean sliderUpdate;
    private boolean speedFactorUpdate;
    private boolean stopThread ;
    
    

    public SimulationManager(HashMap<String, Sensor> sensorMap, List<LogLine> logs, GraphPanel imagePanel,
            JSlider logSlider, MultiThumbSlider<String> rangeSlider, JButton startButton, JLabel speedLabel, JLabel descriptionLabel) {
        this.image_panel = imagePanel;
        this.log_slider = logSlider;
        this.range_slider = rangeSlider;
        this.start_button = startButton;
        this.speed_label = speedLabel;
        this.description_label = descriptionLabel;

        this.sensorMap = sensorMap;
        this.log = logs;
        this.noDraw = new HashSet<>();
        
        this.speedFactor = DEFAULT_SPEED_FACTOR;
        this.threadIsDrawing = false;
        this.sliderUpdate = false;
        this.speedFactorUpdate = false;
        this.stopThread = false;
    }
    
    
    public synchronized ArrayList<SubtrajectoryDTO> calculateSubtrajectories() {
        stopSimulation();
        
        ArrayList<PointDTO> trajectory = new ArrayList<>();
        for( LogLine ll : log ) {
            String pointID = ll.getSensorID();
            double[] pointCoordinates = new double[]{ sensorMap.get(pointID).getSensorPositionX(), sensorMap.get(pointID).getSensorPositionY() };
            long timestamp = ll.getTimestamp();
            PointDTO p = new PointDTO(pointID, pointCoordinates, timestamp);
            trajectory.add(p);
        }
        
        SensorMappingDTO mapping = new SensorMappingDTO();
        for(Sensor s : sensorMap.values() ) {
            mapping.put(s.getSensorID(), s.getRoom(), s.getObject());
        }
        
        
        int MDLcostAdvantage = PropertyReader.getMDLcostAdvantage(); 
        double thresholdAreaMovement = PropertyReader.getThresholdAreaMovement(); 
        double thresholdStopArea = PropertyReader.getThresholdStopArea(); 
        double weightIndexArea = PropertyReader.getWeightIndexArea(); 
        double weightIndexMovement =  PropertyReader.getWeightIndexMovement(); 
        double weightIndexStop = PropertyReader.getWeightIndexStop(); 
        
        ArrayList<Integer> listaCaracteristicPoints = TraclusSplitTrajectory.partitionTrajectory( trajectory, MDLcostAdvantage );
        ArrayList<SubtrajectoryDTO> listaSubtrajectories = ClassificatorTrajectory.classifyTrajectory(
                trajectory, listaCaracteristicPoints, mapping,
                weightIndexStop, weightIndexMovement, weightIndexArea, thresholdStopArea, thresholdAreaMovement
        );
        
        return listaSubtrajectories;
    }
    
    
    
    // save an image of the graph of the the position of the slider
    public synchronized void print() {       
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) { // OK button pressed by user
            String file_name = fc.getSelectedFile().getAbsolutePath();
            
            // check if the file has the jpg extension
            if( !file_name.endsWith(".jpg") && !file_name.endsWith(".JPG") ) {
                file_name = file_name + ".jpg";
            }
        
            BufferedImage bufferedImage = image_panel.printGraph(); // get the image of the graph
            File outputFile = new File(file_name); // create the file that contains the image of the graph
            
            // save the image on the file
            try {
                ImageIO.write(bufferedImage, "jpg", outputFile);
            } catch (IOException ex) {
                Logger.getLogger(SimulationManager.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, SAVE_IMAGE_ERROR_MESSAGE, SAVE_IMAGE_ERROR_DESCRIPTION, JOptionPane.ERROR_MESSAGE);
            }            
        }
    }


    
    // enable the sensor (redraw the graph)
    public synchronized void enableSensor(String sensorID) {
        noDraw.remove(sensorID);
        draw( log_slider.getValue() );
    }
    
    // disable the sensor (redraw the graph)
    public synchronized void disableSensor(String sensorID) {
        noDraw.add(sensorID);
        draw( log_slider.getValue() );
    }
    
    
    public synchronized void changeMultisliderValues(long min, long max) {
        float[] positions = range_slider.getThumbPositions();
        String[] values = range_slider.getValues();
        
        int logSize = log.size() - 1;
        float posMin = SliderRangeListener.castIndexToPosition(min, logSize);
        float posmax = SliderRangeListener.castIndexToPosition(max, logSize);

        if( values[0].equals(SliderRangeListener.MAX_THUMB) ) {
            positions[0] = posmax;
            positions[1] = posMin;
        } else {
            positions[0] = posMin;
            positions[1] = posmax;
        }
        
        range_slider.setValues(positions, values);
    }
    
    // change the min and max value (redraw the graph)
    public synchronized void changeDisplayRange(float minPositionValue, float maxPositionValue) {
        // get min and max value from the simulation slider
        int logMaxIndex = log.size() - 1;
        int minPosition = SliderRangeListener.castPositionToIndex(minPositionValue, logMaxIndex);
        int maxPosition = SliderRangeListener.castPositionToIndex(maxPositionValue, logMaxIndex);
        
        // if needed update the current position of the slider (redraw the graph)
        int currentPosition = log_slider.getValue();
        if( currentPosition < minPosition ) log_slider.setValue( minPosition );
        else if( currentPosition >  maxPosition ) log_slider.setValue( maxPosition );
        else draw( currentPosition );
        
        //System.out.println("minPosition_" + minPosition + " currentPosition_" + currentPosition + " maxPosition_" + maxPosition);

        // set the view of the range slider
        int range = 1 + maxPosition - minPosition;
        log_slider.setMinimum(minPosition);
        log_slider.setMaximum(maxPosition);
        log_slider.setMajorTickSpacing( SimulationFrame.getMajorTrickSpacing(range) );
        log_slider.setMinorTickSpacing( SimulationFrame.getMinorTrickSpacing(range) );
        
        
        
        long first = log.get(0).getTimestamp();
        long min = log.get(minPosition).getTimestamp() - first;
        long max = log.get(maxPosition).getTimestamp() - first;
        
        //System.out.println("min " + min + " max " + max);
    }
    

    // draw the graph from the min value to the current index
    private synchronized void draw(int index) {
        // get the graph information from min value to index
        float[] positions = range_slider.getThumbPositions();
        String[] values = range_slider.getValues();
        int minRangePosition;
        if( values[0].equals(SliderRangeListener.MIN_THUMB) )
            minRangePosition = SliderRangeListener.castPositionToIndex(positions[0], log.size()-1);
        else
            minRangePosition = SliderRangeListener.castPositionToIndex(positions[1], log.size()-1);
        
        // create the data structures ofgraph
        ArrayList<LogLine> subListLog = new ArrayList<>(log.subList(minRangePosition, index+1));
        GraphInformations gi = GraphCreatorManager.createOnMotionSensorGraph(subListLog, sensorMap, noDraw);

        // set the description label
        description_label.setText(gi.getNote());

        // repaint the graph panel
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                image_panel.drawGraph(gi.getGraphVerticies(), gi.getGraphEdges());
            }
        });
    }
    
    // called from the ChangeListener when the slider change the position and draw the graph
    public synchronized void drawLog(int index) {
        draw(index);
        
        // if isDrawing == true the simulation thread has drawn the graph
        if( !threadIsDrawing ) {
            sliderUpdate = true;
            notifyAll();
        }
    }
    
    // called from the simulation thread
    public synchronized boolean drawLogSimulation() {
        boolean canExecuteNext = false;
        
        // get the current position
        int currentSliderPosition = log_slider.getValue();
        int nextSliderPosition = currentSliderPosition + 1;

        // get the maxRangePosition
        int logSize = log.size() - 1;
        int maxRangePosition;
        float[] positions = range_slider.getThumbPositions();
        String[] values = range_slider.getValues();
        if( values[0].equals(SliderRangeListener.MAX_THUMB) )
            maxRangePosition = SliderRangeListener.castPositionToIndex(positions[0], logSize);
        else
            maxRangePosition = SliderRangeListener.castPositionToIndex(positions[1], logSize);

        // check if the simulation is at the end
        if( nextSliderPosition <= maxRangePosition ) {
            long elapsed = 0;
            boolean complete = false;
            while( !complete ) {
                long start = System.currentTimeMillis();
                try {
                    long nextTimestamp = log.get(nextSliderPosition).getTimestamp();
                    long currentTimestamp = log.get(currentSliderPosition).getTimestamp();

                    // wait the thread
                    long sleepTime = Math.round( (nextTimestamp - currentTimestamp - elapsed) / speedFactor );
                    if( sleepTime > 0 ) wait( sleepTime );

                    // the user stops the simulation
                    if( stopThread ) {
                        complete = true; // stop the wait while
                        canExecuteNext = false; // set the return value
                        stopThread = false; // reset the flag
                        
                    // the user change the value (min, max, current position) of a slider
                    } else if( sliderUpdate ) {  
                        complete = true; // stop the wait while
                        canExecuteNext = true; // set the return value
                        sliderUpdate = false; // reset the flag
                        
                    // the user change the speed (using the speed slider)
                    } else if(speedFactorUpdate) {
                        speedFactorUpdate = false; // reset the flag
                        // update the waiting time 
                        long end = System.currentTimeMillis();
                        elapsed = elapsed + end - start;
                        complete = false; // continue the wait while
                        
                    // no user modification, draw the graph
                    } else {
                        // checks that it has not been awakened prematurely
                        long end = System.currentTimeMillis();
                        long trascorso = end - start;

                        if( trascorso >= sleepTime ) {
                            canExecuteNext = true; // set the return value
                            complete = true; // stop the wait while

                            threadIsDrawing = true; // set the flag that indicates the thread modification of the graph
                            log_slider.setValue( nextSliderPosition ); 
                            threadIsDrawing = false;
                        } else {
                            // update the waiting time 
                            elapsed = elapsed + trascorso;
                            complete = false; // continue the wait while
                        }
                    }
                } catch(InterruptedException ex) {
                    Logger.getLogger(SimulationManager.class.getName()).log(Level.SEVERE, null, ex);
                    
                    canExecuteNext = false; // uknown stop of the thread
                }           
            }
        
        // end of the simulation
        } else {
            canExecuteNext = false;
        }
        
        return canExecuteNext;
    }
    
    

    // start the simulation from the current log to the end
    public synchronized void startSimulation() {
        sliderUpdate = false;
        speedFactorUpdate = false;
        stopThread = false;
        threadIsDrawing = false;

        SimulationThread simulation_thread = new SimulationThread(this);
        simulation_thread.start();
    }
    
    // stop the simulation
    public synchronized void stopSimulation() {
        stopThread = true;
        notifyAll();
    }
    
    // called from the thread when start
    public synchronized void threadStart() {
        // check if the slider is at the end value of the simulation
        int logSize = log.size() - 1;
            
        // get min and max  
        float[] positions = range_slider.getThumbPositions();
        String[] values = range_slider.getValues();
        int minRangePosition;
        int maxRangePosition;
        if( values[0].equals(SliderRangeListener.MAX_THUMB) ) {
            minRangePosition = SliderRangeListener.castPositionToIndex(positions[1], logSize);
            maxRangePosition = SliderRangeListener.castPositionToIndex(positions[0], logSize);
        } else {
            minRangePosition = SliderRangeListener.castPositionToIndex(positions[0], logSize);
            maxRangePosition = SliderRangeListener.castPositionToIndex(positions[1], logSize);
        }
        
        // if the currente position is at the end, set at the first position
        if( log_slider.getValue() == maxRangePosition ) {
            threadIsDrawing = true;
            log_slider.setValue( minRangePosition );
            threadIsDrawing = false;
        }
        
        // set the pause command to the button
        start_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pause.png")));
        start_button.setToolTipText(ButtonSimulationListener.BUTTON_NAME_PAUSE);
        start_button.setActionCommand(ButtonSimulationListener.COMMAND_PAUSE_SIMULATION);
    }

    // called from the thread when stop
    public synchronized void threadStop() {
        // set the pause command to the button
        start_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/start.png")));
        start_button.setToolTipText(ButtonSimulationListener.BUTTON_NAME_START);
        start_button.setActionCommand(ButtonSimulationListener.COMMAND_START_SIMULATION);
    }

    
    
    /*
     * speed factor (exponential function)
     * speed factors: min = 1, max = 1000
     * speed slider's values: min = 1, max = 100
     * 
     * y = 2^a(x-1) (pass from (1,1) i.e. (MIN_SPEED_SLIDER_VALUE, MIN_SPEED_FACTOR))
     *      MIN_SPEED_SLIDER_VALUE = 1;
     *      MIN_SPEED_FACTOR = 1;
     * 
     * - find a (s.t. the function pass from (x2,y2)):
     *          y2 = 2^a(x2-1) -> a = log2(y2) / (x2-1) 
     * 
     * y = 2 ^ (log2(y2)*(x-1)/(x2-1))
     * y = (2^(log2(y2)))^((x-1)/(x2-1))
     * y = y2 ^ ((x-1)/(x2-1))
     * 
     * we need (x2, y2) = (MAX_SPEED_SLIDER_VALUE, MAX_SPEED_FACTOR)
     *      MAX_SPEED_SLIDER_VALUE = 100;
     *      MAX_SPEED_FACTOR = 1000;
     * 
     * 
    */
    public synchronized void setSpeedSimulation(int value) {
        /* 
         * from the above formula, y = y2 ^ ((x-1)/(x2-1))
         * y = (y2 ^ (1 /(x2-1))) ^ (x-1)
         * base = y2 ^ (1 /(x2-1))
        */
        double base =  Math.pow((double) MAX_SPEED_FACTOR, 1/((double)MAX_SPEED_SLIDER_VALUE - 1));
        
        speedFactor = Math.pow(base, (value - 1));
        speedFactor = Math.round(speedFactor*100)/100.0;

        speed_label.setText( "speed " + speedFactor + "x" );
        
        speedFactorUpdate = true;
        notifyAll();
    }


    
    
    private class SimulationThread extends Thread {
        private final SimulationManager simulationManager;

        public SimulationThread(SimulationManager simulationManager) {
            super();
            this.simulationManager = simulationManager;
        }

        @Override
        public void run() {
            simulationManager.threadStart();

            boolean canExecute;
            do {
                canExecute = simulationManager.drawLogSimulation();
            } while(canExecute);

            simulationManager.threadStop();
        } 
    }
}
