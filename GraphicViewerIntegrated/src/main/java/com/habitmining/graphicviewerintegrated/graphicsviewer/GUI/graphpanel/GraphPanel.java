package com.habitmining.graphicviewerintegrated.graphicsviewer.GUI.graphpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JComponent;



public class GraphPanel extends JPanel {
    private final static int MIN_GRAPH_WIDTH = 500;
    private final static int MIN_GRAPH_HEIGHT = 500;
    private final static int MAX_GRAPH_WIDTH = 900;
    private final static int MAX_GRAPH_HEIGHT = 800;

    private final double horizontal_scale;
    private final double vertical_scale;
    
    private final HashMap<String, Vertex> verticiesMap;
    private final HashMap<String, Edge> edgesMap;
    
    private final VisualizationViewer<String, String> visualization_viewer;
    private final VisualizationImageServer<String, String> visualization_image_server;
    
    
    public GraphPanel(ImageIcon backgroundImage) {
        // set the data structures to draw the graph
        verticiesMap = new HashMap<>();
        edgesMap = new HashMap<>();
        
        // create the scaled dimensions 
        int imageWidth = backgroundImage.getIconWidth();
        int imageHeight = backgroundImage.getIconHeight();
 
        Dimension scaledImageDimension = scaleDimension(imageWidth, imageHeight);

        int scaledImageWidth = scaledImageDimension.width;
        int scaledImageHeight = scaledImageDimension.height;

        // set the scale to draw the sensors
        horizontal_scale = scaledImageDimension.getWidth() / imageWidth;
        vertical_scale = scaledImageDimension.getHeight() / imageHeight;

        // resize the image 
        backgroundImage = new ImageIcon( 
                backgroundImage
                        .getImage()
                        .getScaledInstance(scaledImageWidth, scaledImageHeight, Image.SCALE_DEFAULT)
        );

        
        /*
         * Create the layout of the graph
         * from the API,
         * layouts are algorithms for assigning 2D coordinates (typically used for graph visualizations) to vertices.
         */
        Layout<String, String> layout = new StaticLayout<>( new DirectedSparseMultigraph<>(), new SensorTransformer() );    
        layout.setSize( new Dimension(scaledImageWidth, scaledImageHeight) );

        /* 
         * VisualizationViewer
         * it's used to create and visualize the graph
         */
        visualization_viewer =  new VisualizationViewer<>( layout, new Dimension(scaledImageWidth, scaledImageHeight) );
        initializeVisualizationServer(visualization_viewer, backgroundImage);

        /* 
         * VisualizationImageServer
         * it's configured in the same way of the VisualizationViewer
         * it's used to create an image of the graph
         */
        visualization_image_server = new VisualizationImageServer<>( layout, new Dimension(scaledImageWidth, scaledImageHeight) );
        initializeVisualizationServer(visualization_image_server, backgroundImage);
        
        // add the visualization viewer to the panel
        setLayout(new BorderLayout());
        add(visualization_viewer, BorderLayout.CENTER);
    }
    
    
    // it scales the dimension trying to respect the aspect ratio
    private Dimension scaleDimension(int width, int height) {
        double scale; // the scale to resize the image
        
        /*
         * width or/and height does not respect the minimum values
         * set the scale to increase the dimensions
        */
        if( width < MIN_GRAPH_WIDTH || height < MIN_GRAPH_HEIGHT ) {
            double scaleX = (double)MIN_GRAPH_WIDTH / width;
            double scaleY = (double)MIN_GRAPH_HEIGHT / height;
            
            scale = scaleX;
            if( scaleY > scaleX ) scale = scaleY; // sets the greater scale (increase the size)

        /*
         * width or (and) height does not respect the maximum values
         * set the scale to decrease the dimensions
        */
        } else if( width > MAX_GRAPH_WIDTH || height > MAX_GRAPH_HEIGHT ) {
            double scaleX = (double)MAX_GRAPH_WIDTH / width;
            double scaleY = (double)MAX_GRAPH_HEIGHT / height;
            
            scale = scaleX;
            if( scaleY < scaleX ) scale = scaleY; // sets the smaller scale (decrease the size)

        /*
         * no modification required
         * width and height are within the minimum and maximum values
        */
        } else return new Dimension(width, height);
        

        // scales the dimensions (these operations respect the aspect ratio)
        int newWidth = (int) ( width * scale );
        int newHeight = (int) ( height * scale );
        
        
        /*
         * modify the new dimensions to respect the min and max values
         * after the definition of a scale one of the two values can fall outside the min or max value
         * (any of these modification change the aspect ratio)
         * for example:
         * width < MIN_GRAPH_WIDTH and height > MIN_GRAPH_HEIGHT
         * or
         * width < MIN_GRAPH_WIDTH and newHeight > MIN_GRAPH_HEIGHT
        */ 
        if( newWidth < MIN_GRAPH_WIDTH) newWidth = MIN_GRAPH_WIDTH;
        else if( newWidth > MAX_GRAPH_WIDTH ) newWidth = MAX_GRAPH_WIDTH;
        
        if( newHeight < MIN_GRAPH_HEIGHT) newHeight = MIN_GRAPH_HEIGHT;
        else if( newHeight > MAX_GRAPH_HEIGHT ) newHeight = MAX_GRAPH_HEIGHT;

        
        return new Dimension(newWidth, newHeight);
    }
    
    
    private void initializeVisualizationServer(BasicVisualizationServer<String, String> visualizationServer, ImageIcon backgroundImage) {
        // add the background image
        visualizationServer.addPreRenderPaintable(new VisualizationViewer.Paintable() {
            @Override
            public void paint(Graphics g) {
                g.drawImage(backgroundImage.getImage(), 0, 0,
                        backgroundImage.getIconWidth(), backgroundImage.getIconHeight(), visualizationServer);
            }

            @Override
            public boolean useTransform() { 
                return false;
            }
            
        });

        // add the renderer to draw the vertices
        Renderer<String, String> render =  visualizationServer.getRenderer();
        render.setVertexRenderer( new SensorRender() );
        
        // get the render context to draw edges and edges label
        RenderContext<String, String> renderContext =  visualizationServer.getRenderContext();
        
        // initialize the edge shape and color
        renderContext.setArrowFillPaintTransformer(new EdgeColorTransformer());
        renderContext.setArrowDrawPaintTransformer(new EdgeColorTransformer());
        renderContext.setEdgeDrawPaintTransformer(new EdgeColorTransformer());

        // set the edge shape type
        renderContext.setEdgeShapeTransformer(new EdgeShape.QuadCurve<>()); // edge shape type
        //renderContext.setEdgeShapeTransformer(new EdgeShape.Line<>()); // edge shape type
        //renderContext.setEdgeShapeTransformer(new EdgeShape.CubicCurve<>()); // edge shape type

        // initialize the edge label
        renderContext.setEdgeLabelRenderer(new PathLabelRender());
        renderContext.setEdgeLabelClosenessTransformer(new ConstantDirectionalEdgeValueTransformer<>(.5, .5));
        renderContext.setEdgeLabelTransformer(new Transformer<String, String>() {
            @Override
            public String transform(String pathID) {
                return pathID;
            }
        });
    }
    
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(visualization_viewer.getGraphLayout().getSize());
    }
    


    /*
     * creates and displays the graph:
     * does not draw hidden sensors
     * combines edges whose vertices are hidden sensors
     */
    public void drawGraph(HashSet<Vertex> vertices, HashSet<Edge> edges) {
        Graph<String, String> graph = new DirectedSparseMultigraph<>(); // create a new graph
        
        // insert the sensors
        verticiesMap.clear();
        for(Vertex v : vertices) {
            String vertexID = v.getVertexID();
            verticiesMap.put(vertexID, v); // add the vertex into the verticies map
            graph.addVertex(vertexID); // add the vertex into the graph
        }

        // insert the paths
        edgesMap.clear();
        for(Edge e : edges) {
            String edgeID = e.getEdgeID();
            String fromID = e.getFromID();
            String toID = e.getToID();
            EdgeType type;
            
            if( e.isDirect() ) type = EdgeType.DIRECTED;
            else type = EdgeType.UNDIRECTED;
            
            edgesMap.put(e.getEdgeID(), e); // add the edges into the verticies map
            graph.addEdge(edgeID, fromID, toID, type); // add the edge into the graph
        }
        
        // redraw the graph
        visualization_viewer.getGraphLayout().setGraph(graph);
        visualization_viewer.repaint();
    }
    

        
    /*
     * Generate a buffered image of the current graph
     */
    public BufferedImage printGraph() {
        Dimension layoutDimension = visualization_image_server.getGraphLayout().getSize();
        BufferedImage image = (BufferedImage) visualization_image_server.getImage(
                new Point2D.Double(layoutDimension.getWidth() / 2, layoutDimension.getHeight() / 2),
                new Dimension(layoutDimension)
        );
        return image;
    }



    
    
    /*
     * transform the sensor (using its id) in a point (in 2d) of the graph 
     * (used to draw the edge vertices)
     */
    private class SensorTransformer implements Transformer<String, Point2D> {
        @Override
        public Point2D transform(String sensorID) {
            // get the sensor (represented as Vertex Object)
            Vertex v = verticiesMap.get(sensorID);
            
            double scaledPositionX = v.getPositionX() * horizontal_scale;
            double scaledPositionY = v.getPositionY() * vertical_scale;

            return new Point2D.Double( scaledPositionX, scaledPositionY );
        }
    }
    
    
    /*
     * draw the sensor
     */
    private class SensorRender implements Renderer.Vertex<String, String> {
        @Override
        public void paintVertex(RenderContext<String, String> rc, Layout<String, String> layout, String sensorID) {                
            // get the sensor (represented as Vertex Object)
            Vertex v = verticiesMap.get(sensorID);

            Color sensorColor = v.getColor();
            int sensorDimension = v.getDimension();
            int positionX = (int) (v.getPositionX() * horizontal_scale - (sensorDimension / 2));
            int positionY = (int) (v.getPositionY() * vertical_scale - (sensorDimension / 2));
            
            GraphicsDecorator graphicsContext = rc.getGraphicsContext();
            graphicsContext.setPaint(sensorColor);
            graphicsContext.fill(new Ellipse2D.Double(positionX, positionY, sensorDimension, sensorDimension));
        }
    }

    
    /*
     * transform the edge id in the color used to draw the edge
     */
    private class EdgeColorTransformer implements Transformer<String, Paint> {
        @Override
        public Paint transform(String pathID) {
            return edgesMap.get(pathID).getColor();
        }
    }
    
    
    /*
     * draw the label of the edge
     */ 
    private class PathLabelRender extends DefaultEdgeLabelRenderer {
        public PathLabelRender() {
            super(Color.BLACK, false);
        }
        
        @Override
        public <String> Component getEdgeLabelRendererComponent(JComponent vv, Object value, Font font, boolean isSelected, String edgeID) {
            super.getEdgeLabelRendererComponent(vv, value, font, isSelected, edgeID);
            
            setForeground( edgesMap.get(edgeID).getColor() );
            
            return this;
        }
    };
}