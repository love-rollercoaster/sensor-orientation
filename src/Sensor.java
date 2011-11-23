import java.awt.geom.Point2D;

public class Sensor {
    private static int NextId = 0;
    private static Double range;

    private Point2D position;
    private final Double  angle;
    private final int id;

    public Sensor(Point2D position) {
        this(position, 2 * Math.PI);
    }

    public Sensor(Point2D position, double angle) {
        this.position = position;
        this.angle = angle;
        this.id = NextId++;
    }

    public static void SetRange(double range) {
        Sensor.range = range;
    }

    public Point2D getPosition() {
        return position;
    }

    public double getAngle() {
        return angle;
    }

    public double getRange() {
        return range;
    }

    public void setPosition(Point2D position) {
        setPosition(position.getX(), position.getY());
    }

    public void setPosition(Double x, Double y) {
        this.position = new Point2D.Double(x, y);
    }

    /**
     * FIXME: We have to change this when we deal with directional stuff.
     */
    public boolean canReach(Sensor sensor) {
        if (angle.compareTo(2*Math.PI) != 0) {
            throw new RuntimeException("Not implemented with sensors with directional antennas");
        }

        Double distanceBetweenSensors = this.position.distance(sensor.getPosition());
        return distanceBetweenSensors.compareTo(range) < 0;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
