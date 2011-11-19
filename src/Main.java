import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

import org.jgraph.*;
import org.jgraph.graph.*;

import org.jgrapht.*;
import org.jgrapht.demo.JGraphAdapterDemo;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;


// resolve ambiguity
import org.jgrapht.graph.DefaultEdge;


public class Main extends JApplet {
    private static final long serialVersionUID = 3256444702936019250L;

    private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");

    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

    private JGraphModelAdapter<String, DefaultEdge> jgraphAdapter;

    public static void main(String[] args) {
        JGraphAdapterDemo applet = new JGraphAdapterDemo();
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("JGraphT Adapter to JGraph Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void init() {
        // create a JGraphT graph
        ListenableGraph<String, DefaultEdge> graph = new ListenableDirectedMultigraph<String, DefaultEdge>(
                DefaultEdge.class);

        // create a visualization using JGraph, via an adapter
        jgraphAdapter = new JGraphModelAdapter<String, DefaultEdge>(graph);

        JGraph jgraph = new JGraph(jgraphAdapter);
        adjustDisplaySettings(jgraph);
        getContentPane().add(jgraph);
        resize(DEFAULT_SIZE);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add some sample data (graph manipulated via JGraphT)
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);

        graph.addEdge(v1, v2);
        graph.addEdge(v2, v3);
        graph.addEdge(v3, v1);
        graph.addEdge(v4, v3);

        // position vertices nicely within JGraph component
        positionVertexAt(v1, 130, 40);
        positionVertexAt(v2, 60, 200);
        positionVertexAt(v3, 310, 230);
        positionVertexAt(v4, 380, 70);

        // that's all there is to it!...
    }

    private void adjustDisplaySettings(JGraph jg) {
        jg.setPreferredSize(DEFAULT_SIZE);

        Color c = DEFAULT_BG_COLOR;
        String colorStr = null;

        try {
            colorStr = getParameter("bgcolor");
        } catch (Exception e) {
        }

        if (colorStr != null) {
            c = Color.decode(colorStr);
        }

        jg.setBackground(c);
    }

    @SuppressWarnings("unchecked")
    // FIXME hb 28-nov-05: See FIXME below
    private void positionVertexAt(Object vertex, int x, int y) {
        DefaultGraphCell cell = jgraphAdapter.getVertexCell(vertex);


        AttributeMap attr = cell.getAttributes();
        Rectangle2D bounds = GraphConstants.getBounds(attr);

        Rectangle2D newBounds = new Rectangle2D.Double(x, y, bounds.getWidth(), bounds.getHeight());

        GraphConstants.setBounds(attr, newBounds);

        // TODO: Clean up generics once JGraph goes generic
        AttributeMap cellAttr = new AttributeMap();
        cellAttr.put(cell, attr);
        jgraphAdapter.edit(cellAttr, null, null, null);
    }

    private static class ListenableDirectedMultigraph<V, E> extends DefaultListenableGraph<V, E> implements
            DirectedGraph<V, E> {
        private static final long serialVersionUID = 1L;

        ListenableDirectedMultigraph(Class<E> edgeClass) {
            super(new DirectedMultigraph<V, E>(edgeClass));
        }
    }
}

// End JGraphAdapterDemo.java
