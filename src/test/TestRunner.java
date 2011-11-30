package test;

import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import network.ProximityGraph;
import network.Sensor;
import network.SensorEdge;
import network.TransmissionGraph;

import org.jgrapht.alg.FloydWarshallShortestPaths;

import algorithms.Algorithms;
import algorithms.UnconnectedGraphException;

public class TestRunner {

    public TestResult run(Set<Sensor> vertices, PrintStream printWriter) throws UnconnectedGraphException {

        ProximityGraph proxGraph = new ProximityGraph(vertices);
        TransmissionGraph transGraph = new TransmissionGraph(vertices);

        if (!Algorithms.IsStronglyConnected(transGraph)) {
            throw new UnconnectedGraphException();
        }
        FloydWarshallShortestPaths<Sensor, SensorEdge> proxShortestPaths = new FloydWarshallShortestPaths<Sensor, SensorEdge>(
                proxGraph);
        FloydWarshallShortestPaths<Sensor, SensorEdge> transShortestPaths = new FloydWarshallShortestPaths<Sensor, SensorEdge>(
                transGraph);
        Set<Set<Sensor>> powerSet = Algorithms.MakeVerticesPowerSet(vertices);

        double shortestPathRatio = 0;
        double routeLengthRatio = 0;

        printWriter.println();
        printWriter.println("==== Running tests on sensors [" + new Date() + "] ====================");

        printWriter.println("Sensor Range: " + Sensor.GetRange());
        printWriter.println("Sector Angle: " + Math.toDegrees(Sensor.GetAngle()));
        printWriter.println();

        for (Sensor s : vertices) {
            printWriter.print("Sensor " + s);
            printWriter.print(" [orientation: " + Math.toDegrees(s.getOrientation()));
            printWriter.println(" , position: (" + s.getPosition().getX() + ", " + s.getPosition().getY() + ")]");
        }

        for (Set<Sensor> sensorSet : powerSet) {
            Iterator<Sensor> sensorIterator = sensorSet.iterator();

            Sensor source = sensorIterator.next();
            Sensor destination = sensorIterator.next();

            printWriter.println("Sensors: Source = " + source + " Destination = " + destination);
            printWriter.println();
            double shortestPathResult = transShortestPaths.shortestDistance(source, destination)
                    / proxShortestPaths.shortestDistance(source, destination);
            double routeLengthResult = Algorithms.ComputeLengthOfRoute(transShortestPaths.getShortestPath(source, destination))
                    / Algorithms.ComputeLengthOfRoute(proxShortestPaths.getShortestPath(source, destination));
            printWriter.println("Shortest Path Ratio: " + shortestPathResult);
            printWriter.println("Route Length Ratio: " + routeLengthResult);
            shortestPathRatio += shortestPathResult;
            routeLengthRatio += routeLengthResult;
        }

        shortestPathRatio /= powerSet.size();
        routeLengthRatio /= powerSet.size();

        printWriter.println();
        printWriter.println("Average Shortest Path Ratio: " + shortestPathRatio);
        printWriter.println("Average Route Length Ratio: " + routeLengthRatio);

        double diameterOfTransmissionGraph = Algorithms.ComputeNetworkDiameter(transGraph);
        double diameterOfProximityGraph = Algorithms.ComputeNetworkDiameter(proxGraph);
        double diameterRatio = diameterOfTransmissionGraph / diameterOfProximityGraph;

        printWriter.println("Network Diameter Ratio: " + diameterRatio);
        printWriter.println("");

        return new TestResult(shortestPathRatio, routeLengthRatio, diameterRatio);
    }
}
