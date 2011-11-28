import java.util.Set;

import org.jgrapht.Graph;


public interface JGraphTFactory {

    Graph<Sensor, SensorEdge> createGraph(Set<Sensor> vertices);

}
