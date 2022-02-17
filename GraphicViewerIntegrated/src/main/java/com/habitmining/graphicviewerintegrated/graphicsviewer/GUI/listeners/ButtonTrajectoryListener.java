package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import com.fasterxml.sort.DataReader;
import com.fasterxml.sort.DataReaderFactory;
import com.fasterxml.sort.DataWriter;
import com.fasterxml.sort.DataWriterFactory;
import com.fasterxml.sort.IteratingSorter;
import com.fasterxml.sort.SortConfig;
import com.fasterxml.sort.SortingState;
import com.fasterxml.sort.TempFileProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager.SimulationManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.csv.CSVFileReferenceUnivocityImpl;
import org.processmining.log.csv.ICSVReader;
import org.processmining.log.csv.config.CSVConfig;
import org.processmining.log.csvimport.CSVConversion;
import org.processmining.log.csvimport.CSVConversion.ConversionResult;
import org.processmining.log.csvimport.config.CSVConversionConfig;
import org.processmining.log.csvimport.config.CSVConversionConfig.CSVErrorHandlingMode;
import org.processmining.log.csvimport.config.CSVConversionConfig.CSVMapping;
import org.processmining.log.csvimport.config.CSVConversionConfig.Datatype;
import org.processmining.log.csvimport.exception.CSVConversionConfigException;
import org.processmining.log.csvimport.exception.CSVConversionException;
import org.processmining.models.graphbased.directed.fuzzymodel.MutableFuzzyGraph;
import org.processmining.models.graphbased.directed.fuzzymodel.attenuation.Attenuation;
import org.processmining.models.graphbased.directed.fuzzymodel.attenuation.LinearAttenuation;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.MetricsRepository;
import org.processmining.plugins.fuzzymodel.FastTransformerVisualization;
import org.processmining.plugins.fuzzymodel.adapter.FuzzyAdapterPlugin;
import org.processmining.plugins.fuzzymodel.miner.FuzzyMinerPlugin;
import org.rapidprom.external.connectors.prom.RapidProMGlobalContext;
import org.rapidprom.ioobjectrenderers.MetricsRepositoryIOObjectRenderer;
import org.rapidprom.ioobjects.MetricsRepositoryIOObject;
import com.habitmining.graphicviewerintegrated.promutils.CustomProMPluginContext;
import com.habitmining.graphicviewerintegrated.promutils.HabitFrame;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Set;
import java.util.SortedSet;
import javax.swing.JComponent;
import org.processmining.framework.packages.PackageDescriptor;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.plugin.PluginDescriptorID;
import org.processmining.framework.plugin.PluginManager;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.util.Pair;
import org.processmining.log.csvimport.CSVConversion.NoOpProgressListenerImpl;

import com.habitmining.trajectoryanalyzer.dto.PointDTO;
import com.habitmining.trajectoryanalyzer.dto.SubtrajectoryDTO;
import com.ning.compress.lzf.LZFInputStream;
import com.ning.compress.lzf.parallel.PLZFOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.processmining.framework.plugin.Progress;
import org.processmining.log.csv.CSVFile;
import org.processmining.log.csv.ICSVWriter;
//import org.processmining.log.csvimport.CSVSorter;
import org.processmining.log.csvimport.exception.CSVSortException;
import org.processmining.log.csvimport.handler.CSVConversionHandler;
import org.processmining.log.csvimport.handler.XESConversionHandlerImpl;
import org.processmining.models.connections.fuzzymodel.FuzzyModelConnection;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.binary.AggregateBinaryMetric;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.binary.BinaryMetric;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.unary.UnaryMetric;
import org.processmining.models.graphbased.directed.fuzzymodel.transform.BestEdgeTransformer;
import org.processmining.models.graphbased.directed.fuzzymodel.transform.FastTransformer;
import org.processmining.models.graphbased.directed.fuzzymodel.transform.FuzzyEdgeTransformer;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FMLogEvents;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FuzzyMinerLog;
import org.processmining.plugins.fuzzymodel.adapter.FastFuzzyMinerAdapted;
import org.processmining.plugins.fuzzymodel.adapter.FuzzyOptimalResult;
import org.processmining.plugins.fuzzymodel.adapter.NewConcurrencyEdgeTransformer;
//import org.processmining.plugins.fuzzymodel.adapter.TraceSimilarityMetric;

/**
 *
 * @author Giovanni
 */
public class ButtonTrajectoryListener implements ActionListener {

    private final static String EXTENSION_CSV = ".csv";

    private final static String NAME_DISPLAY_SUBTRAJECTORY_BUTTON = "sub_traj_%d";

    public final static String CLASSIFICATION_TYPE_STOP = "STOP";
    public final static String CLASSIFICATION_TYPE_AREA = "AREA";
    public final static String CLASSIFICATION_TYPE_MOVEMENT = "MOVEMENT";
    public final static String CLASSIFICATION_TYPE_NOT_DEFINED = "NOT DEFINED";

    public final static String COMMAND_GENERATE_SUBTRAJECTORIES = "graphicsviewer.GUI.COMMAND_GENERATE_SUBTRAJECTORIES";
    public final static String COMMAND_GENERATE_VALIDATION_FILE = "graphicsviewer.GUI.COMMAND_GCOMMAND_GENERATE_VALIDATION_FILE";
    public final static String COMMAND_GENERATE_FILE_DISCO = "graphicsviewer.GUI.COMMAND_GENERATE_FILE_DISCO";
    public final static String COMMAND_SAVE_SUBTRAJECTORIES = "graphicsviewer.GUI.COMMAND_SAVE_SUBTRAJECTORIES";
    public final static String COMMAND_SAVE_INDEXES_SUBTRAJECTORIES = "graphicsviewer.GUI.COMMAND_SAVE_INDEXES_SUBTRAJECTORIES";

    public final static String NAME_GENERATE_SUBTRAJECTORIES = "Calculate subtrajectories";
    public final static String NAME_GENERATE_VALIDATION_FILE = "Save Validation";
    public final static String NAME_GENERATE_FILE_DISCO = "Save Disco file";
    public final static String NAME_SAVE_SUBTRAJECTORIES = "Save subtrajectories";
    public final static String NAME_SAVE_INDEXES_SUBTRAJECTORIES = "Save indexes subtrajectories";

    private final String WORKING_FOLDER = "temp";

    private final ArrayList<SubtrajectoryDTO> subtrajectories;
    private final ArrayList<JCheckBox> boxindici;

    private final JPanel panel;
    private final SimulationManager simulationManager;
    private final String trajectoryName;

    public ButtonTrajectoryListener(String trajectoryName, SimulationManager simulationManager, JPanel panel) {
        this.subtrajectories = new ArrayList<>();
        this.boxindici = new ArrayList<>();

        this.panel = panel;
        this.simulationManager = simulationManager;
        this.trajectoryName = trajectoryName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(COMMAND_GENERATE_SUBTRAJECTORIES)) {
            getSubtrajectories();
        } else if (subtrajectories.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Subtrajectorie not loaded");
        } else if (command.equals(COMMAND_GENERATE_VALIDATION_FILE)) {
            saveValidation();
        } else if (command.equals(COMMAND_GENERATE_FILE_DISCO)) {
            saveDisco();
        } else if (command.equals(COMMAND_SAVE_SUBTRAJECTORIES)) {
            saveIndex();
        } else if (command.equals(COMMAND_SAVE_INDEXES_SUBTRAJECTORIES)) {
            saveSubtrajectories();
        }
    }

    private String matchType(String subtrajectoryType) {
        String type = CLASSIFICATION_TYPE_NOT_DEFINED;
        if (subtrajectoryType.equals(SubtrajectoryDTO.TYPE_STAY)) {
            type = CLASSIFICATION_TYPE_STOP;
        } else if (subtrajectoryType.equals(SubtrajectoryDTO.TYPE_AREA)) {
            type = CLASSIFICATION_TYPE_AREA;
        } else if (subtrajectoryType.equals(SubtrajectoryDTO.TYPE_MOVEMENT)) {
            type = CLASSIFICATION_TYPE_MOVEMENT;
        }

        return type;
    }

    public void getSubtrajectories() {
                com.habitmining.graphicviewerintegrated.promutils.ToolsProm.appendInFile("timeReport.txt", "Start Traclus Complete: " + System.currentTimeMillis());

        subtrajectories.clear();
        boxindici.clear();
        panel.removeAll();

        subtrajectories.addAll(simulationManager.calculateSubtrajectories());

        int conta = 1;
        for (SubtrajectoryDTO sbt : subtrajectories) {
            String tipo = matchType(sbt.getType());
            JLabel l = new JLabel(tipo);

            JCheckBox checkBox = new JCheckBox("");
            checkBox.setName("");
            checkBox.setFocusable(false);
            checkBox.setSelected(true);
            boxindici.add(checkBox);

            String name = String.format(NAME_DISPLAY_SUBTRAJECTORY_BUTTON, conta);
            final long begin = sbt.getStartIndex();
            final long end = sbt.getEndIndex();
            JButton b = new JButton(name);
            b.setFocusPainted(false);
            b.setFocusable(false);
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    simulationManager.changeMultisliderValues(begin, end);
                }
            });

            JPanel p = new JPanel();
            p.add(b);
            p.add(l);
            p.add(checkBox);
            panel.add(p);
            panel.revalidate();
            panel.repaint();

            conta++;
        }
                com.habitmining.graphicviewerintegrated.promutils.ToolsProm.appendInFile("timeReport.txt", "End Traclus Complete: " + System.currentTimeMillis());

    }

    private void saveValidation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            String path = fileChooser.getSelectedFile().getAbsolutePath();

            String extension = "";
            int i = path.lastIndexOf('.');
            if (i > 0) {
                extension = path.substring(i);
            }
            if (!extension.equals(EXTENSION_CSV)) {
                path += EXTENSION_CSV;
            }

            int correctCounter = 0;
            int errorCounter = 0;
            for (JCheckBox jcb : boxindici) {
                if (jcb.isSelected()) {
                    correctCounter++;
                } else {
                    errorCounter++;
                }
            }

            int totalSubtrajectories = boxindici.size();

            double percentage = correctCounter / (float) totalSubtrajectories;

            String riga = trajectoryName.replaceAll(",", "_") + "," + errorCounter + "," + correctCounter + "," + totalSubtrajectories + "," + percentage + "\n";

            FileWriter fw = null;
            try {
                fw = new FileWriter(path, true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(riga);
                bw.flush();
            } catch (IOException ex) {
                Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    private void saveDisco() {
        //TODO Insert PromLogic
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(null);
        String path = null;
        //************** PROM_PATCH**************
        String workingPath = WORKING_FOLDER;
        PrintWriter promWriter = null;
        //***************************************

        com.habitmining.graphicviewerintegrated.promutils.ToolsProm.appendInFile("timeReport.txt", "Start Prom Complete: " + System.currentTimeMillis());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            FileWriter fw = null;
            try {
                path = fileChooser.getSelectedFile().getAbsolutePath();
                //*****************************************************
                workingPath += File.separator + fileChooser.getSelectedFile().getName();
                //*****************************************************
                String extension = "";
                int i = path.lastIndexOf('.');
                if (i > 0) {
                    extension = path.substring(i);
                }
                if (!extension.equals(EXTENSION_CSV)) {
                    path += EXTENSION_CSV;
                }

                //*******************************************
                extension = "";
                i = workingPath.lastIndexOf('.');
                if (i > 0) {
                    extension = path.substring(i);
                }
                if (!extension.equals(EXTENSION_CSV)) {
                    path += EXTENSION_CSV;
                }

                fw = new FileWriter(path, false);
                //*******************************************
                File workingFile = new File(workingPath);
                workingFile.getParentFile().mkdirs();
                workingFile.createNewFile();
                System.err.println(workingFile.getAbsolutePath());
                promWriter = new PrintWriter(new FileWriter(workingFile));
                /////promWriter.println("case,timein,timeend,event");
                promWriter.println("case,timein,timeend,event,dayOfWeek,month,doorStatus");
                //*******************************************
                BufferedWriter bw = new BufferedWriter(fw);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime ldt = null;
                int id = 0;
                for (SubtrajectoryDTO st : subtrajectories) {
                    if (st.getType().equals(SubtrajectoryDTO.TYPE_MOVEMENT)) {
                        continue;
                    }
                    LocalDateTime startDateTime = Instant.ofEpochMilli(st.getStartMillisecond()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime endDateTime = Instant.ofEpochMilli(st.getEndMillisecond()).atZone(ZoneId.systemDefault()).toLocalDateTime();

                    
//                    id = 0;
                    String startDate = startDateTime.format(formatter);
                    String endDate = endDateTime.format(formatter);
                    String type = matchType(st.getType());
                    String description = st.getDescription();

                    
                    if (ldt != null) {
                        if (startDateTime.getDayOfMonth() != endDateTime.getDayOfMonth()) {
                            String endDateDay = startDate.split(" ")[0];
                            endDate = endDateDay.concat(" 06:00:00");
                        }
                        if (startDateTime.getDayOfMonth() != ldt.getDayOfMonth()) {
                            id = id + 1;
                        }
                    }

                    ldt = startDateTime;
                    
                    /////
                    int day = startDateTime.getDayOfWeek().getValue();
                    String dayName = "";
                    switch(day) {
                        case 1:
                            dayName = "Monday";
                            break;
                        case 2:
                            dayName = "Tuesday";
                            break;
                        case 3:
                            dayName = "Wednesday";
                            break;
                        case 4:
                            dayName = "Thursday";
                            break;
                        case 5:
                            dayName = "Friday";
                            break;
                        case 6:
                            dayName = "Saturday";
                            break;
                        case 7:
                            dayName = "Sunday";
                            break;
                    }
                    
                    int month = startDateTime.getMonth().getValue();
                    String monthName = "";
                    switch(month) {
                        case 1:
                            monthName = "January";
                            break;
                        case 2:
                            monthName = "February";
                            break;
                        case 3:
                            monthName = "March";
                            break;
                        case 4:
                            monthName = "April";
                            break;
                        case 5:
                            monthName = "May";
                            break;
                        case 6:
                            monthName = "June";
                            break;
                        case 7:
                            monthName = "July";
                            break;
                        case 8:
                            monthName = "August";
                            break;
                        case 9:
                            monthName = "September";
                            break;
                        case 10:
                            monthName = "October";
                            break;
                        case 11:
                            monthName = "November";
                            break;
                        case 12:
                            monthName = "December";
                            break;
                    }
                    /////
                   

                    String riga = id + "," + startDate + "," + endDate + "," + "< " + type + " " + description + " >" + "," + dayName + "," + monthName;
                    bw.write(riga);
                    bw.flush();
                    //****************************************
                    promWriter.println(riga);
                    promWriter.flush();
                    //****************************************
                }

            } catch (IOException ex) {
                Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (fw != null) {
                        fw.close();
                    }
                    //*****************************************
                    if (promWriter != null) {
                        promWriter.close();
                    }
                    //*****************************************
                } catch (IOException ex) {
                    Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            mineBestInstanceFromCSV(workingPath);
        }
        
        com.habitmining.graphicviewerintegrated.promutils.ToolsProm.appendInFile("timeReport.txt", "End Prom Complete: " + System.currentTimeMillis());

    }

    private void mineBestInstanceFromCSV(String path) {
        //PluginContext pc = new CustomProMPluginContext();
        PluginContext pc = new CustomProMPluginContext();
        FuzzyMinerPlugin fuzzyMiner = new FuzzyMinerPlugin();
        FuzzyAdapterPlugin fuzzyBestMiner = new FuzzyAdapterPlugin();

        XLog csvL = convertCSV2XES(path);
        XEventClassifier classifierMini = new XEventLifeTransClassifier();

        XLogInfo logInfoMini = XLogInfoFactory.createLogInfo(csvL, classifierMini);

        XEventClass assignEventClassMini = null;
        for (XEventClass ev : logInfoMini.getEventClasses().getClasses()) {
            if (ev.toString().equals("complete")) {
                assignEventClassMini = ev;
            }
        }

        // XLog filtered_log = new XLogImpl(log.getAttributes());
        XLog filtered_log_mini = new XLogImpl(csvL.getAttributes());

        // for (XTrace trace : log) {
        for (XTrace trace : csvL) {
            XTrace copyTrace = new XTraceImpl(trace.getAttributes());
            for (XEvent event : trace) {
                if (logInfoMini.getEventClasses().getClassOf(event).equals(assignEventClassMini)) {
                    copyTrace.add(event);
                }
            }
            if (copyTrace.size() > 0) {
                filtered_log_mini.add(copyTrace);
            }
        }

        FastTransformerVisualization ftvMini = new FastTransformerVisualization();
        //Compute metric repo
        System.out.println("Computing repository");
        MetricsRepository fuzzyMetricRepoModel = fuzzyMiner.mineDefault(pc, filtered_log_mini);

        //MetricsRepositoryIOObject metricsRepository = new MetricsRepositoryIOObject(fuzzyMetricRepoModel,pc);
        //Compute best instance mutable fuzzy graph
        System.out.println("Computing best");
        /////MutableFuzzyGraph fuzzyModelBest = fuzzyBestMiner.mineGeneric(pc, fuzzyMetricRepoModel);
        long time = System.currentTimeMillis();
        MutableFuzzyGraph fuzzyModelBest = adapt(pc, fuzzyMetricRepoModel);
        pc.addConnection(new FuzzyModelConnection(fuzzyModelBest));
        time = System.currentTimeMillis() - time;
        String logStr = new String("Select Best Fuzzy Instance: Took " + time + " ms.");
        pc.log(logStr, org.processmining.framework.plugin.events.Logger.MessageLevel.NORMAL);
        System.out.println(logStr);
        
        
        
        
        //visualize no filter
        //javax.swing.JComponent compMini = ftvMini.visualize(pc, fuzzyModelMini);
        System.out.println("Showing it...");
        //visualize best instance
        JComponent compMini = ftvMini.visualize(pc, fuzzyModelBest);
//        javax.swing.JComponent compMini = ftvMini.visualize(pc, fuzzyMetricRepoModel);
        HabitFrame frameMini = new HabitFrame(compMini, fuzzyModelBest);
//        frameMini.add(compMini);
//        frameMini.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frameMini.setSize(1000, 800);
//        frameMini.setVisible(true);
    }

    public static XLog convertCSV2XES(String csvFilename) {
        CSVFileReferenceUnivocityImpl csvFile = new CSVFileReferenceUnivocityImpl(new File(csvFilename).toPath());
        CSVConfig config = null;
        ;
        try {
            config = new CSVConfig(csvFile);
        } catch (IOException ex) {
            Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        // TODO Auto-generated catch block
        // System.out.println(config.getSeparator());
        // System.out.println(config.getCharset());
        // System.out.println(config.getEscapeChar());
        try (ICSVReader reader = csvFile.createReader(config)) {
            CSVConversion conversion = new CSVConversion();
            CSVConversionConfig conversionConfig = new CSVConversionConfig(csvFile, config);
            conversionConfig.autoDetect();
            //TODO Scrivere queste righe come prima cosa nel file di disco
            conversionConfig.setCaseColumns(ImmutableList.of("case"));
            conversionConfig.setEventNameColumns(ImmutableList.of("event"));
            conversionConfig.setCompletionTimeColumn("timeend");
            conversionConfig.setStartTimeColumn("timein");
            // conversionConfig.setEmptyCellHandlingMode(CSVEmptyCellHandlingMode.SPARSE);
            conversionConfig.setErrorHandlingMode(CSVErrorHandlingMode.ABORT_ON_ERROR);
            Map<String, CSVMapping> conversionMap = conversionConfig.getConversionMap();
            CSVMapping mapping = conversionMap.get("timein");
            mapping.setDataType(Datatype.TIME);
            mapping.setPattern("yyyy/MM/dd");

            ConversionResult<XLog> result = conversion.doConvertCSVToXES(new NoOpProgressListenerImpl(), csvFile, config, conversionConfig);

            XLog log = result.getResult();
            return log;
        } catch (CSVConversionConfigException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CSVConversionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    private void saveIndex() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            FileWriter fw = null;
            try {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                fw = new FileWriter(path, false);
                BufferedWriter bw = new BufferedWriter(fw);
                String header = "startIndex\tendIndex\tstartMillisecond\tendMillisecond\ttype\n";
                bw.write(header);
                for (SubtrajectoryDTO st : subtrajectories) {
                    if (st.getType().equals(SubtrajectoryDTO.TYPE_MOVEMENT)) {
                        continue;
                    }
                    String riga = st.getStartIndex() + "\t";
                    riga += st.getEndIndex() + "\t";
                    riga += st.getStartMillisecond() + "\t";
                    riga += st.getEndMillisecond() + "\t";
                    riga += matchType(st.getType()) + "\n";
                    bw.write(riga);
                    bw.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void saveSubtrajectories() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            FileWriter fw = null;
            try {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                fw = new FileWriter(path, false);
                BufferedWriter bw = new BufferedWriter(fw);

                ArrayList<PointDTO> points = subtrajectories.get(0).getSubtrajectoryPoints();
                PointDTO firstPoint = points.get(0);

                String header = "SensorID";
                for (int m = 0; m < firstPoint.getPointCoordinates().length; m++) {
                    header += "\t" + "coordinate" + m;
                }
                header += "\t" + "timestamp";
                header += "\t" + "caracteristicPoint";
                header += "\n";

                bw.write(header);

                int size = points.size();
                int endIndex = size - 1;
                for (int n = 0; n < size; n++) {
                    PointDTO point = points.get(n);

                    String riga = point.getPointID();
                    double[] coordinate = point.getPointCoordinates();
                    for (int m = 0; m < coordinate.length; m++) {
                        riga += "\t" + (int) coordinate[m];
                    }
                    riga += "\t" + point.getTimestamp();
                    riga += "\t" + (n == endIndex || n == 0);
                    riga += "\n";

                    bw.write(riga);
                    bw.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ButtonTrajectoryListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String EDGE_TRANSFORMER_SELECTION = "EdgeTransformerSelection";
    private static final String EDGE_TRANSFORMER_SELECTION_BEST_EDGES = "EdgeTransformerSelectionBestEdges";
    private static final String EDGE_TRANSFORMER_SELECTION_FUZZY_EDGES = "EdgeTransformerSelectionFuzzyEdges";
    private static final String CONCURRENCY_EDGE_TRANSFORMER_ACTIVE = "ConcurrencyEdgeTransformerActive";
    private static final String NODE_CUTOFF = "NodeCutoff";
    private static final String FUZZY_EDGE_RATIO = "FuzzyEdgeRatio";
    private static final String FUZZY_EDGE_CUTOFF = "FuzzyEdgeCutoff";
    private static final String CONCURRENCY_THRESHOLD = "ConcurrencyThreshold";
    private static final String CONCURRENCY_RATIO = "ConcurrencyRatio";
    private static final String EDGES_FUZZY_IGNORE_LOOPS = "EdgesFuzzyIgnoreLoops";
    private static final String EDGES_FUZZY_INTERPRET_ABSOLUTE = "EdgesFuzzyInterpretAbsolute";
    private MutableFuzzyGraph adapt(PluginContext context, MetricsRepository repository) {
        MutableFuzzyGraph mfg = new MutableFuzzyGraph(repository);
        XLog log = repository.getLogReader();
        XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);

        TraceSimilarityMetric tsm = new TraceSimilarityMetric(logInfo, repository);
        double logComplexity = (logInfo.getNumberOfEvents() > 50000 ? 1600 : tsm.measure());

        mfg.initializeGraph();
        System.out.println("mfg.initializeGraph()");
        mfg.setBinaryRespectiveSignificance();
        System.out.println("mfg.setBinaryRespectiveSignificance()");
        AggregateBinaryMetric edgeLogSignificance = repository.getAggregateSignificanceBinaryLogMetric();
        int numberofEventClasses = logInfo.getEventClasses().size();
        int numTr = FuzzyMinerLog.getTraces(log).size();
        int numberofEC = logInfo.getEventClasses().size();
        int numberofEvents = logInfo.getNumberOfEvents();
        System.out.println("number of event classes: " + numberofEventClasses + "\nnumber of traces " + numTr + "\nnumber of events " + numberofEvents);
        double avgEvents = numberofEvents / (numTr * 1.0);
        double weight = avgEvents / numberofEC;
        double timeCom = numberofEventClasses * numTr * (weight > 1.0 ? Math.pow(1.5, weight) : 1.0);

        double originalEdgesDetail = 0.0;
        int tEdges = 0;
        for (int x = 0; x < numberofEventClasses; x++) {
            for (int y = 0; y < numberofEventClasses; y++) {
                if (edgeLogSignificance.getMeasure(x, y) > 0) {
                    tEdges++;
                }
                originalEdgesDetail += mfg.getBinarySignificance(x, y);
            }
        }

        int nofNodes = mfg.getNumberOfInitialNodes();
        int total = nofNodes * nofNodes;
        int count = 0;
        double[] preserveall = new double[total];
        double[] ratioall = new double[total];
        double sumOfPreserve = 0;
        double sumOfRatio = 0;
        System.out.println("Number of initial nodes: " + nofNodes);
        for (int x = 0; x < nofNodes; x++) {
            for (int y = 0; y < x; y++) {
                double relImpAB = mfg.getBinaryRespectiveSignificance(x, y);
                double relImpBA = mfg.getBinaryRespectiveSignificance(y, x);

                if (relImpAB > 0.0 && relImpBA > 0.0) {
                    // conflict situation
                    if (relImpAB > relImpBA) {
                        preserveall[count] = relImpBA;
                        ratioall[count] = Math.min(relImpAB, relImpBA) / Math.max(relImpAB, relImpBA);
                        sumOfPreserve = sumOfPreserve + relImpBA;
                        sumOfRatio = sumOfRatio + Math.min(relImpAB, relImpBA) / Math.max(relImpAB, relImpBA);
                    } else {
                        preserveall[count] = relImpAB;
                        ratioall[count] = Math.min(relImpAB, relImpBA) / Math.max(relImpAB, relImpBA);
                        sumOfPreserve = sumOfPreserve + relImpAB;
                        sumOfRatio = sumOfRatio + Math.min(relImpAB, relImpBA) / Math.max(relImpAB, relImpBA);
                    }
                    count++;

                }
            }
        }
        Arrays.sort(preserveall);
        Arrays.sort(ratioall);
        double avgOfPreserve;
        double avgOfRatio;
        if (count > 0) {
            avgOfPreserve = sumOfPreserve / (count * 1.0);
            avgOfRatio = sumOfRatio / (count * 1.0);
        } else {
            avgOfPreserve = 0.0;
            avgOfRatio = 0.0;
//			preserveall = null;
//			ratioall = null;
        }
        int n = mfg.getNumberOfInitialNodes();
        double[] nodesigall = new double[n];
        UnaryMetric nodeSignificance = repository.getAggregateUnaryLogMetric();
        for (int i = 0; i < n; i++) {
            int j = 0;
            for (j = 0; j < nodesigall.length; j++) {
                if (nodesigall[j] == nodeSignificance.getMeasure(i)) {
                    break;
                }
            }
            if (j >= nodesigall.length) {
                nodesigall[i] = nodeSignificance.getMeasure(i);
            }
        }
        Arrays.sort(nodesigall);

        FastTransformer fastTransformer = new FastTransformer(context);
        BestEdgeTransformer bestEdgeTransformer = new BestEdgeTransformer(context);
        FuzzyEdgeTransformer fuzzyEdgeTransformer = new FuzzyEdgeTransformer(context);
        NewConcurrencyEdgeTransformer concurrencyEdgeTransformer = new NewConcurrencyEdgeTransformer(context);

        double nodeThreshold;
        double conformance = 0.8;
        if (nofNodes > 0) {
            nodeThreshold = mfg.getThresholdShowingPrimitives(nofNodes) - mfg.getMinimalNodeSignificance();
            nodeThreshold = nodeThreshold / (1.0 - mfg.getMinimalNodeSignificance());
            fastTransformer.setThreshold(nodeThreshold);
            fastTransformer.addInterimTransformer(fuzzyEdgeTransformer);
            fuzzyEdgeTransformer.setSignificanceCorrelationRatio(0.75);
            fuzzyEdgeTransformer.setPreservePercentage(0.2);
            fuzzyEdgeTransformer.setIgnoreSelfLoops(true);
            fuzzyEdgeTransformer.setInterpretPercentageAbsolute(false);
            fastTransformer.addPreTransformer(concurrencyEdgeTransformer);
            concurrencyEdgeTransformer.setPreserveThreshold(0.6);
            concurrencyEdgeTransformer.setRatioThreshold(0.7);
        } else {
            fastTransformer.setThreshold(0.0);
            fastTransformer.addInterimTransformer(fuzzyEdgeTransformer);
            fuzzyEdgeTransformer.setSignificanceCorrelationRatio(0.75);
            fuzzyEdgeTransformer.setPreservePercentage(1.0);
            fuzzyEdgeTransformer.setIgnoreSelfLoops(true);
            fuzzyEdgeTransformer.setInterpretPercentageAbsolute(false);
            fastTransformer.addPreTransformer(concurrencyEdgeTransformer);
            concurrencyEdgeTransformer.setPreserveThreshold(1.0);
            concurrencyEdgeTransformer.setRatioThreshold(0.7);
        }
        System.out.println("*****************************");
        String edgeTransformerSelection = mfg.getAttribute(EDGE_TRANSFORMER_SELECTION);
        if (edgeTransformerSelection != null) {
            if (edgeTransformerSelection.equalsIgnoreCase(EDGE_TRANSFORMER_SELECTION_BEST_EDGES)) {
                fastTransformer.addInterimTransformer(bestEdgeTransformer);
            } else if (edgeTransformerSelection.equalsIgnoreCase(EDGE_TRANSFORMER_SELECTION_FUZZY_EDGES)) {
                fastTransformer.addInterimTransformer(fuzzyEdgeTransformer);
            }
        }
        String concurrencyTransformerActive = mfg.getAttribute(CONCURRENCY_EDGE_TRANSFORMER_ACTIVE);
        if (concurrencyTransformerActive != null) {
            if (concurrencyTransformerActive.equals(TRUE)) {
                fastTransformer.addPreTransformer(concurrencyEdgeTransformer);
            } else if (concurrencyTransformerActive.equals(FALSE)) {
                fastTransformer.removePreTransformer(concurrencyEdgeTransformer);
            }
        }
        String nodeCutoff = mfg.getAttribute(NODE_CUTOFF);
        if (nodeCutoff != null) {
            fastTransformer.setThreshold(Double.parseDouble(nodeCutoff));
        }
        String fuzzyEdgeRatio = mfg.getAttribute(FUZZY_EDGE_RATIO);
        if (fuzzyEdgeRatio != null) {
            fuzzyEdgeTransformer.setSignificanceCorrelationRatio(Double.parseDouble(fuzzyEdgeRatio));
        }
        String fuzzyEdgeCutoff = mfg.getAttribute(FUZZY_EDGE_CUTOFF);
        if (fuzzyEdgeCutoff != null) {
            fuzzyEdgeTransformer.setPreservePercentage(Double.parseDouble(fuzzyEdgeCutoff));
        }
        String concurrencyThreshold = mfg.getAttribute(CONCURRENCY_THRESHOLD);
        if (concurrencyThreshold != null) {
            concurrencyEdgeTransformer.setPreserveThreshold(Double.parseDouble(concurrencyThreshold));
        }
        String concurrencyRatio = mfg.getAttribute(CONCURRENCY_RATIO);
        if (concurrencyRatio != null) {
            concurrencyEdgeTransformer.setRatioThreshold(Double.parseDouble(concurrencyRatio));
        }
        String ignoreLoops = mfg.getAttribute(EDGES_FUZZY_IGNORE_LOOPS);
        if (ignoreLoops != null) {
            if (ignoreLoops.equals(TRUE)) {
                fuzzyEdgeTransformer.setIgnoreSelfLoops(true);
            } else if (ignoreLoops.equals(FALSE)) {
                fuzzyEdgeTransformer.setIgnoreSelfLoops(false);
            }
        }
        String interpretAbsolute = mfg.getAttribute(EDGES_FUZZY_INTERPRET_ABSOLUTE);
        if (interpretAbsolute != null) {
            if (interpretAbsolute.equals(TRUE)) {
                fuzzyEdgeTransformer.setInterpretPercentageAbsolute(true);
            } else if (interpretAbsolute.equals(FALSE)) {
                fuzzyEdgeTransformer.setInterpretPercentageAbsolute(false);
            }
        }
        System.out.println("*****************************");   
        FastFuzzyMinerAdapted fastfuzzymineradapted = new FastFuzzyMinerAdapted(context, mfg, log, conformance,
                repository, logComplexity, count, avgOfPreserve, avgOfRatio, timeCom, tEdges, originalEdgesDetail,
                preserveall, ratioall, 1, logInfo, nodesigall);
        fastfuzzymineradapted.calculation();

        FuzzyOptimalResult fuzzyoptimalresult = fastfuzzymineradapted.getMaxOptimalvalueFuzzyResult(fastfuzzymineradapted
                .getfuzzyoptimalresults());

        mfg.setAttribute(EDGES_FUZZY_IGNORE_LOOPS, TRUE);
        mfg.setAttribute(EDGES_FUZZY_INTERPRET_ABSOLUTE, FALSE);
        mfg.setAttribute(CONCURRENCY_EDGE_TRANSFORMER_ACTIVE, TRUE);
        mfg.setAttribute(CONCURRENCY_RATIO, Double.toString(fuzzyoptimalresult.ratio));
        mfg.setAttribute(CONCURRENCY_THRESHOLD, Double.toString(fuzzyoptimalresult.preserve));
        mfg.setAttribute(FUZZY_EDGE_CUTOFF, Double.toString(fuzzyoptimalresult.cutoff));
        mfg.setAttribute(FUZZY_EDGE_RATIO, Double.toString(fuzzyoptimalresult.utility));
        mfg.setAttribute(NODE_CUTOFF, Double.toString(fuzzyoptimalresult.nodesig));
        mfg.setAttribute(EDGE_TRANSFORMER_SELECTION, EDGE_TRANSFORMER_SELECTION_FUZZY_EDGES);
        fastTransformer.setThreshold(fuzzyoptimalresult.nodesig);
        fastTransformer.addInterimTransformer(fuzzyEdgeTransformer);
        fuzzyEdgeTransformer.setSignificanceCorrelationRatio(fuzzyoptimalresult.utility);
        fuzzyEdgeTransformer.setPreservePercentage(fuzzyoptimalresult.cutoff);
        fuzzyEdgeTransformer.setIgnoreSelfLoops(true);
        fuzzyEdgeTransformer.setInterpretPercentageAbsolute(false);
        fastTransformer.addPreTransformer(concurrencyEdgeTransformer);
        concurrencyEdgeTransformer.setPreserveThreshold(fuzzyoptimalresult.preserve);
        concurrencyEdgeTransformer.setRatioThreshold(fuzzyoptimalresult.ratio);

        return mfg;
    }
    
}

class TraceSimilarityMetric {

    protected List<XTrace> logTraces;
    protected FMLogEvents logEvents;
    protected int numberofEvents;
    protected int Tedges;
    protected BinaryMetric edgeSignificance;
    protected double LogComplexity;
    protected XLogInfo logsummary = null;
    protected MetricsRepository aRepository;

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.mining.fuzzymining.metrics.TraceMetric#measure(org.
	 * processmining.framework.log.LogReader)
     */
    public TraceSimilarityMetric(XLogInfo logsummary, MetricsRepository aRepository) {
        this.logsummary = logsummary;
        this.aRepository = aRepository;

        /*
		 * super("Trace similarity",
		 * "Measures two trace by the number of intersect contained event class relations compared to the total relation they have."
		 * , aRepository);
         */
    }

    protected double getFollowingRelation(XTrace pi1, XTrace pi2, int size) {
        DoubleMatrix2D followRelations, followRelations1, followRelations2;
        int AteIndex, followerAteIndex;
        if (size < 512) {
            followRelations = DoubleFactory2D.dense.make(size, size, 0.0);
            followRelations1 = DoubleFactory2D.dense.make(size, size, 0.0);
            followRelations2 = DoubleFactory2D.dense.make(size, size, 0.0);
        } else {
            followRelations = DoubleFactory2D.sparse.make(size, size, 0.0);
            followRelations1 = DoubleFactory2D.sparse.make(size, size, 0.0);
            followRelations2 = DoubleFactory2D.sparse.make(size, size, 0.0);
        }
        for (int k = 0; k < pi1.size(); k++) {
            XEvent Ate = pi1.get(k);
            AteIndex = logEvents.findLogEventNumber(Ate);
            for (int n = k + 1; n < pi1.size(); n++) {
                XEvent followerAte = pi1.get(n);
                followerAteIndex = logEvents.findLogEventNumber(followerAte);
                followRelations.set(AteIndex, followerAteIndex, 1.0);
                followRelations1.set(AteIndex, followerAteIndex, 1.0);
            }
        }
        for (int k = 0; k < pi2.size(); k++) {
            XEvent Ate = pi2.get(k);
            AteIndex = logEvents.findLogEventNumber(Ate);
            for (int n = k + 1; n < pi2.size(); n++) {
                XEvent followerAte = pi2.get(n);
                followerAteIndex = logEvents.findLogEventNumber(followerAte);
                followRelations.set(AteIndex, followerAteIndex, 1.0);
                followRelations2.set(AteIndex, followerAteIndex, 1.0);
            }
        }
        int followRelationSizeM = 0;
        int followRelationSizeInt = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (followRelations.get(i, j) > 0.0) {
                    followRelationSizeM++;
                }
                if (followRelations1.get(i, j) > 0.0 && followRelations2.get(i, j) > 0.0) {
                    followRelationSizeInt++;
                }
            }
        }
        double ratio;
        if (followRelationSizeM == 0) {
            ratio = 0;
        } else {
            ratio = followRelationSizeInt / (followRelationSizeM * 1.0);
        }
        return ratio;
    }

    protected double getEventIntersectionRatio(XTrace pi1, XTrace pi2, int size) {
        int totalEvents = 0;
        int Ate1Index, Ate2Index;
        int intersectNum = 0;

        boolean pi1T = false;
        boolean pi2T = false;

        for (int k = 0; k < size; k++) {
            for (XEvent Ate1 : pi1) {
                Ate1Index = logEvents.findLogEventNumber(Ate1);
                if (k == Ate1Index) {
                    pi1T = true;
                    break;
                }
            }
            for (XEvent Ate2 : pi2) {
                Ate2Index = logEvents.findLogEventNumber(Ate2);
                if (k == Ate2Index) {
                    pi2T = true;
                    break;
                }
            }
            if (pi1T && pi2T) {
                intersectNum++;
            }
            if ((pi1T == false && pi2T == false) == false) {
                totalEvents++;
            }
            pi1T = false;
            pi2T = false;
        }
        return intersectNum / (totalEvents * 1.0);
    }

    public double measure() {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub        
        //---determine the log complexity
        AggregateBinaryMetric edgeLogSignificance = aRepository.getAggregateSignificanceBinaryLogMetric();
        double tracesimilarity = 0.0;
        double eventsSimilarity = 0.0;
        int traceRelativeNumber = 0;

        numberofEvents = logsummary.getEventClasses().size();
        XLog log = logsummary.getLog();
        logTraces = log; //FuzzyMinerLog.getTraces(log);
        logEvents = FuzzyMinerLog.getLogEvents(log);
        int numberofTr = logsummary.getNumberOfTraces();

        Tedges = 0;
        for (int x = 0; x < numberofEvents; x++) {
            for (int y = 0; y < numberofEvents; y++) {
                if (edgeLogSignificance.getMeasure(x, y) > 0) {
                    Tedges++;
                }
            }
        }
        //---determine the log complexity
        Random r = new Random();
        int p = 0;
        int numberOfEs = logsummary.getNumberOfEvents();
        int loopsize;
        //long logsize=numberOfEs*numberofTr;
        int AvgEvents = numberOfEs / numberofTr;
        //logsize=logsummary.getNumberOfEvents();

        if (AvgEvents > 20) {
            loopsize = 1;
        } else {
            loopsize = 20;
        }
        while (p < loopsize) {
            XTrace pi1, pi2;
            int k = r.nextInt(numberofTr);
            int n = r.nextInt(numberofTr);
            pi1 = logTraces.get(k);
            pi2 = logTraces.get(n);
            if (k == n) {
                tracesimilarity = tracesimilarity + getFollowingRelation(pi1, pi2, numberofEvents);
                eventsSimilarity = eventsSimilarity + getEventIntersectionRatio(pi1, pi2, numberofEvents);
                traceRelativeNumber++;
                p++;
            }
        }

        double traceS = tracesimilarity / (traceRelativeNumber * 1.0);
        double traceE = eventsSimilarity / (traceRelativeNumber * 1.0);
        double AggregetedTS = 0.2 * traceE + 0.8 * traceS;
        double u = Math.pow(2, 1 - AggregetedTS);
        LogComplexity = Math.pow(numberofEvents, u);
        if (LogComplexity == 0) {
            LogComplexity = Math.pow(numberofEvents, (Tedges * 1.0) / (numberofEvents ^ 2));
        }
        return LogComplexity;
    }

    public double getLogComplexity() {
        return LogComplexity;
    }
}

/*
final class CSVConversion {

    public interface ConversionResult<R> {
        R getResult();
        boolean hasConversionErrors();
        String getConversionErrors();
    }

    public interface ProgressListener {
        Progress getProgress();
        void log(String message);
    }

    public final static class NoOpProgressListenerImpl implements ProgressListener {

        public void log(String message) {
        }

        public Progress getProgress() {
            return new NoOpProgressImpl();
        }
    }

    private final static class NoOpProgressImpl implements Progress {

        public void setValue(int value) {
        }

        public void setMinimum(int value) {
        }

        public void setMaximum(int value) {
        }

        public void setIndeterminate(boolean makeIndeterminate) {
        }

        public void setCaption(String message) {
        }

        public boolean isIndeterminate() {
            return false;
        }

        public boolean isCancelled() {
            return false;
        }

        public void inc() {
        }

        public int getValue() {
            return 0;
        }

        public int getMinimum() {
            return 0;
        }

        public int getMaximum() {
            return 0;
        }

        public String getCaption() {
            return null;
        }

        public void cancel() {
        }
    }

    private static final class ImportOrdering extends Ordering<String[]> {

        private final int[] caseIndices;

        private final int completionTimeIndex;
        private final CSVMapping completionTimeMapping;

        private final CSVErrorHandlingMode errorHandlingMode;

        public ImportOrdering(int[] indices, Map<Integer, CSVMapping> mappingMap, int completionTimeIndex,
                int startTimeIndex, CSVErrorHandlingMode errorHandlingMode) {
            this.caseIndices = indices;
            this.completionTimeIndex = completionTimeIndex;
            this.completionTimeMapping = mappingMap.get(completionTimeIndex);
            this.errorHandlingMode = errorHandlingMode;
        }

        public int compare(String[] o1, String[] o2) {
            if (o1.length != o2.length) {
                throw new IllegalArgumentException(
                        "Can only compare lines in a CSV file with the same number of columns!");
            }
            // First compare on all the case columns
            for (int i = 0; i < caseIndices.length; i++) {
                int index = caseIndices[i];
                // We treat empty and NULL cells as the same as there is no concept of a NULL cell in CSV 
                String s1 = o1[index] == null ? "" : o1[index];
                String s2 = o2[index] == null ? "" : o2[index];
                int comp = s1.compareTo(s2);
                if (comp != 0) {
                    // Case ID is different on current index
                    return comp;
                }
            }
            // Belongs to the same case over all indices, compare on completion time
            if (completionTimeIndex != -1) {
                // Sort by completion time
                System.out.println(completionTimeMapping);
                System.out.println(o1[completionTimeIndex]);
                System.out.println(o2[completionTimeIndex]);
                return compareTime(completionTimeMapping, o1[completionTimeIndex], o2[completionTimeIndex]);
            } else {
                // Keep ordering -> using a stable sort algorithm
                return 0;
            }
        }

        private int compareTime(CSVMapping mapping, String t1, String t2) {
            Date d1;
            try {
                d1 = parseDate((DateFormat) mapping.getFormat(), t1);
            } catch (ParseException e) {
                if (errorHandlingMode == CSVErrorHandlingMode.ABORT_ON_ERROR) {
                    throw new IllegalArgumentException("Cannot parse date: " + t1);
                } else {
                    d1 = new Date(0);
                }
            }
            Date d2;
            try {
                d2 = parseDate((DateFormat) mapping.getFormat(), t2);
            } catch (ParseException e) {
                if (errorHandlingMode == CSVErrorHandlingMode.ABORT_ON_ERROR) {
                    throw new IllegalArgumentException("Cannot parse date: " + t2);
                } else {
                    d2 = new Date(0);
                }
            }
            return d1.compareTo(d2);
        }
    }

    /**
     * Convert a {@link CSVFileReferenceOpenCSVImpl} into an {@link XLog} using
     * the supplied configuration.
     *
     * @param progressListener
     * @param csvFile
     * @param importConfig
     * @param conversionConfig
     * @return
     * @throws CSVConversionException
     * @throws CSVConversionConfigException
     *
    public ConversionResult<XLog> doConvertCSVToXES(final ProgressListener progressListener, CSVFile csvFile,
            CSVConfig importConfig, CSVConversionConfig conversionConfig) throws CSVConversionException,
            CSVConversionConfigException {
        return convertCSV(progressListener, importConfig, conversionConfig, csvFile, new XESConversionHandlerImpl(
                importConfig, conversionConfig));
    }

    /**
     * Converts a {@link CSVFileReferenceOpenCSVImpl} into something determined
     * by the supplied {@link CSVConversionHandler}. Use
     * {@link #doConvertCSVToXES(ProgressListener, CSVFileReferenceOpenCSVImpl, CSVConfig, CSVConversionConfig)}
     * in case you want to convert to an {@link XLog}.
     *
     * @param progress
     * @param importConfig
     * @param conversionConfig
     * @param csvFile
     * @param conversionHandler
     * @return
     * @throws CSVConversionException
     * @throws CSVConversionConfigException
     *
    public <R> ConversionResult<R> convertCSV(ProgressListener progress, CSVConfig importConfig,
            CSVConversionConfig conversionConfig, CSVFile csvFile, final CSVConversionHandler<R> conversionHandler)
            throws CSVConversionException, CSVConversionConfigException {

        Progress p = progress.getProgress();

        //TODO can we provide determinate progress? maybe based on bytes of CSV read
        p.setMinimum(0);
        p.setMaximum(1);
        p.setValue(0);
        p.setIndeterminate(true);

        long startCSVTime = System.currentTimeMillis();

        conversionHandler.startLog(csvFile);

        int[] caseColumnIndex = new int[conversionConfig.getCaseColumns().size()];
        int[] eventNameColumnIndex = new int[conversionConfig.getEventNameColumns().size()];
        int completionTimeColumnIndex = -1;
        int startTimeColumnIndex = -1;
        String[] header = null;

        final Map<String, Integer> headerMap = new HashMap<>();
        final Map<Integer, CSVMapping> mappingMap = new HashMap<>();

        try {
            header = csvFile.readHeader(importConfig);
            for (int i = 0; i < header.length; i++) {
                String columnHeader = header[i];
                Integer oldIndex = headerMap.put(columnHeader, i);
                if (oldIndex != null) {
                    throw new CSVConversionException(
                            String.format(
                                    "Ambigous header in the CSV file: Two columns (%s, %s) have the same header %s. Please fix this in the CSV file!",
                                    oldIndex, i, columnHeader));
                }
                CSVMapping columnMapping = conversionConfig.getConversionMap().get(columnHeader);
                mappingMap.put(i, columnMapping);
            }

            for (int i = 0; i < conversionConfig.getCaseColumns().size(); i++) {
                caseColumnIndex[i] = headerMap.get(conversionConfig.getCaseColumns().get(i));
            }
            for (int i = 0; i < conversionConfig.getEventNameColumns().size(); i++) {
                eventNameColumnIndex[i] = headerMap.get(conversionConfig.getEventNameColumns().get(i));
            }
            if (conversionConfig.getCompletionTimeColumn() != "") {
                completionTimeColumnIndex = headerMap.get(conversionConfig.getCompletionTimeColumn());
            }
            if (conversionConfig.getStartTimeColumn() != "") {
                startTimeColumnIndex = headerMap.get(conversionConfig.getStartTimeColumn());
            }
        } catch (IOException e) {
            throw new CSVConversionException("Could not read first row of CSV file with header information", e);
        }

        InputStream sortedCsvInputStream = null;
        File sortedFile = null;

        try {
            try {
                long startSortTime = System.currentTimeMillis();
                int maxMemory = (int) ((Runtime.getRuntime().maxMemory() * 0.30) / 1024 / 1024);
                progress.log(String.format(
                        "Sorting CSV file (%.2f MB) by case and time using maximal %s MB of memory ...",
                        (getFileSizeInBytes(csvFile) / 1024 / 1024), maxMemory));
                Ordering<String[]> caseComparator = new ImportOrdering(caseColumnIndex, mappingMap,
                        completionTimeColumnIndex, startTimeColumnIndex, conversionConfig.getErrorHandlingMode());
                sortedFile = CSVSorter.sortCSV(csvFile, caseComparator, importConfig, maxMemory, header.length,
                        progress);
                sortedCsvInputStream = new LZFInputStream(new FileInputStream(sortedFile));
                long endSortTime = System.currentTimeMillis();
                progress.log(String.format("Finished sorting in %d seconds", (endSortTime - startSortTime) / 1000));
            } catch (IllegalArgumentException e) {
                throw new CSVSortException("Could not sort CSV file", e);
            } catch (IOException e) {
                throw new CSVSortException("Could not sort CSV file", e);
            }

            // The following code assumes that the file is sorted by cases and written to disk compressed with LZF
            progress.log("Reading cases ...");
            try ( ICSVReader reader = csvFile.getCSV().createReader(sortedCsvInputStream, importConfig)) {

                int caseIndex = 0;
                int eventIndex = 0;
                int lineIndex = -1;
                String[] nextLine;
                String currentCaseId = null;

                while ((nextLine = reader.readNext()) != null && (caseIndex % 1000 != 0 || !p.isCancelled())) {
                    lineIndex++;

                    final String newCaseID = readCompositeAttribute(caseColumnIndex, nextLine,
                            conversionConfig.getCompositeAttributeSeparator());

                    // Handle new traces
                    if (!newCaseID.equals(currentCaseId)) {

                        if (currentCaseId != null) {
                            // Finished with current case
                            conversionHandler.endTrace(currentCaseId);
                        }

                        // Update current case id to next case id
                        currentCaseId = newCaseID;

                        // Create new case
                        conversionHandler.startTrace(currentCaseId);
                        caseIndex++;

                        if (caseIndex % 1000 == 0) {
                            progress.log("Reading line " + lineIndex + ", already " + caseIndex + " cases and "
                                    + eventIndex + " events processed ...");
                        }

                    }

                    // Create new event
                    try {

                        // Read event name
                        final String eventClass = readCompositeAttribute(eventNameColumnIndex, nextLine,
                                conversionConfig.getCompositeAttributeSeparator());

                        // Read time stamps
                        final Date completionTime = completionTimeColumnIndex != -1 ? parseDate((DateFormat) mappingMap
                                .get(completionTimeColumnIndex).getFormat(), nextLine[completionTimeColumnIndex])
                                : null;
                        final Date startTime = startTimeColumnIndex != -1 ? parseDate(
                                (DateFormat) mappingMap.get(startTimeColumnIndex).getFormat(),
                                nextLine[startTimeColumnIndex]) : null;

                        conversionHandler.startEvent(eventClass, completionTime, startTime);

                        for (int i = 0; i < nextLine.length; i++) {
                            if (Ints.contains(eventNameColumnIndex, i) || Ints.contains(caseColumnIndex, i)
                                    || i == completionTimeColumnIndex || i == startTimeColumnIndex) {
                                // Is already mapped to a special column, do not include again
                                continue;
                            }

                            final String name = header[i];
                            final String value = nextLine[i];

                            if (!(conversionConfig.getEmptyCellHandlingMode() == CSVConversionConfig.CSVEmptyCellHandlingMode.SPARSE && (value == null
                                    || conversionConfig.getTreatAsEmptyValues().contains(value) || value.isEmpty()))) {
                                parseAttributes(progress, conversionConfig, conversionHandler, mappingMap.get(i),
                                        lineIndex, i, name, nextLine);
                            }
                        }

                        // Already sorted by time
                        conversionHandler.endEvent();
                        eventIndex++;

                    } catch (ParseException e) {
                        conversionHandler.errorDetected(lineIndex, nextLine, e);
                    }
                }

                // Close last trace
                conversionHandler.endTrace(currentCaseId);

            } catch (IOException e) {
                throw new CSVConversionException("Error converting the CSV file to XES", e);
            }
        } finally {
            if (sortedCsvInputStream != null) {
                try {
                    sortedCsvInputStream.close();
                } catch (Exception e) {
                    throw new CSVConversionException("Error closing the CSV file", e);
                }
            }
            if (sortedFile != null) {
                sortedFile.delete();
            }
        }

        long endConvertTime = System.currentTimeMillis();
        progress.log(String.format("Finished reading cases in %d seconds.", (endConvertTime - startCSVTime) / 1000));

        return new ConversionResult<R>() {

            public R getResult() {
                return conversionHandler.getResult();
            }

            public boolean hasConversionErrors() {
                return conversionHandler.hasConversionErrors();
            }

            public String getConversionErrors() {
                return conversionHandler.getConversionErrors();
            }
        };
    }

    private static double getFileSizeInBytes(CSVFile csvFile) throws IOException {
        return Files.size(csvFile.getFile());
    }

    private <R> void parseAttributes(ProgressListener progress, CSVConversionConfig conversionConfig,
            CSVConversionHandler<R> conversionHandler, CSVMapping csvMapping, int lineIndex, int columnIndex,
            String name, String[] line) throws CSVConversionException {

        String value = line[columnIndex];
        if (name == null) // TODO: Nicer would be to create names like "unknown-1", "unknown-2", etc. instead of skipping the attribute
        {
            return;
        }
        if (csvMapping.getDataType() == null) {
            conversionHandler.startAttribute(name, value);
        } else {
            try {
                switch (csvMapping.getDataType()) {
                    case BOOLEAN:
                        boolean boolVal;
                        if ("J".equalsIgnoreCase(value) || "Y".equalsIgnoreCase(value) || "T".equalsIgnoreCase(value)) {
                            boolVal = true;
                        } else if ("N".equalsIgnoreCase(value) || "F".equalsIgnoreCase(value)) {
                            boolVal = false;
                        } else {
                            boolVal = Boolean.valueOf(value);
                        }
                        conversionHandler.startAttribute(name, boolVal);
                        break;
                    case CONTINUOUS:
                        if (csvMapping.getFormat() != null) {
                            conversionHandler.startAttribute(name, (Double) csvMapping.getFormat().parseObject(value));
                        } else {
                            conversionHandler.startAttribute(name, Double.parseDouble(value));
                        }
                        break;
                    case DISCRETE:
                        if (csvMapping.getFormat() != null) {
                            conversionHandler.startAttribute(name, (Integer) csvMapping.getFormat().parseObject(value));
                        } else {
                            conversionHandler.startAttribute(name, Long.parseLong(value));
                        }
                        break;
                    case TIME:
                        conversionHandler.startAttribute(name, parseDate((DateFormat) csvMapping.getFormat(), value));
                        break;
                    case LITERAL:
                    default:
                        if (csvMapping.getFormat() != null) {
                            value = ((MessageFormat) csvMapping.getFormat()).format(ObjectArrays.concat(value, line),
                                    new StringBuffer(), null).toString();
                        }
                        conversionHandler.startAttribute(name, value);
                        break;
                }
            } catch (NumberFormatException e) {
                conversionHandler.errorDetected(lineIndex, value, e);
                conversionHandler.startAttribute(name, value);
            } catch (ParseException e) {
                conversionHandler.errorDetected(lineIndex, value, e);
                conversionHandler.startAttribute(name, value);
            }
        }
        conversionHandler.endAttribute();
    }

    /**
     * Concatenates multiple composite attributes to a String representation.
     *
     * @param columnIndex
     * @param line
     * @param compositeSeparator
     * @return the composite attributes concatenated or an empty String in case
     * no columns are selected
     *
    private static String readCompositeAttribute(int[] columnIndex, String[] line, String compositeSeparator) {
        if (columnIndex.length == 0) {
            return "";
        }
        int size = 0;
        for (int index : columnIndex) {
            String cell = line[index];
            size += (cell == null ? 0 : cell.length());
        }
        StringBuilder sb = new StringBuilder(size + columnIndex.length);
        for (int index : columnIndex) {
            String cell = line[index];
            if (cell != null) {
                sb.append(cell);
            }
            sb.append(compositeSeparator);
        }
        return sb.substring(0, sb.length() - 1);
    }

    private static Pattern INVALID_MS_PATTERN = Pattern.compile("(\\.[0-9]{3})[0-9]*");

    private static Date parseDate(DateFormat customDateFormat, String value) throws ParseException {

        if (value == null) {
            throw new ParseException("Could not parse NULL timestamp!", 0);
        }

        if (customDateFormat != null) {
            ParsePosition pos = new ParsePosition(0);
            Date date = customDateFormat.parse(value, pos);
            if (date != null) {
                return date;
            } else {
                String fixedValue = INVALID_MS_PATTERN.matcher(value).replaceFirst("$1");
                pos.setIndex(0);
                date = customDateFormat.parse(fixedValue, pos);
                if (date != null) {
                    return date;
                } else {
                    String pattern = "unkown";
                    if (customDateFormat instanceof SimpleDateFormat) {
                        pattern = ((SimpleDateFormat) customDateFormat).toPattern();
                    }
                    throw new ParseException("Could not parse " + value + " using pattern '" + pattern + "'",
                            pos.getErrorIndex());
                }
            }
        }

        throw new ParseException("Could not parse " + value, -1);
    }

}


final class CSVSorter {

    private static final class UncompressedCSVReaderWithoutHeader extends DataReader<String[]> {

        private static final int MAX_COLUMNS_FOR_ERROR_REPORTING = 32;
        private static final int MAX_FIELD_LENGTH_FOR_ERROR_REPORTING = 64;

        private final ICSVReader reader;
        private final int numColumns;

        private UncompressedCSVReaderWithoutHeader(CSVFile csvFile, CSVConfig importConfig, int numColumns)
                throws IOException {
            this.numColumns = numColumns;
            this.reader = csvFile.createReader(importConfig);
            // Skip header line
            this.reader.readNext();
        }

        public void close() throws IOException {
            reader.close();
        }

        public int estimateSizeInBytes(String[] val) {
            return estimateSize(val);
        }

        public String[] readNext() throws IOException {
            String[] val = reader.readNext();
            if (val != null && val.length != numColumns) {
                String offendingLine = safeToString(val);
                throw new IOException("Inconsistent number of fields in a row of the CSV file. Should be " + numColumns
                        + " according to the header, but read a line with " + val.length + " fields! Invalid line: "
                        + offendingLine);
            }
            return val;
        }

        private String safeToString(String[] valueArray) {
            if (valueArray == null) {
                return "NULL";
            } else if (valueArray.length == 0) {
                return "[]";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                for (int i = 0;; i++) {
                    String value = valueArray[i];
                    if (value != null) {
                        if (value.length() < MAX_FIELD_LENGTH_FOR_ERROR_REPORTING) {
                            sb.append(value);
                        } else {
                            sb.append(value.substring(0, MAX_FIELD_LENGTH_FOR_ERROR_REPORTING - 1));
                        }
                        if (i > MAX_COLUMNS_FOR_ERROR_REPORTING) {
                            return sb.append(String.format("[... omitted %s further columns]", valueArray.length - i))
                                    .toString();
                        }
                        if (i == valueArray.length - 1) {
                            return sb.append(']').toString();
                        }
                        sb.append(", ");
                    }
                }
            }
        }
    }

    private static final class CompressedCSVDataWriterFactory extends DataWriterFactory<String[]> {

        private final CSVConfig importConfig;
        private final CSVFile csvFile;

        private CompressedCSVDataWriterFactory(CSVFile csvFile, CSVConfig importConfig) {
            this.csvFile = csvFile;
            this.importConfig = importConfig;
        }

        public DataWriter<String[]> constructWriter(OutputStream os) throws IOException {
            final ICSVWriter writer = csvFile.getCSV().createWriter(new PLZFOutputStream(os), importConfig);
            // Write Header
            return new DataWriter<String[]>() {

                public void close() throws IOException {
                    writer.close();
                }

                public void writeEntry(String[] val) throws IOException {
                    writer.writeNext(val);
                }
            };
        }
    }

    private static final class CompressedCSVDataReaderFactory extends DataReaderFactory<String[]> {

        private final CSVConfig importConfig;
        private final CSVFile csvFile;

        private CompressedCSVDataReaderFactory(CSVFile csvFile, CSVConfig importConfig) {
            this.csvFile = csvFile;
            this.importConfig = importConfig;
        }

        public DataReader<String[]> constructReader(InputStream is) throws IOException {
            final ICSVReader reader = csvFile.getCSV().createReader(new LZFInputStream(is), importConfig);
            return new DataReader<String[]>() {

                public void close() throws IOException {
                    reader.close();
                }

                public int estimateSizeInBytes(String[] item) {
                    return estimateSize(item);
                }

                public String[] readNext() throws IOException {
                    return reader.readNext();
                }
            };
        }
    }

    private CSVSorter() {
    }

    /**
     * Sorts an {@link CSVFile} using only a configurable, limited amount of
     * memory.
     *
     * @param csvFile
     * @param rowComparator
     * @param importConfig
     * @param maxMemory
     * @param numOfColumnsInCSV
     * @param progress
     * @return a {@link File} containing the sorted CSV
     * @throws CSVSortException
     *
    public static File sortCSV(final CSVFile csvFile, final Comparator<String[]> rowComparator,
            final CSVConfig importConfig, final int maxMemory, final int numOfColumnsInCSV,
            final CSVConversion.ProgressListener progress) throws CSVSortException {

        // Create Sorter
        final CompressedCSVDataReaderFactory dataReaderFactory = new CompressedCSVDataReaderFactory(
                csvFile, importConfig);
        final CompressedCSVDataWriterFactory dataWriterFactory = new CompressedCSVDataWriterFactory(
                csvFile, importConfig);
        final IteratingSorter<String[]> sorter = new IteratingSorter<>(new SortConfig().withMaxMemoryUsage(
                maxMemory * 1024l * 1024l).withTempFileProvider(new TempFileProvider() {

                    public File provide() throws IOException {
                        return Files.createTempFile(csvFile.getFilename() + "-merge-sort", ".lzf").toFile();
                    }
                }), dataReaderFactory, dataWriterFactory, rowComparator);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<File> future = executorService.submit(new Callable<File>() {

            public File call() throws Exception {

                // Read uncompressed CSV
                DataReader<String[]> inputDataReader = new UncompressedCSVReaderWithoutHeader(csvFile, importConfig, numOfColumnsInCSV);
                try {
                    Iterator<String[]> result = sorter.sort(inputDataReader);

                    // Write sorted result to compressed file
                    if (result != null && result.hasNext()) {
                        File sortedCsvFile = Files.createTempFile(csvFile.getFilename() + "-sorted", ".lzf").toFile();
                        DataWriter<String[]> dataWriter = dataWriterFactory.constructWriter(new FileOutputStream(
                                sortedCsvFile));
                        try {
                            while (result.hasNext()) {
                                dataWriter.writeEntry(result.next());
                            }
                        } finally {
                            dataWriter.close();
                        }
                        return sortedCsvFile;
                    } else {
                        if (!result.hasNext()) {
                            throw new CSVSortException("Could not sort file! Input parser returned empty file.");
                        } else {
                            throw new CSVSortException("Could not sort file! Unkown error while sorting.");
                        }
                    }

                } finally {
                    sorter.close();
                }

            }
        });

        try {
            executorService.shutdown();
            int sortRound = -1;
            int preSortFiles = -1;
            while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                if (progress.getProgress().isCancelled()) {
                    progress.log("Cancelling sorting, this might take a while ...");
                    sorter.cancel(new RuntimeException("Cancelled"));
                    throw new CSVSortException("User cancelled sorting");
                }
                if (sorter.getPhase() == SortingState.Phase.PRE_SORTING) {
                    if (sorter.getSortRound() != sortRound) {
                        sortRound = sorter.getSortRound();
                        progress.log(MessageFormat.format("Pre-sorting finished segment {0} in memory ...",
                                sortRound + 1));
                    }
                    if (sorter.getNumberOfPreSortFiles() != preSortFiles) {
                        preSortFiles = sorter.getNumberOfPreSortFiles();
                        progress.log(MessageFormat.format("Pre-sorting finished segment {0} ...", preSortFiles + 1));
                    }
                } else if (sorter.getPhase() == SortingState.Phase.SORTING) {
                    if (sorter.getSortRound() != sortRound) {
                        sortRound = sorter.getSortRound();
                        progress.log(MessageFormat.format("Sorting finished round {0}/{1} ...", sortRound + 1,
                                sorter.getNumberOfSortRounds() + 1));
                    }
                }
            }
            return future.get();
        } catch (InterruptedException e) {
            progress.log("Cancelling sorting, this might take a while ...");
            sorter.cancel();
            throw new CSVSortException("Cancelled sorting", e);
        } catch (ExecutionException e) {
            throw new CSVSortException("Could not sort file.", e);
        }
    }

    private static int estimateSize(String[] item) {
        int size = 8 * ((item.length * 4 + 12) / 8);
        for (String s : item) {
            if (s != null) {
                size += 8 * ((((s.length()) * 4) + 45) / 8);
            }
        }
        return size;
    }

}
*/