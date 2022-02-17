package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.graphpanel;

import java.awt.Color;

/**
 *
 * @author Giovanni
 */
public class Edge {
    private final int edgeNumber;
    private final String edgeID;
    private final String fromID;
    private final String toID;
    private final boolean isDirect;
    private final Color color;

    public Edge(int edgeNumber, String edgeID, String fromID, String toID, boolean isDirect, Color color) {
        this.edgeNumber = edgeNumber;
        this.edgeID = edgeID;
        this.fromID = fromID;
        this.toID = toID;
        this.isDirect = isDirect;
        this.color = color;
    }

    public int getEdgeNumber() {
        return edgeNumber;
    }

    public String getEdgeID() {
        return edgeID;
    }

    public String getFromID() {
        return fromID;
    }

    public String getToID() {
        return toID;
    }

    public boolean isDirect() {
        return isDirect;
    }

    public Color getColor() {
        return color;
    }
}
