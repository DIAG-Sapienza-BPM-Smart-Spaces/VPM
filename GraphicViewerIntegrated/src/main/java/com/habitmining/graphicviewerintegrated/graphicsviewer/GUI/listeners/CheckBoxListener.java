package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners;

import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.SimulationManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;

/**
 *
 * @author Giovanni
 */
public class CheckBoxListener implements ItemListener {
    private final SimulationManager simulation_manager;

    public CheckBoxListener(SimulationManager simulation_manager) {
        this.simulation_manager = simulation_manager;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        JCheckBox checkBox = (JCheckBox) e.getItem();
        if( checkBox.isSelected() ) simulation_manager.enableSensor(checkBox.getName());
        else simulation_manager.disableSensor(checkBox.getName());
    }
}
