import java.awt.geom.Point2D;

public class Sensor {
    private static int NextId = 0;
    private static Double Range;
    private static Double Angle;

    private final int id;
    private Point2D position;

    public static double GetRange() {
        return Range;
    }

    public static double GetAngle() {
        return Angle;
    }

    public static void SetRange(double range) {
        Sensor.Range = range;
    }

    public static void SetAngle(double angle) {
        Sensor.Angle = angle;
    }

    public Sensor(Point2D position) {
        this.position = position;
        this.id = NextId++;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        setPosition(position.getX(), position.getY());
    }

    public void setPosition(Double x, Double y) {
        this.position = new Point2D.Double(x, y);
    }

    // FIXME: this method only supports omnidirectional sensors.
    public boolean canReach(Sensor sensor) {
        Double distanceBetweenSensors = this.position.distance(sensor.getPosition());
        return distanceBetweenSensors.compareTo(Range) < 0;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
