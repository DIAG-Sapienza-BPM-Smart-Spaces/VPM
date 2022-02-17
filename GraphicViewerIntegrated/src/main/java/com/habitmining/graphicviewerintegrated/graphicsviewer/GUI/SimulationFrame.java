package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI;

import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.ButtonTrajectoryListener;
import com.bric.multislider.*;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.graphpanel.GraphPanel;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.ButtonSimulationListener;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.CheckBoxListener;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.SliderLogListener;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.SliderRangeListener;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.SliderSpeedListener;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.GraphCreatorManager;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.SimulationManager;
import com.habitmining.graphicviewerintegrated.graphicsviewer.Utility.LogLine;
import com.habitmining.graphicviewerintegrated.graphicsviewer.Utility.Sensor;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

/**
 *
 * @author Giovanni
 */
public class SimulationFrame extends JFrame {
    private final static int MAJOR_TRICK_SPACING_VALUE = 15;
    private final static int MINOR_TRICK_SPACING_VALUE = 2;
    
    private final StartFrame startFrame;
    private final GraphPanel imagePanel;
    private final MultiThumbSlider<String> slider_range;

    /**
     * Creates new form SimulationFrame
     * @param startFrame
     * @param taskName
     * @param backgroundImage
     * @param sensorMap
     * @param logsList
     */
    public SimulationFrame(StartFrame startFrame, String taskName, ImageIcon backgroundImage, HashMap<String, Sensor> sensorMap, List<LogLine> logsList) {
        super(startFrame.getTitle() + " - " + taskName);
        
        this.startFrame = startFrame;

        List<LogLine> filteredLogsList = GraphCreatorManager.filterOnMotionSensor(logsList, sensorMap);

        int filteredLogsListSize = filteredLogsList.size();
        
        
        // inizialize the panel that display the graph
        this.imagePanel = new GraphPanel( backgroundImage );
        
        // inizialize the slider for the simulation bound
        float[] positions = new float[] { SliderRangeListener.MIN_SLIDER_VALUE, SliderRangeListener.MAX_SLIDER_VALUE };
        String[] values = new String[] { SliderRangeListener.MIN_THUMB, SliderRangeListener.MAX_THUMB };
        this.slider_range = new MultiThumbSlider<>( MultiThumbSlider.HORIZONTAL, positions, values );
        
        // default initializator
        initComponents(); 

        
        // create the simulation manager
        SimulationManager simulationManager = new SimulationManager( sensorMap, filteredLogsList,
                imagePanel, slider_log, slider_range, button_start, label_speed, label_report );
        
        if( filteredLogsListSize > 0 ) simulationManager.drawLog(0); // draw the first report
        
        
        // create the check boxes
        ArrayList<String> sortSensorList = new ArrayList<>( sensorMap.keySet() ); // sort the sensor name
        Collections.sort(sortSensorList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        // for each sensor create the check box and add the listener
        CheckBoxListener item_listener = new CheckBoxListener(simulationManager);
        Iterator<String> iter2 = sortSensorList.iterator();
        while(iter2.hasNext()) {
            String sensorID = iter2.next();
            JCheckBox checkBox = new JCheckBox(sensorID);
            checkBox.setName(sensorID);
            checkBox.setFocusable(false);
            checkBox.setSelected(true);
            checkBox.addItemListener(item_listener);
            sensors_panel.add(checkBox);
        }


        // update the dimension of the sensor control
        int scrollSizeWidth = sensor_panel_scrollPane.getPreferredSize().width;
        int panelSizeHeight = sensor_controls_panel.getPreferredSize().height;
        sensor_controls_panel.setPreferredSize(new Dimension(scrollSizeWidth, panelSizeHeight));

        
        // set the slider for the log add the change listener
        slider_log.setMinimum(0);
        slider_log.setMaximum( filteredLogsListSize - 1 );
        slider_log.setValue(0);
        slider_log.setMajorTickSpacing( getMajorTrickSpacing(filteredLogsListSize) );
        slider_log.setMinorTickSpacing( getMinorTrickSpacing(filteredLogsListSize) );
        slider_log.addChangeListener(new SliderLogListener(simulationManager));
        
        // set the slider for the speed factor add the change listener
        slider_speed.addChangeListener(new SliderSpeedListener(simulationManager));
        
        
        
        slider_range.setPaintTicks( true );
        slider_range.setAutoAdding( false );
        slider_range.setThumbOverlap( false );
        slider_range.setThumbRemovalAllowed( false );
        slider_range.setCollisionPolicy( MultiThumbSlider.Collision.NUDGE_OTHER );
        slider_range.putClientProperty(MultiThumbSliderUI.THUMB_SHAPE_PROPERTY, MultiThumbSliderUI.Thumb.Triangle);
        slider_range.addChangeListener(new SliderRangeListener(simulationManager));
        
        
        
        ButtonTrajectoryListener l = new ButtonTrajectoryListener(taskName, simulationManager, panel_subtrajectories);
        generate_subtrajectories_button.addActionListener(l);
        generate_validation_button.addActionListener(l);
        save_subtrajectories_button.addActionListener(l);
        generate_file_disco_button.addActionListener(l);
        save_index_subtrajectories_button.addActionListener(l);


        // add the action listener to the button
        ButtonSimulationListener buttonListener = new ButtonSimulationListener(simulationManager);
        button_start.addActionListener(buttonListener);
        button_print.addActionListener(buttonListener);

    }
    

    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sensor_controls_panel = new javax.swing.JPanel();
        sensor_panel_scrollPane = new javax.swing.JScrollPane();
        sensors_panel = new javax.swing.JPanel();
        javax.swing.JPanel jPanel7 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
        button_start = new javax.swing.JButton();
        label_speed = new javax.swing.JLabel();
        label_report = new javax.swing.JLabel();
        slider_speed = new javax.swing.JSlider();
        button_print = new javax.swing.JButton();
        slider_log = new javax.swing.JSlider();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        panel_subtrajectories = new javax.swing.JPanel();
        save_index_subtrajectories_button = new javax.swing.JButton();
        save_subtrajectories_button = new javax.swing.JButton();
        generate_file_disco_button = new javax.swing.JButton();
        generate_subtrajectories_button = new javax.swing.JButton();
        generate_validation_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        sensor_panel_scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Sensor Controls"));
        sensor_panel_scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sensor_panel_scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        sensors_panel.setAutoscrolls(true);
        sensors_panel.setLayout(new java.awt.GridLayout(0, 3));
        sensor_panel_scrollPane.setViewportView(sensors_panel);

        javax.swing.GroupLayout sensor_controls_panelLayout = new javax.swing.GroupLayout(sensor_controls_panel);
        sensor_controls_panel.setLayout(sensor_controls_panelLayout);
        sensor_controls_panelLayout.setHorizontalGroup(
            sensor_controls_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 189, Short.MAX_VALUE)
            .addGroup(sensor_controls_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(sensor_panel_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
        );
        sensor_controls_panelLayout.setVerticalGroup(
            sensor_controls_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 458, Short.MAX_VALUE)
            .addGroup(sensor_controls_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(sensor_panel_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE))
        );

        getContentPane().add(sensor_controls_panel, java.awt.BorderLayout.LINE_END);

        jPanel7.setLayout(new java.awt.BorderLayout());

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Simulation Controls"));

        button_start.setIcon(new javax.swing.ImageIcon(getClass().getResource("/start.png"))); // NOI18N
        button_start.setToolTipText(com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.ButtonSimulationListener.BUTTON_NAME_START);
        button_start.setActionCommand(com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.ButtonSimulationListener.COMMAND_START_SIMULATION);
        button_start.setFocusable(false);

        label_speed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        label_report.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        slider_speed.setMajorTickSpacing(4);
        slider_speed.setMaximum(com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.SimulationManager.MAX_SPEED_SLIDER_VALUE);
        slider_speed.setMinimum(com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.SimulationManager.MIN_SPEED_SLIDER_VALUE);
        slider_speed.setPaintTicks(true);
        slider_speed.setValue(com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.SimulationManager.DEFAULT_SPEED_FACTOR);
        slider_speed.setFocusable(false);

        button_print.setIcon(new javax.swing.ImageIcon(getClass().getResource("/save_image.png"))); // NOI18N
        button_print.setToolTipText(com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.ButtonSimulationListener.BUTTON_NAME_SAVE);
        button_print.setActionCommand(com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.ButtonSimulationListener.COMMAND_PRINT);
        button_print.setFocusable(false);
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        slider_log.setPaintLabels(true);
        slider_log.setPaintTicks(true);
        slider_log.setFocusable(false);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.add(slider_range, BorderLayout.CENTER); // add the graph panel in the frame

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slider_log, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(slider_speed, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_speed, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_print))
                    .addComponent(label_report, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_start)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_report, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_print))
                    .addComponent(slider_speed, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_speed, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider_log, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(button_start, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel7.add(jPanel9, java.awt.BorderLayout.PAGE_END);

        jPanel7.add(imagePanel, BorderLayout.CENTER); // add the graph panel in the frame
        getContentPane().add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel6.setAutoscrolls(true);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panel_subtrajectories.setLayout(new javax.swing.BoxLayout(panel_subtrajectories, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(panel_subtrajectories);

        save_index_subtrajectories_button.setText(ButtonTrajectoryListener.NAME_SAVE_INDEXES_SUBTRAJECTORIES);
        save_index_subtrajectories_button.setActionCommand(ButtonTrajectoryListener.COMMAND_SAVE_INDEXES_SUBTRAJECTORIES);
        save_index_subtrajectories_button.setFocusPainted(false);
        save_index_subtrajectories_button.setFocusable(false);

        save_subtrajectories_button.setText(ButtonTrajectoryListener.NAME_SAVE_SUBTRAJECTORIES);
        save_subtrajectories_button.setActionCommand(ButtonTrajectoryListener.COMMAND_SAVE_SUBTRAJECTORIES);
        save_subtrajectories_button.setFocusPainted(false);
        save_subtrajectories_button.setFocusable(false);

        generate_file_disco_button.setText(ButtonTrajectoryListener.NAME_GENERATE_FILE_DISCO);
        generate_file_disco_button.setActionCommand(ButtonTrajectoryListener.COMMAND_GENERATE_FILE_DISCO);
        generate_file_disco_button.setFocusPainted(false);
        generate_file_disco_button.setFocusable(false);

        generate_subtrajectories_button.setText(ButtonTrajectoryListener.NAME_GENERATE_SUBTRAJECTORIES);
        generate_subtrajectories_button.setActionCommand(ButtonTrajectoryListener.COMMAND_GENERATE_SUBTRAJECTORIES);
        generate_subtrajectories_button.setFocusPainted(false);
        generate_subtrajectories_button.setFocusable(false);
        generate_subtrajectories_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generate_subtrajectories_buttonActionPerformed(evt);
            }
        });

        generate_validation_button.setText(ButtonTrajectoryListener.NAME_GENERATE_VALIDATION_FILE);
        generate_validation_button.setActionCommand(ButtonTrajectoryListener.COMMAND_GENERATE_VALIDATION_FILE);
        generate_validation_button.setFocusPainted(false);
        generate_validation_button.setFocusable(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(save_index_subtrajectories_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(save_subtrajectories_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generate_file_disco_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generate_validation_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generate_subtrajectories_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generate_subtrajectories_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generate_validation_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generate_file_disco_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(save_subtrajectories_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(save_index_subtrajectories_button)
                .addContainerGap())
        );

        jPanel3.add(jPanel6, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.LINE_START);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        startFrame.removeClosedFrame(SimulationFrame.this);
    }//GEN-LAST:event_formWindowClosed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button_printActionPerformed

    private void generate_subtrajectories_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generate_subtrajectories_buttonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_generate_subtrajectories_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_print;
    private javax.swing.JButton button_start;
    private javax.swing.JButton generate_file_disco_button;
    private javax.swing.JButton generate_subtrajectories_button;
    private javax.swing.JButton generate_validation_button;
    private javax.swing.JLabel label_report;
    private javax.swing.JLabel label_speed;
    private javax.swing.JPanel panel_subtrajectories;
    private javax.swing.JButton save_index_subtrajectories_button;
    private javax.swing.JButton save_subtrajectories_button;
    private javax.swing.JPanel sensor_controls_panel;
    private javax.swing.JScrollPane sensor_panel_scrollPane;
    private javax.swing.JPanel sensors_panel;
    private javax.swing.JSlider slider_log;
    private javax.swing.JSlider slider_speed;
    // End of variables declaration//GEN-END:variables

    
    
    public static int getMajorTrickSpacing(int range) {
        int majorTrickSpacing = range / MAJOR_TRICK_SPACING_VALUE;
        if(majorTrickSpacing == 0) majorTrickSpacing = 1;
        return majorTrickSpacing;
    }
    
    public static int getMinorTrickSpacing(int range) {
        int minorTrickSpacing = getMajorTrickSpacing(range) / MINOR_TRICK_SPACING_VALUE;
        if(minorTrickSpacing == 0) minorTrickSpacing = 1;
        return minorTrickSpacing;
    }
}