import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class SensorDrawingUtils {
    private final static double SENSOR_SCREEN_WIDTH = 20.0;

    public static Rectangle2D GetBoundsFromSensor(Sensor sensor) {
        Point2D upLeftCorner = ConvertCoordinateSystem(ComputeSensorScreenPosition(sensor));
        return new Rectangle2D.Double(upLeftCorner.getX(), upLeftCorner.getY(), SENSOR_SCREEN_WIDTH, SENSOR_SCREEN_WIDTH);
    }

    public static Point2D ConvertCoordinateSystem(Point2D point) {

        return new Point2D.Double(point.getX(), GraphApplet.JGRAPH.getHeight() - point.getY());
    }

    public static double GetSensorScreenWidth() {
        return SENSOR_SCREEN_WIDTH;
    }

    public static void PaintSensorAntenna(Graphics g, Shape antennae) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(ColorTheme.Grey);
        g2d.draw(antennae);
    }

    public static Point2D ComputeSensorScreenPosition(Sensor sensor) {
        Point2D sensorPosition = sensor.getPosition();
        double centerX = sensorPosition.getX() - SENSOR_SCREEN_WIDTH / 2.0;
        double centerY = sensorPosition.getY() + SENSOR_SCREEN_WIDTH / 2.0;

        return new Point2D.Double(centerX, centerY);
    }
}

