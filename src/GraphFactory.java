import java.util.HashSet;
import java.util.Set;

public class GraphFactory {

    public ProximityGraph makeProximityGraphFromSensors(Set<Sensor> sensors) {
        return new ProximityGraph(generateEdges(sensors), sensors);
    }

    private Set<SensorUndirectedEdge> generateEdges(Set<Sensor> sensors) {
        Set<SensorUndirectedEdge> edges = new HashSet<SensorUndirectedEdge>(sensors.size());

        for (Sensor currentSensor : sensors) {

            for (Sensor otherSensor : sensors) {

                if (otherSensor.equals(currentSensor)) {
                    continue;
                }

                if (currentSensor.canReach(otherSensor)) {
                    edges.add(new SensorUndirectedEdge(currentSensor, otherSensor));
                }
            }
        }

        return edges;
    }

}
