/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.habitmining.graphicviewerintegrated.promutils;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.processmining.models.graphbased.directed.fuzzymodel.MutableFuzzyGraph;

/**
 *
 * @author daniele
 */
public class HabitFrame extends JFrame{
    
    private JComponent promComp;
    private MutableFuzzyGraph graph;
    
    public HabitFrame(JComponent promComp, MutableFuzzyGraph graph){
        
        this.graph = graph;
        this.promComp = promComp;
        
        JPanel southPanel = new JPanel();
        JButton export = new JButton("Export XML");
        //JButton process = new JButton("Export and Process");
        export.setActionCommand("expxml");
        JButton extract = new JButton("Extract Habits");
        extract.setActionCommand("extandvis");
        
        ProMVisualListener xmlListner = new ProMVisualListener(graph);
        export.addActionListener(xmlListner);
        extract.addActionListener(xmlListner);
        southPanel.add(export);
        southPanel.add(extract);
        
        add(southPanel, BorderLayout.SOUTH);
        add(promComp, BorderLayout.CENTER);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 800);
        setVisible(true);
    }
}
