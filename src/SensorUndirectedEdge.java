import java.util.HashSet;
import java.util.Set;

// immutable
public class SensorUndirectedEdge {
    private final int hashCode;

    private final Sensor first;
    private final Sensor second;

    public SensorUndirectedEdge(Sensor first, Sensor second) {
        this.first = first;
        this.second = second;
        this.hashCode = generateHashCode(first, second);
    }

    public Sensor getFirst() {
        return first;
    }

    public Sensor getSecond() {
        return second;
    }



    @Override
    public String toString() {
        return "SensorUndirectedEdge [first=" + first + ", second=" + second + "]";
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (other instanceof SensorUndirectedEdge)
            return false;

        return equals((SensorUndirectedEdge)other);
    }

    private boolean equals(SensorUndirectedEdge edge) {
        boolean firstSensorsAreEqual = this.getFirst().equals(edge.getFirst());
        boolean secondSensorsAreEqual = this.getSecond().equals(edge.getSecond());

        return firstSensorsAreEqual && secondSensorsAreEqual;
    }

    private int generateHashCode(Sensor first, Sensor second) {
        Set<Sensor> edge = new HashSet<Sensor>(2);
        edge.add(first);
        edge.add(second);

        return edge.hashCode();
    }
}
