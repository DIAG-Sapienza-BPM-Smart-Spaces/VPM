package com.habitmining.graphicviewerintegrated.graphicsviewer;

import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.StartFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Giovanni
 */
public class GraphicsViewerMain {
    
    //private final static String PROPERTIES_PATH = "/main/java/com/habitmining/graphicviewerintegrated/graphicsviewer/parametres.properties";
//    private final static String PROPERTIES_PATH = "parametres.properties";
//    
//    private final static String SAMPLING_VALUE = "sampling_values";
//    private final static String MDL_COST_ADVANTAGE = "mdl_cost_advantage";
//    private final static String THRESHOLD_STOP_AREA = "threshold_stop_area";
//    private final static String THRESHOL_AREA_MOVEMEMENT = "threshold_area_movement";
//    private final static String WEIGHT_INDEX_STOP = "weight_index_stop";
//    private final static String WEIGHT_INDEX_AREA = "weight_index_area";
//    private final static String WEIGHT_INDEX_MOVEMENT = "weight_index_movement";
//    
//    private final static int DEFAULT_SAMPLING_VALUE = 25;
//    private final static int DEFAULT_MDL_COST_ADVANTAGE = 25;
//    private final static double DEFAULT_THRESHOLD_STOP_AREA = 0.33;
//    private final static double DEFAULT_THRESHOL_AREA_MOVEMEMENT = 0.66;
//    private final static double DEFAULT_WEIGHT_INDEX_STOP = 0.33;
//    private final static double DEFAULT_WEIGHT_INDEX_AREA = 0.33;
//    private final static double DEFAULT_WEIGHT_INDEX_MOVEMENT = 0.33;
    
    
    private final static String APPLICATION_TITLE = "Graphics Viewer";

    public static void main(String[] args) {        
        // set the system look and feel
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch( ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex ) {
            Logger.getLogger(GraphicsViewerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Start the main frame
        java.awt.EventQueue.invokeLater(new Runnable()  {
            @Override
            public void run() {
                new StartFrame(APPLICATION_TITLE).setVisible(true);
            }
        });   

    }
    
    //TODELETE
//    public static int getSamplingValue() {
//        int mdl_cost_advantage = DEFAULT_SAMPLING_VALUE;
//        
//        Properties properties = new Properties();
//        System.err.println(PROPERTIES_PATH);
//        try {
//            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            //URL url = cl.getResource( PROPERTIES_PATH );
//            //InputStream is = url.openStream();
//            InputStream is = cl.getResourceAsStream(PROPERTIES_PATH);
//            properties.load( is );
//            mdl_cost_advantage = Integer.parseInt( properties.getProperty(SAMPLING_VALUE) ); 
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(StartFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return mdl_cost_advantage;
//    }
}