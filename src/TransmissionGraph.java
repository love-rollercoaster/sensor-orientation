import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

public class TransmissionGraph extends SimpleDirectedGraph<Sensor, SensorEdge> {
    private static final long serialVersionUID = 1L;

    private Double anglePhi;
    private Double range;

    public TransmissionGraph(Set<Sensor> vertices) {
        super(new SensorEdgeFactory());
        this.anglePhi = Sensor.GetAngle();
        this.range = Sensor.GetRange();

        for (Sensor vertex : vertices) {
            addVertex(vertex);
        }

        List<Set<Sensor>> connectedSets = (new ConnectivityInspector<Sensor, SensorEdge>(new ProximityGraph(vertices)))
                .connectedSets();

        for (Set<Sensor> connectedVertices : connectedSets) {
            orientAntennae(connectedVertices);
        }
    }

    private SimpleGraph<Sensor, SensorEdge> computeMST(Set<Sensor> vertices) {
        KruskalMinimumSpanningTree<Sensor, SensorEdge> mstResult = new KruskalMinimumSpanningTree<Sensor, SensorEdge>(
                new ProximityGraph(vertices));

        SimpleGraph<Sensor, SensorEdge> mstGraph = new SimpleGraph<Sensor, SensorEdge>(this.getEdgeFactory());

        for (Sensor vertex : vertices) {
            mstGraph.addVertex(vertex);
        }

        for (SensorEdge edge : mstResult.getEdgeSet()) {
            mstGraph.addEdge(edge.getSource(), edge.getDestination());
        }

        return mstGraph;
    }

    private void orientAntennae(Set<Sensor> vertices) {
        SimpleGraph<Sensor, SensorEdge> minSpanTree = computeMST(vertices);

        Set<SensorEdge> matching = new HashSet<SensorEdge>(); // Note that it's
                                                              // possible for a
                                                              // leaf to be in
                                                              // the matching
        Set<Sensor> leaves = new HashSet<Sensor>();
        Set<Sensor> verticesAdded = new HashSet<Sensor>();

        // Grab the first vertex in our set and use it to root the bfs
        // Make sure we have a vertex to work with
        if (vertices.iterator().hasNext()) {
            Sensor startVtx = vertices.iterator().next();
            BreadthFirstIterator<Sensor, SensorEdge> bfsIt = new BreadthFirstIterator<Sensor, SensorEdge>(minSpanTree,
                    startVtx);
            Sensor firstCoupled;

            if (bfsIt.hasNext()) {
                firstCoupled = bfsIt.next(); // This first call apparently gets
                                             // the startVtx
            }

            if (bfsIt.hasNext()) {

                firstCoupled = bfsIt.next();
                matching.add(minSpanTree.getEdge(startVtx, firstCoupled));
                verticesAdded.add(startVtx);
                verticesAdded.add(firstCoupled);

                // This while loop creates the matching
                while (bfsIt.hasNext()) {
                    Sensor temp = bfsIt.next();

                    // "if leaf node or edge between it and its parent is in matching M, continue"
                    // Note that if the degree of this vertex is 1 it's a leaf
                    // Also note that if the node is a source/target of an edge
                    // in the matching then the edge between it and its parent
                    // is in M
                    if (minSpanTree.degreeOf(temp) > 1) {

                        // Loop through the matching's edges and check for our
                        // target
                        boolean moveOn = false;

                        Set<SensorEdge> edgesOfTemp = minSpanTree.edgesOf(temp);

                        for (SensorEdge e : edgesOfTemp) {
                            if (matching.contains(e)) {
                                moveOn = true;
                            }
                        }

                        if (!moveOn) {
                            // pick an edge between temp and one of its children
                            // and add to M
                            Iterator<SensorEdge> neighbourIt = edgesOfTemp.iterator();

                            while (!moveOn && neighbourIt.hasNext()) {
                                SensorEdge neighbourEdge = neighbourIt.next();
                                // Note that temp is going to be one of source
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

                    } else {
                        if (minSpanTree.degreeOf(temp) == 1) {
                            if (!verticesAdded.contains(temp)) {
                                leaves.add(temp);
                            }
                        }
                    }
                }

                for (Sensor sensor : leaves) {
                    if (sensor == null)
                        continue;

                    // Connect it to it's parent. Because it's a leaf, it's
                    // parent is it's neighbour.
                    Set<SensorEdge> edgesOfS = minSpanTree.edgesOf(sensor);

                    // This should be an unnecessary check because the graph
                    // should be connected
                    if (edgesOfS.iterator().hasNext()) {
                        SensorEdge tempEdge = edgesOfS.iterator().next();
                        if (tempEdge.getSource().equals(sensor)) {
                            addEdge(sensor, tempEdge.getDestination());
                            double orientation = angleBetweenTwoSensors(tempEdge.getDestination(), sensor);
                            sensor.setOrientation(orientation);
                        } else if (tempEdge.getDestination().equals(sensor)) {
                            addEdge(sensor, tempEdge.getSource());
                            double orientation = angleBetweenTwoSensors(tempEdge.getSource(), sensor);
                            sensor.setOrientation(orientation);
                        }
                    }
                }

                for (SensorEdge edge : matching) {
                    if (edge == null)
                        return;

                    Sensor source = edge.getSource();
                    Sensor destination = edge.getDestination();
                    addEdge(source, destination);
                    addEdge(destination, source);
                    double angleFromSourceToDest = angleBetweenTwoSensors(destination, source);
                    double angleFromDestToSource = angleBetweenTwoSensors(source, destination);

                    // Orient them so that they see each other at the edge
                    // of their vision
                    source.setOrientation(angleFromSourceToDest + (anglePhi / 2));
                    destination.setOrientation(angleFromDestToSource + (anglePhi / 2));

                    // FIXME: This algorithm orients, then considers
                    // neighbours. It would be nice if we could consider
                    // neighbours
                    // and then orient.
                    for (Sensor neighbour : vertices) {

                        if (neighbour != null) {
                            if (!neighbour.equals(source) && !neighbour.equals(destination)) {
                                if (source.getPosition().distance(neighbour.getPosition()) <= range) {
                                    double sourceNeighbourAngle = angleBetweenTwoSensors(neighbour, source);
                                    double sourceOrient = source.getOrientation();
                                    // If adding and subtracting the angles
                                    // doesn't overlap the origin
                                    if (((sourceOrient + (anglePhi / 2)) <= (2 * Math.PI))
                                            && ((sourceOrient - (anglePhi / 2)) >= 0)) {
                                        if ((sourceNeighbourAngle <= (sourceOrient + (anglePhi / 2)))
                                                && (sourceNeighbourAngle >= (sourceOrient - (anglePhi / 2)))) {
                                            // Add the edge between them
                                            addEdge(source, neighbour);
                                        }
                                    } else {
                                        if ((sourceOrient + (anglePhi / 2)) > (2 * Math.PI)) {
                                            if ((sourceOrient - (anglePhi / 2)) < 0) {
                                                // Unlikely situation. In
                                                // any case, the sweep is
                                                // more than 2*PI so just
                                                // accept it.
                                                addEdge(source, neighbour);
                                            } else {
                                                if (((sourceNeighbourAngle >= (sourceOrient - (anglePhi / 2))) && (sourceNeighbourAngle <= (2 * Math.PI)))
                                                        || ((sourceNeighbourAngle >= 0) && (sourceNeighbourAngle <= ((sourceOrient + (anglePhi / 2)) - (2 * Math.PI))))) {
                                                    addEdge(source, neighbour);
                                                }
                                            }
                                        } else {
                                            if ((sourceOrient - (anglePhi / 2)) < 0) {
                                                if (((sourceNeighbourAngle <= (sourceOrient + (anglePhi / 2))) && (sourceNeighbourAngle >= 0))
                                                        || ((sourceNeighbourAngle >= ((2 * Math.PI) + (sourceOrient - (anglePhi / 2)))))) {
                                                    addEdge(source, neighbour);
                                                }
                                            }
                                        }
                                    }
                                }

                                if (destination.getPosition().distance(neighbour.getPosition()) <= range) {
                                    double destinationNeighbourAngle = angleBetweenTwoSensors(neighbour, destination);
                                    double destinationOrient = destination.getOrientation();
                                    // If adding and subtracting the angles
                                    // doesn't overlap the origin
                                    if (((destinationOrient + (anglePhi / 2)) <= (2 * Math.PI))
                                            && ((destinationOrient - (anglePhi / 2)) >= 0)) {
                                        if ((destinationNeighbourAngle <= (destination.getOrientation() + (anglePhi / 2)))
                                                && (destinationNeighbourAngle >= (destination.getOrientation() - (anglePhi / 2)))) {
                                            // Add the edge between them
                                            addEdge(destination, neighbour);
                                        }
                                    } else {
                                        if ((destinationOrient + (anglePhi / 2)) > (2 * Math.PI)) {
                                            if ((destinationOrient - (anglePhi / 2)) < 0) {
                                                // Unlikely situation. In
                                                // any case, the sweep is
                                                // more than 2*PI so just
                                                // accept it.
                                                addEdge(destination, neighbour);
                                            } else {
                                                if (((destinationNeighbourAngle >= (destinationOrient - (anglePhi / 2))) && (destinationNeighbourAngle <= (2 * Math.PI)))
                                                        || ((destinationNeighbourAngle >= 0) && (destinationNeighbourAngle <= ((destinationOrient + (anglePhi / 2)) - (2 * Math.PI))))) {
                                                    addEdge(destination, neighbour);
                                                }
                                            }
                                        } else {
                                            if ((destinationOrient - (anglePhi / 2)) < 0) {
                                                if (((destinationNeighbourAngle <= (destinationOrient + (anglePhi / 2))) && (destinationNeighbourAngle >= 0))
                                                        || ((destinationNeighbourAngle >= ((2 * Math.PI) + (destinationOrient - (anglePhi / 2)))))) {
                                                    addEdge(destination, neighbour);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param to
     *            the vertex we are measuring to
     * @param from
     *            the vertex we are measuring from
     * @return the angle between the two vertices expressed as a value between 0
     *         and 2pi
     */
    private double angleBetweenTwoSensors(Sensor to, Sensor from) {
        double heightDiff = to.getPosition().getY() - from.getPosition().getY();
        double strideDiff = to.getPosition().getX() - from.getPosition().getX();

        double oppositeOverAdjacent;
        if (heightDiff < 0) {
            oppositeOverAdjacent = strideDiff / heightDiff;
        } else {
            oppositeOverAdjacent = heightDiff / strideDiff;
        }

        double result = Math.atan(oppositeOverAdjacent);

        return correctAngleForQuandrant(heightDiff, strideDiff, result);
    }

    private double correctAngleForQuandrant(double heightDiff, double strideDiff, double angle) {
        boolean positiveY = heightDiff >= 0;
        boolean positiveX = strideDiff >= 0;

        if (positiveY) {

            // Quadrant 2
            if (!positiveX) {
                // The dest must be in quadrant 2 and result must be negative
                // This will give us an angle between pi/2 and pi
                angle += Math.PI;
            }
            // Quandrant 1
            else {

            }

        } else {

            // Quandrant 4 (3pi/2 and 2pi)
            if (positiveX) {
                // The dest must be in quadrant 4 and the result must be
                // negative
                // This will give us an angle between
                angle = (3 * Math.PI / 2) - angle;

                // Quadrant 3
            } else {
                // The dest must be in quadrant 3 and must be positive
                // This will give us an angle between pi and 3pi/2
                angle = Math.PI + (Math.PI / 2 - angle);
            }
        }
        return angle;
    }

}
