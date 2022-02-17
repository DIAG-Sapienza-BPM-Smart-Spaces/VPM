package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners;

import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.SimulationManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Giovanni
 */
public class ButtonSimulationListener implements ActionListener {
    public final static String BUTTON_NAME_START = "Start Simulation";
    public final static String BUTTON_NAME_PAUSE = "Pause Simulation";
    public final static String BUTTON_NAME_SAVE = "Save Image";
    
    public final static String COMMAND_START_SIMULATION = "simulation.frame.action.listener.START.SIMULATION.command";
    public final static String COMMAND_PAUSE_SIMULATION = "simulation.frame.action.listener.PAUSE.SIMULATION.command";
    public final static String COMMAND_PRINT = "simulation.frame.action.listener.PRINT.command";
    
    
    private final SimulationManager simulation_manager;

    public ButtonSimulationListener(SimulationManager simulation_manager) {
        this.simulation_manager = simulation_manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if( command == null ) return;
        switch (command) {
            case COMMAND_START_SIMULATION:
                simulation_manager.startSimulation();
                break;
            case COMMAND_PAUSE_SIMULATION:
                simulation_manager.stopSimulation();
                break;
            case COMMAND_PRINT:
                simulation_manager.print();
                break;
        }
    }
}
