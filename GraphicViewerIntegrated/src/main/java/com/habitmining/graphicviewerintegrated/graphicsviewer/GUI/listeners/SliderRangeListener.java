package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners;

import com.bric.multislider.*;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.SimulationManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Giovanni
 */
public class SliderRangeListener implements ChangeListener {
    // the slider has values in range [0.0, 1.0]
    public final static float MIN_SLIDER_VALUE = 0f;
    public final static float MAX_SLIDER_VALUE = 1f;
    
    public final static String MIN_THUMB = "min";
    public final static String MAX_THUMB = "max";
    
    private final SimulationManager simulation_manager;

    public SliderRangeListener(SimulationManager simulationManager) {
        this.simulation_manager = simulationManager;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        MultiThumbSlider<String> slider = (MultiThumbSlider<String>) e.getSource();   
        float[] positions = slider.getThumbPositions();
        String[] values = slider.getValues();

        float min;
        float max;
        if( values[0].equals(SliderRangeListener.MIN_THUMB) ) {
            min = positions[0];
            max = positions[1];
        } else {
            min = positions[1];
            max = positions[0];
        }
        
        simulation_manager.changeDisplayRange( min, max );
    }
    
    
    public static int castPositionToIndex(float position, int maxIndex) {
        return (int) Math.ceil( maxIndex * position / MAX_SLIDER_VALUE );
    }
     
    public static float castIndexToPosition(long index, int maxIndex) {
        return MAX_SLIDER_VALUE * (float)index / maxIndex;
    }
}
