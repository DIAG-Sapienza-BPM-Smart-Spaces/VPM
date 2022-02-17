/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.habitmining.graphicviewerintegrated.promutils;

import com.habitmining.newgraphsimilarity.structs.ActivityModelBuilder;
import com.habitmining.newgraphsimilarity.structs.Edge;
import com.habitmining.newgraphsimilarity.tools.Jaccard;
import com.habitmining.newgraphsimilarity.tools.KruskalMST;
import com.habitmining.newgraphsimilarity.tools.Utilities;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author daniele
 */
public class HabitsExtractor {

    public static String WORKING_DIRECTORY = "temp";

    private static String MODELS_PATH = "xml"+File.separator+"all";

    // Mappa che contiene archi + occorrenze (peso) del grafo1
    Map<Edge, Double> graph1 = new HashMap<Edge, Double>();

    // Mappa che contiene traduzioni nodi (index-nome) grafo1
    Map<String, String> nodesIndexName1 = new HashMap<String, String>();
    
    // Mappa che contiene traduzioni nodi (nome-index) grafo1
    Map<String, String> nodesNameIndex1 = new HashMap<String, String>();
    
    // Nodi e pesi del grafo1
    Map<String, Double> nodesW1 = new HashMap<String, Double>();
    
    // Mappa che contiene traduzioni nodi (index-indice) del grafo2 SENZA CORRISPONDENZE con grafo1
    Map<String, String> translateNodesMap = new HashMap<String, String>();

    Map<String, Map<Edge, Double>> activitiesModels = new HashMap<>();

    public HabitsExtractor(String filename) {
        //filename contains the complete path to the exported model
        File graph = new File(filename);
        System.out.println("File esiste: "+ graph.exists());
        Document xml1 = Utilities.getDocument(graph);
        Utilities.fullFillNodes(xml1, nodesIndexName1, nodesNameIndex1, null, nodesW1, translateNodesMap);
        System.out.println("Nodes ok");

        Utilities.fullFillEdges(xml1, graph1, null);
        System.out.println("Edges ok");

        System.out.println(filename);
        System.out.println(MODELS_PATH);
       File source = new File(MODELS_PATH);
        
        /////File[] children = source.listFiles();
        /////for (File file : children) {
            // Mappa che contiene archi + occorrenze (peso) del grafo2
        Map<Edge, Double> graph2 = new HashMap<Edge, Double>();
            
            // Mappa che contiene traduzioni nodi (index-nome) grafo2
        Map<String, String> nodesIndexName2 = new HashMap<String, String>();
            
            // Mappa che contiene traduzioni nodi (nome-indice) grafo2
        Map<String, String> nodesNameIndex2 = new HashMap<String, String>();
            
            // Nodi e pesi del grafo2
        Map<String, Double> nodesW2 = new HashMap<String, Double>();

        Document xml2 = Utilities.getDocument(graph); ///// file
        Utilities.fullFillNodes(xml2, nodesIndexName2, nodesNameIndex2, nodesNameIndex1, nodesW2, translateNodesMap);
        String key = graph.getName().split(" ")[0]; /////file
        activitiesModels.put(key, graph2);

        
    }

    public Map<String, Map<Edge, Double>> extract() {

        KruskalMST kmst = new KruskalMST(graph1, nodesIndexName1);

        kmst.computeKruskalMST();

        Map<String, Map<Edge, Double>> multiSegmentsMap = new HashMap<String, Map<Edge, Double>>();

        List<Map<Edge, Double>> segments = kmst.getSegments();
        System.out.println("Sequenze: " + segments.size());
        int id = 0;
        long ts = System.currentTimeMillis();
//        ActivityModelBuilder a = new ActivityModelBuilder();
        for (Map<Edge, Double> originalSeg : segments) {

            Map<Edge, Double> seg = Utilities.translateMap(originalSeg, nodesIndexName1);

//            if(activitiesModels == null);
            String res = "";
            Double max = 0.0;
            for (String cl : activitiesModels.keySet()) {

                Map<Edge, Double> currentModel = activitiesModels.get(cl);
                Double val = Jaccard.computeTopologicalJaccard(seg, currentModel);

                if (val >= max) {
                    res = cl;
                    max = val;
                }

            }
//            if(multiSegmentsMap.containsKey(res)){
//                multiSegmentsMap.get(res).add(seg);
//            }else{
//                List<Map<Edge, Double>> list = new ArrayList<Map<Edge, Double>>();
//                list.add(seg);
//                multiSegmentsMap.put(res, list);
//            }
            
            ts = ToolsProm.writeSegment(res, id, ts,(LinkedHashMap<Edge, Double>)seg);
            id++;
            if (multiSegmentsMap.containsKey(res)) {
                LinkedHashMap<Edge, Double> m = (LinkedHashMap<Edge, Double>) multiSegmentsMap.get(res);
                for (Edge e : seg.keySet()) {
                    if (m.containsKey(e)) {
                        Double d1 = seg.get(e);
                        Double d2 = m.get(e);
                        Double dTot = d1 + d2;
                        m.put(e, dTot);
                    } else {
                        m.put(e, seg.get(e));
                    }
                }
                multiSegmentsMap.put(res, m);

            } else {
                multiSegmentsMap.put(res, seg);
            }
        }
        return multiSegmentsMap;
    }

}
