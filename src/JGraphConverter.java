import java.awt.Color;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;


public class JGraphConverter {

    public JGraphModelAdapter<Sensor, SensorEdge> convertProximityGraphToJGraph(ProximityGraph proximityGraph) {
        AttributeMap vertexAttributes = createDefaultVertexAttributes();
        AttributeMap edgeAttributes = createDefaultEdgeAttributes(false);

        return new JGraphModelAdapter<Sensor, SensorEdge>(proximityGraph, vertexAttributes, edgeAttributes);
    }

    private AttributeMap createDefaultEdgeAttributes(boolean directedGraph) {
        AttributeMap map = new AttributeMap();

        if (directedGraph) {
            GraphConstants.setLineEnd(map, GraphConstants.ARROW_TECHNICAL);
            GraphConstants.setEndFill(map, true);
            GraphConstants.setEndSize(map, 10);
        }

        GraphConstants.setForeground(map, ColorTheme.Black);
        GraphConstants.setFont(map, GraphConstants.DEFAULTFONT);
        GraphConstants.setLineColor(map, ColorTheme.White);
        GraphConstants.setEditable(map, false);
        GraphConstants.setSelectable(map, false);
        GraphConstants.setLabelAlongEdge(map, false);
        GraphConstants.setLabelPosition(map, new Point2D.Double(-1000.0, -1000.0));


        return map;
    }

    private AttributeMap createDefaultVertexAttributes() {
        AttributeMap map = new AttributeMap();

        GraphConstants.setForeground(map, ColorTheme.Black);
        GraphConstants.setLineColor(map, ColorTheme.White);
        GraphConstants.setBackground(map, ColorTheme.White);
        GraphConstants.setBorder(map, BorderFactory.createLineBorder(Color.black));
        GraphConstants.setEditable(map, false);
        GraphConstants.setFont(map, GraphConstants.DEFAULTFONT);
        GraphConstants.setOpaque(map, true);
        GraphConstants.setResize(map, false);
        GraphConstants.setSizeable(map, false);

        return map;
    }

}
