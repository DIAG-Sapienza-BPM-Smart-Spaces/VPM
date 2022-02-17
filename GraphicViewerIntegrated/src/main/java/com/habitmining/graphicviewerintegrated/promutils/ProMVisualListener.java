/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.habitmining.graphicviewerintegrated.promutils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JButton;
import org.processmining.models.graphbased.directed.fuzzymodel.MutableFuzzyGraph;

/**
 *
 * @author daniele
 */

public class ProMVisualListener implements ActionListener {
    
    private final String xmlDirName="xml";
    private final String xmlFileName="xml.xml";
    
    private MutableFuzzyGraph mfg;

    public ProMVisualListener(MutableFuzzyGraph mfg) {
        this.mfg = mfg;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = ((JButton) e.getSource()).getActionCommand();
        if (command.equals("expxml")) {
            File dir = new File(xmlDirName);
            dir.mkdirs();

            try {
                ToolsProm.fromMutableToXML(mfg, dir, xmlFileName);

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }else if(command.equals("extandvis")){
            ToolsProm.visualizeHabits(xmlDirName + File.separator+xmlFileName);
        }
    }

}
