package algorithms;

import java.io.PrintStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import network.ProximityGraph;
import network.Sensor;
import network.SensorEdge;
import network.SensorEdgeFactory;
import network.TransmissionGraph;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import test.TestResult;


public class Algorithms {

    public static SimpleGraph<Sensor, SensorEdge> ComputeMinimumSpanningTree(Set<Sensor> vertices) {
        KruskalMinimumSpanningTree<Sensor, SensorEdge> mstResult = new KruskalMinimumSpanningTree<Sensor, SensorEdge>(
                new ProximityGraph(vertices));

        SimpleGraph<Sensor, SensorEdge> mstGraph = new SimpleGraph<Sensor, SensorEdge>(new SensorEdgeFactory());

        for (Sensor vertex : vertices) {
            mstGraph.addVertex(vertex);
        }

        for (SensorEdge edge : mstResult.getEdgeSet()) {
            mstGraph.addEdge(edge.getSource(), edge.getDestination());
        }

        return mstGraph;
    }

    public static Set<Sensor> ComputeLeaves(SimpleGraph<Sensor, SensorEdge> minimumSpanningTree, Sensor root) {
        Set<Sensor> leaves = new HashSet<Sensor>();
        for (Sensor sensor : minimumSpanningTree.vertexSet()) {
            boolean isLeaf = minimumSpanningTree.degreeOf(sensor) == 1 && !sensor.equals(root);
            if (isLeaf) {
                leaves.add(sensor);
            }
        }
        return leaves;
    }

    public static Set<SensorEdge> ComputeMatching(SimpleGraph<Sensor, SensorEdge> minimumSpanningTree, Sensor root) {
        Set<SensorEdge> matching = new HashSet<SensorEdge>();

        if (minimumSpanningTree.vertexSet().size() == 1) {
            return matching;
        }

        Set<Sensor> verticesAdded = new HashSet<Sensor>();
        BreadthFirstIterator<Sensor, SensorEdge> bfsIt = new BreadthFirstIterator<Sensor, SensorEdge>(
                minimumSpanningTree, root);

        if (bfsIt.hasNext()) {
            bfsIt.next(); // ignoring the root
        }

        Sensor firstCoupled = bfsIt.next();
        matching.add(minimumSpanningTree.getEdge(root, firstCoupled));
        verticesAdded.add(root);
        verticesAdded.add(firstCoupled);

        // This while loop creates the matching
        while (bfsIt.hasNext()) {
            Sensor sensor = bfsIt.next();

            // "if leaf node or edge between it and its parent is in matching M, continue"
            // Note that if the degree of this vertex is 1 it's a leaf
            // Also note that if the node is a source/target of an edge
            // in the matching then the edge between it and its parent
            // is in M
            if (minimumSpanningTree.degreeOf(sensor) <= 1) {
                continue;
            }

            Set<SensorEdge> edgesOfSensor = minimumSpanningTree.edgesOf(sensor);

            boolean moveOn = false;

            for (SensorEdge edge : edgesOfSensor) {
                if (matching.contains(edge)) {
                    moveOn = true;
                }
            }

            // pick an edge between sensor and one of its children
            // and add to M
            Iterator<SensorEdge> neighbourIt = edgesOfSensor.iterator();

            while (!moveOn && neighbourIt.hasNext()) {
                SensorEdge neighbourEdge = neighbourIt.next();

                // Note that sensor is going to be one of source
                // or dest
                Sensor nSource = neighbourEdge.getSource();
                Sensor nDest = neighbourEdge.getDestination();

                // This check is so we don't move back up the
                // tree
                if (!verticesAdded.contains(nSource) && !verticesAdded.contains(nDest)) {
                    verticesAdded.add(nSource);
                    verticesAdded.add(nDest);

                    matching.add(neighbourEdge);
                    moveOn = true;
                }
            }
        }

        return matching;
    }

    // FIXME: throws null pointer exception if ComputeLargestShortestPath returns null
    public static double ComputeNetworkDiameter(Graph<Sensor, SensorEdge> graph) {
        return ComputeLargestShortestPath(graph).getEdgeList().size();
    }

    // FIXME: Returns null!
    public static GraphPath<Sensor, SensorEdge> ComputeLargestShortestPath(Graph<Sensor, SensorEdge> graph) {
        List<GraphPath<Sensor, SensorEdge>> allShortestPaths = ComputeAllShortestPaths(graph);

        GraphPath<Sensor, SensorEdge> largestShortestPath = null;
        int largestShortestPathSize = -1;
        for (GraphPath<Sensor, SensorEdge> path : allShortestPaths) {
            int pathSize = path.getEdgeList().size();

            if (pathSize > largestShortestPathSize) {
                largestShortestPathSize = pathSize;
                largestShortestPath = path;
            }
        }
        return largestShortestPath;
    }

    public static List<GraphPath<Sensor, SensorEdge>> ComputeAllShortestPaths(Graph<Sensor, SensorEdge> graph) {
        List<GraphPath<Sensor, SensorEdge>> shortestPaths = new LinkedList<GraphPath<Sensor, SensorEdge>>();

        for (Sensor vertex : graph.vertexSet()) {
            FloydWarshallShortestPaths<Sensor, SensorEdge> shortestPathsAlgorithm = new FloydWarshallShortestPaths<Sensor, SensorEdge>(graph);
            shortestPaths.addAll(shortestPathsAlgorithm.getShortestPaths(vertex));
        }

        return shortestPaths;
    }


    public static double ComputeLengthOfRoute(GraphPath<Sensor, SensorEdge> path) {
        double result = 0;
        for (SensorEdge e : path.getEdgeList()) {
            result += e.getSource().getPosition().distance(e.getDestination().getPosition());
        }
        return result;
    }

    public static boolean IsStronglyConnected(TransmissionGraph graph) {
        StrongConnectivityInspector<Sensor, SensorEdge> connectivityInspector = new StrongConnectivityInspector<Sensor, SensorEdge>(
                graph);
        return connectivityInspector.isStronglyConnected();
    }

    public static Set<Set<Sensor>> MakeVerticesPowerSet(Set<Sensor> vertices) {

        Set<Set<Sensor>> powerSet = new HashSet<Set<Sensor>>();

        for (Sensor vertexOuter : vertices) {
            for (Sensor vertexInner : vertices) {
                if (vertexOuter.equals(vertexInner)) {
                    continue;
                }
                Set<Sensor> pair = new HashSet<Sensor>();
                pair.add(vertexOuter);
                pair.add(vertexInner);
                powerSet.add(pair);
            }
        }

        return powerSet;
    }
}
