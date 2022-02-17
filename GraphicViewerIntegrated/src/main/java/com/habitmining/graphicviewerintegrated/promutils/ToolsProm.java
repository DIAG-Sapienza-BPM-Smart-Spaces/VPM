/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.habitmining.graphicviewerintegrated.promutils;

import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.listeners.ButtonTrajectoryListener;
import com.habitmining.newgraphsimilarity.structs.Edge;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.apache.wicket.util.file.Files;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.plugin.PluginContext;
//import org.processmining.models.graphbased.directed.fuzzymodel.FMClusterNode;
import org.processmining.models.graphbased.directed.fuzzymodel.FMEdge;
import org.processmining.models.graphbased.directed.fuzzymodel.FMNode;
import org.processmining.models.graphbased.directed.fuzzymodel.MutableFuzzyGraph;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.MetricsRepository;
import org.processmining.plugins.fuzzymodel.FastTransformerVisualization;
import org.processmining.plugins.fuzzymodel.miner.FuzzyMinerPlugin;

/**
 *
 * @author daniele
 *
 * Class containing static methods implementing common functionalities
 */
public class ToolsProm {

    private static final String csvFold = "csv";
    private static final String csvExt = ".csv";

    private static final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ProcessMap numNodes=\"21\" nodeThreshold=\"0.5\" edgeThreshold=\"0.0\" discoVersion=\"1.9.9\">\n\t"
            + "<Layout width=\"00.0000\" height=\"0.000\"/>\n\t\t";

    //Metodo per ottenre XML simil-disco per integrare pipeline: vengono considerati nodi, archi e pesi dei nodi, il resto è dummy
    public static void fromMutableToXML(MutableFuzzyGraph mutable, File workingFolder, String filename) throws FileNotFoundException {
        System.out.println("Saving XML");
        //mappa per la gestione degli indici e dei nomi

        String xmlFinal = xmlHeader + getXMLString(mutable);

        PrintWriter pw = new PrintWriter(new File(workingFolder.getAbsolutePath() + File.separator + filename));
        pw.println(xmlFinal);
        pw.flush();
        pw.close();
    }

    private static String getXMLString(MutableFuzzyGraph mutable) {
        Map<String, String> namedIndex = new HashMap<String, String>();
        Set<FMNode> nodesSet = mutable.getNodes();

//        nodesSet.removeAll(mutable.getClusterNodes());

//        FMNode node = mutable.getClusterNodes().iterator().next();
//        
//        System.out.println(node.getGraph().equals(mutable));
        String res = "<Nodes size=\"" + nodesSet.size() + "\">\n\t";

        int i = 0;
        for (FMNode currentFM : nodesSet) {
            String name = currentFM.getElementName();

            String index = namedIndex.get(name);
            if (index == null) {
                index = "" + i;
                namedIndex.put(name, index);
                i = i + 1;
            }
            String nodeInfo
                    = "<Node index=\"" + index + "\" activity=\"" + name.replace('<', ' ').replace('>', ' ').trim() + "\">\n\t\t"
                    + "<Frequency total=\"" + Math.ceil(currentFM.getSignificance() * 1000) + "\" case=\"220\" start=\"28\" end=\"27\" maxRepetitions=\"85\"/>\n\t\t"
                    + "<Duration total=\"1218549000\" min=\"2000\" max=\"31686000\" mean=\"181818\" median=\"6000\"/>\n\t\t"
                    + "<Layout x=\"0.4395829\" y=\"0.2096912\" width=\"0.11349822\" height=\"0.040266167\"/>\n\t"
                    + "</Node>\n\t";
            res += nodeInfo;
        }
        res += "</Nodes>\n";

        Set<FMEdge<? extends FMNode, ? extends FMNode>> edgesSet = mutable.getEdges();

        res += "<Edges size=\"" + edgesSet.size() + "\">\n\t";

        for (FMEdge<? extends FMNode, ? extends FMNode> e : edgesSet) {
            String from = e.getSource().getElementName();
            String to = e.getTarget().getElementName();

            String idxFrom = namedIndex.get(from);
            String idxTo = namedIndex.get(to);

            if (idxFrom == null || idxTo == null) {
                continue;
            }
            String edgInfo
                    = "<Edge sourceIndex=\"" + idxFrom + "\" targetIndex=\"" + idxTo + "\" type=\"observed\">\n\t\t"
                    + "<Frequency total=\"" + Math.ceil(e.getSignificance() * 1000) + "\" case=\"140\" maxRepetitions=\"8\"/>\n\t\t"
                    + "<Duration total=\"0\" min=\"0\" max=\"0\" mean=\"0\" median=\"0\"/>\n\t\t"
                    + "<Layout curve=\"0.35223225,0.4550418,0.35516664,0.4383211,0.35956824,0.41460502,0.3667994,0.39327762,0.39299938,0.3270773,0.44099772,0.27725643,0.47159925,0.2511517\" labelX=\"0.41479775\" labelY=\"0.35301143\"/>\n\t"
                    + "</Edge>\n";
            res += edgInfo;
        }

        res += "</Edges>\n"
                + "</ProcessMap>";
        return res;
    }

    public static void appendInFile(String filename, String text) {

        try {
            File f = new File(filename);
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw, true);

            pw.println(text);

            pw.close();

        } catch (IOException ex) {
            Logger.getLogger(ToolsProm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static long writeSegment(String fileName, int id, long timestamp, LinkedHashMap<Edge, Double> segment) {
        long time = timestamp;
        File f = new File(csvFold);
        f.mkdirs();
        System.out.println(fileName);
        try {
            f = new File(csvFold + File.separator + fileName + csvExt);
            PrintWriter pw = new PrintWriter(new FileWriter(f, true));
//            System.out.println(f.exists());
            if (id == 0) { 
//                System.out.println("Here");
//                f.createNewFile();
                pw.println("case,timein,timeend,event");
                pw.flush();
            }
            String node = "";

            for (Edge e : segment.keySet()) {
                if (node.equals("")) {
                    node = e.getFrom();
                }
                pw.println(id + "," + toFormattedDate(new Date(time)) + ","
                        + toFormattedDate(new Date(time += 10000)) + "," + node);

                node = e.getTo();
            }
            pw.println(id + "," + toFormattedDate(new Date(time)) + ","
                    + toFormattedDate(new Date(time += 20000)) + "," + node);
            pw.flush();
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(ToolsProm.class.getName()).log(Level.SEVERE, null, ex);
        }

        return time;
    }

    public static void visualizeHabits(String sourceFile) {
        
        com.habitmining.graphicviewerintegrated.promutils.ToolsProm.appendInFile("timeReport.txt", "Start Showing All Habits Complete: " + System.currentTimeMillis());

        
        System.out.println("Extractor");
        
        File foldOut = new File(csvFold);
        
        /*File[] elems = foldOut.listFiles();
        System.out.println(elems);
        for (File elem : elems) {
            Files.remove(elem);
        }
        Files.remove(foldOut);
        */
        foldOut.mkdirs();
        
        HabitsExtractor extractor = new HabitsExtractor(sourceFile);
        extractor.extract();

        

        for (File f : foldOut.listFiles()) {

            PluginContext pc = new CustomProMPluginContext();
            FuzzyMinerPlugin fuzzyMiner = new FuzzyMinerPlugin();
            XEventClassifier classifier = new XEventLifeTransClassifier();

            XLog csvL = ButtonTrajectoryListener.convertCSV2XES(f.getAbsolutePath());
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
            MetricsRepository fuzzyModelMini = fuzzyMiner.mineDefault(pc, filtered_log_mini);
            javax.swing.JComponent compMini = ftvMini.visualize(pc, fuzzyModelMini);
            javax.swing.JFrame frameMini = new javax.swing.JFrame();
            frameMini.add(compMini);
            frameMini.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frameMini.setSize(1000, 800);
            frameMini.setVisible(true);
            frameMini.setTitle(f.getName().split("\\.")[0]);
        }
        com.habitmining.graphicviewerintegrated.promutils.ToolsProm.appendInFile("timeReport.txt", "End Showing All Habits Complete: " + System.currentTimeMillis());

    }

    private static String toFormattedDate(Date date) {
        String res = "";
        String temp = date.toString();
        String[] elems = temp.split(" ");
        String time = elems[3];
        String month = "";
        switch (elems[1]) {
            case "Jan":
                month = "01";
                break;
            case "Feb":
                month = "02";
                break;
            case "Mar":
                month = "03";
                break;
            case "Apr":
                month = "04";
                break;
            case "May":
                month = "05";
                break;
            case "Jun":
                month = "06";
                break;
            case "Jul":
                month = "07";
                break;
            case "Aug":
                month = "08";
                break;
            case "Sep":
                month = "09";
                break;
            case "Oct":
                month = "10";
                break;
            case "Nov":
                month = "11";
                break;
            case "Dec":
                month = "12";
                break;
        }
        res = elems[5] + "/" + month + "/" + elems[2] + " " + time;
        return res;
    }

    public static double[][] computeClosure(Map<Edge, Double> graph) {
        // WORKING ABOUT NODES

        Set<String> nodeSet = ToolsProm.fullfillNodes(graph.keySet());
        int maximum = 0;
        for (String node : nodeSet) {
            Integer i = new Integer(node);
            if (i > maximum) {
                maximum = i;
            }
        }
        maximum = maximum + 1;
        // Got graph nodes
//		double[][] res = new double[nodeSet.size()][nodeSet.size()];
        double[][] res = new double[maximum][maximum];
        for (Edge e : graph.keySet()) {
            String currentRoot = e.getFrom();
            if (!nodeSet.contains(currentRoot)) // if the node is not into the nodeSet, it has been already
            // processed
            {
                continue;
            } else {

                nodeSet.remove(currentRoot); //update nodeSet

                Set<String> alreadyVisited = new HashSet<String>();//to avoid infinite cycles
                alreadyVisited.add(currentRoot);

                Set<String> setNodesDest = new HashSet<String>();
                double minimum = Double.MAX_VALUE;
                for (Edge eTemp : graph.keySet()) {
                    if (eTemp.getFrom().equals(currentRoot)) {
                        setNodesDest.add(eTemp.getTo());
                        if (graph.get(eTemp) < minimum) {
                            minimum = graph.get(eTemp);
                        }
                    }
                    while (!setNodesDest.isEmpty()) {
                        Set<String> setTemp = new HashSet<String>(setNodesDest);
                        for (String n : setNodesDest) {
                            for (Edge eTemp2 : graph.keySet()) {
                                if (eTemp2.getFrom().equals(n) && !alreadyVisited.contains(eTemp2.getTo())
                                        && !graph.containsKey(new Edge(currentRoot, eTemp2.getTo()))) {
                                    if (graph.get(eTemp2) < minimum) {
                                        minimum = graph.get(eTemp2);
                                    }
                                    int i = new Integer(currentRoot), j = new Integer(eTemp2.getTo());
                                    res[i][j] = minimum;
                                    setTemp.add(eTemp2.getTo());
                                }
                            }
                            alreadyVisited.add(n);
                        }
                        setTemp.removeAll(alreadyVisited);
                        setNodesDest = setTemp;
                    }
                }
            }
        }
        return res;
    }

    public static Set<String> fullfillNodes(Set<Edge> edges) {
        Set<String> res = new HashSet<String>();
        // doppioni non inseribili
        for (Edge e : edges) {
            res.add(e.getFrom());
            res.add(e.getTo());
        }
        return res;
    }
}
