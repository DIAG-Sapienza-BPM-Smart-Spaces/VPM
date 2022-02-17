package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.graphpanel;

import java.awt.Color;

/**
 *
 * @author Giovanni
 */
public class Vertex {
    private final String vertexID;
    private final int positionX;
    private final int positionY;
    private final int dimension;
    private final Color color;

    public Vertex(String vertexID, int positionX, int positionY, int dimension, Color color) {
        this.vertexID = vertexID;
        this.positionX = positionX;
        this.positionY = positionY;
        this.dimension = dimension;
        this.color = color;
    }

    public String getVertexID() {
        return vertexID;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getDimension() {
        return dimension;
    }

    public Color getColor() {
        return color;
    }
}
