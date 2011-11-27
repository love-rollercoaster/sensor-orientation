import java.awt.Color;
import java.awt.geom.Point2D;
//import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;



public class JGraphConverter {

    public JGraphModelAdapter<Sensor, DefaultEdge> convertProximityGraphToJGraph(ProximityGraph proximityGraph) {
        ListenableGraph<Sensor, DefaultEdge> graph = convertProximityGraphToJGraphT(proximityGraph);

        AttributeMap vertexAttributes = createDefaultVertexAttributes();
        AttributeMap edgeAttributes = createDefaultEdgeAttributes(graph instanceof DirectedGraph);

        return new JGraphModelAdapter<Sensor, DefaultEdge>(graph, vertexAttributes, edgeAttributes);
    }

    public ListenableGraph<Sensor, DefaultEdge> convertProximityGraphToJGraphT(ProximityGraph graph) {
        ListenableGraph<Sensor, DefaultEdge> jgrapht = new ListenableUndirectedGraph<Sensor, DefaultEdge>(
                DefaultEdge.class);

        updateJGraphTFromProximityGraph(jgrapht, graph);

        return jgrapht;
    }

    public void updateJGraphTFromProximityGraph(ListenableGraph<Sensor, DefaultEdge> jgrapht, ProximityGraph graph) {

        for (Sensor sensor : graph.vertexSet()) {
            jgrapht.addVertex(sensor);
        }

        for (SensorEdge edge : graph.edgeSet()) {
            jgrapht.addEdge(edge.getSource(), edge.getDestination());
        }

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

        // GraphConstants.setSelectable(map, false);

        GraphConstants.setEditable(map, false);
        GraphConstants.setFont(map, GraphConstants.DEFAULTFONT);
        GraphConstants.setOpaque(map, true);
        GraphConstants.setResize(map, false);
        GraphConstants.setSizeable(map, false);

        return map;
    }

}
