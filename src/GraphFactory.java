import java.util.Set;


import org.jgrapht.Graph;

public interface GraphFactory extends GraphView {

    Graph<Sensor, SensorEdge> createGraph(Set<Sensor> vertices);
}
