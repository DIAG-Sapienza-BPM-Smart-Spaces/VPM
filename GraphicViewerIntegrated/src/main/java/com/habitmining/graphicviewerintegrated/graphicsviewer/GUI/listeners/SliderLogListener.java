package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners;

import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.SimulationManager;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Giovanni
 */
public class SliderLogListener implements ChangeListener {
    private final SimulationManager simulation_manager;

    public SliderLogListener(SimulationManager simulation_manager) {
        this.simulation_manager = simulation_manager;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int sliderPositionIndex = ((JSlider) e.getSource()).getValue(); 
        simulation_manager.drawLog(sliderPositionIndex);        
    }
}
