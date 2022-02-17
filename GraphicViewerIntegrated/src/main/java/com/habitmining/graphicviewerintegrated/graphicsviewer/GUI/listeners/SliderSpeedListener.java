package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners;

import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.SimulationManager;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Giovanni
 */
public class SliderSpeedListener implements ChangeListener {
    private final SimulationManager simulation_manager;

    public SliderSpeedListener(SimulationManager simulation_manager) {
        this.simulation_manager = simulation_manager;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();

        // update the speed factor
        int sliderPositionIndex = slider.getValue(); 
        simulation_manager.setSpeedSimulation(sliderPositionIndex);
    }
}
