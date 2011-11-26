import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;

// immutable
public class SensorEdge extends DefaultEdge{
    /**
	 *  Automatically generated when I extended DefaultEdge. No idea what it's for.
	 */
	private static final long serialVersionUID = -1387584016973135099L;

	private final int hashCode;

    private final Sensor source;
    private final Sensor destination;

    public SensorEdge(Sensor first, Sensor second) {
        this.source = first;
        this.destination = second;
        this.hashCode = generateHashCode(first, second);
    }

    public Sensor getSource() {
        return source;
    }

    public Sensor getDestination() {
        return destination;
    }



    @Override
    public String toString() {
        return "SensorEdge [source=" + source + ", destination=" + destination + "]";
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
        if (!(other instanceof SensorEdge))
            return false;

        return this.equals((SensorEdge)other);
    }

    private boolean equals(SensorEdge edge) {
        boolean firstSensorsAreEqual = this.getSource().equals(edge.getSource());
        boolean secondSensorsAreEqual = this.getDestination().equals(edge.getDestination());

        return firstSensorsAreEqual && secondSensorsAreEqual;
    }

    private int generateHashCode(Sensor first, Sensor second) {
        Set<Sensor> edge = new HashSet<Sensor>(2);
        edge.add(first);
        edge.add(second);

        return edge.hashCode();
    }
}
