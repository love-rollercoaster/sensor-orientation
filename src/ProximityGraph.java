import java.util.Set;

public class ProximityGraph {
    private final Set<SensorUndirectedEdge> edges;
    private final Set<Sensor> vertices;

    public ProximityGraph(Set<SensorUndirectedEdge> edges, Set<Sensor> vertices) {
        this.edges = edges;
        this.vertices = vertices;
    }

    public Set<SensorUndirectedEdge> getEdges() {
        return edges;
    }

    public Set<Sensor> getVertices() {
        return vertices;
    }
}
