package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.manager;

import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.graphpanel.Edge;
import com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.graphpanel.Vertex;
import java.util.HashSet;

/**
 *
 * @author Giovanni
 */
public class GraphInformations {
    private final HashSet<Vertex> graph_verticies;
    private final HashSet<Edge> graph_edges;
    private final String note;

    public GraphInformations(HashSet<Vertex> graphVerticies, HashSet<Edge> graphVdges, String note) {
        this.graph_verticies = graphVerticies;
        this.graph_edges = graphVdges;
        this.note = note;
    }

    public HashSet<Vertex> getGraphVerticies() {
        return graph_verticies;
    }

    public HashSet<Edge> getGraphEdges() {
        return graph_edges;
    }

    public String getNote() {
        return note;
    }
}
