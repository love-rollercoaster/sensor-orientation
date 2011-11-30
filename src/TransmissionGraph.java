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
            orientAntenna(connectedVertices);
        }
    }

    private void orientAntenna(Set<Sensor> vertices) {
     // Grab the first vertex in our set and use it to root the bfs
        Sensor root = vertices.iterator().next();
        SimpleGraph<Sensor, SensorEdge> minimumSpanningTree = Algorithms.ComputeMinimumSpanningTree(vertices);
        Set<SensorEdge> matching = Algorithms.ComputeMatching(minimumSpanningTree, root);
        Set<Sensor> leaves = Algorithms.ComputeLeaves(minimumSpanningTree, root);

        orientMinimumSpanningTreeLeaves(minimumSpanningTree, leaves);
        orientMatching(vertices, matching);
    }

    private void orientMatching(Set<Sensor> vertices, Set<SensorEdge> matching) {
        for (SensorEdge edge : matching) {

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

            connectMatchingCouplingToNeighbors(vertices, source, destination);
        }
    }

    private void connectMatchingCouplingToNeighbors(Set<Sensor> vertices, Sensor source, Sensor destination) {
        // FIXME: This algorithm orients, then considers
        // neighbours. It would be nice if we could consider
        // neighbours
        // and then orient.
        for (Sensor neighbour : vertices) {

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

    private void orientMinimumSpanningTreeLeaves(SimpleGraph<Sensor, SensorEdge> minimumSpanningTree, Set<Sensor> leaves) {
        for (Sensor leaf : leaves) {

            // Connect it to it's parent. Because it's a leaf, it's
            // parent is it's neighbour.
            Set<SensorEdge> edgesOfS = minimumSpanningTree.edgesOf(leaf);

            if (!edgesOfS.iterator().hasNext()) {
                return;
            }

            SensorEdge leafEdge = edgesOfS.iterator().next();
            Sensor parent = leafEdge.getSource().equals(leaf) ? leafEdge.getDestination() : leafEdge.getSource();

            addEdge(leaf, parent);
            double orientation = angleBetweenTwoSensors(leafEdge.getDestination(), leaf);
            leaf.setOrientation(orientation);
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
