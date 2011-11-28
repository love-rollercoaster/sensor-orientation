import java.util.Set;

import org.jgrapht.graph.SimpleGraph;

public class ProximityGraph extends SimpleGraph<Sensor, SensorEdge> {
    private static final long serialVersionUID = 1L;

    public ProximityGraph(Set<Sensor> sensors) {
        super(SensorEdge.class);
        addVerticesFromSensorSet(sensors);
        computeAndaddEdgesFromSensorSet(sensors);
    }

    private void addVerticesFromSensorSet(Set<Sensor> sensors) {
        for (Sensor sensor : sensors) {
            addVertex(sensor);
        }
    }

    private void computeAndaddEdgesFromSensorSet(Set<Sensor> sensors) {
        for (Sensor currentSensor : sensors) {
            for (Sensor otherSensor : sensors) {

                if (otherSensor.equals(currentSensor)) {
                    continue;
                }

                if (currentSensor.canReach(otherSensor)) {
                    SensorEdge edge = new SensorEdge(currentSensor, otherSensor);
                    this.addEdge(currentSensor, otherSensor, edge);
                }
            }
        }
    }
}
