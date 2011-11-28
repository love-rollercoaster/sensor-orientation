import java.util.Set;

import org.jgrapht.Graph;


public class TransmissionGraphFactory implements JGraphTFactory {

    @Override
    public Graph<Sensor, SensorEdge> createGraph(Set<Sensor> vertices) {
        return new TransmissionGraph(vertices);
    }

}
