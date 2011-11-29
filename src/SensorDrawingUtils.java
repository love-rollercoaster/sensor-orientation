import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;

public class SensorDrawingUtils {
    private final static int GRAPH_SCREEN_WIDTH = 900;
    private final static double SENSOR_SCREEN_WIDTH = 20.0;

    public static Rectangle2D GetBoundsFromSensor(Sensor sensor) {
        Point2D upLeftCorner = ConvertCoordinateSystem(ComputeSensorScreenPosition(sensor));
        return new Rectangle2D.Double(upLeftCorner.getX(), upLeftCorner.getY(), SENSOR_SCREEN_WIDTH, SENSOR_SCREEN_WIDTH);
    }

    public static Point2D ConvertCoordinateSystem(Point2D point) {
        return new Point2D.Double(point.getX(), GRAPH_SCREEN_WIDTH - point.getY());
    }

    public static double GetSensorScreenWidth() {
        return SENSOR_SCREEN_WIDTH;
    }

    public static double GetGraphScreenWidth() {
        return GRAPH_SCREEN_WIDTH;
    }

    public static void PaintSensorAntenna(Graphics g, Shape antennae) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(ColorTheme.Grey);
        g2d.draw(antennae);

        // g2d.setColor(Color.green);
        // g2d.draw(debugOrientation);

//        g2d.setColor(Color.red);
//        double x = sensor.getPosition().getX();
//        double y = sensor.getPosition().getY();
//        g2d.draw(new Ellipse2D.Double(x-1, GRAPH_SCREEN_WIDTH - y-1, 2, 2));
    }

    private static Shape GetDebugOrientationLine(Sensor sensor) {
        Point2D position = ConvertCoordinateSystem(sensor.getPosition());

        double angle = sensor.getOrientation();
        double range = Sensor.GetRange();
        double x = position.getX() + range * Math.cos(angle);
        double y = position.getY() - range * Math.sin(angle);

        return new Line2D.Double(position.getX(), position.getY(), x, y);
    }


    public static Point2D ComputeSensorScreenPosition(Sensor sensor) {
        Point2D sensorPosition = sensor.getPosition();
        double centerX = sensorPosition.getX() - SENSOR_SCREEN_WIDTH / 2.0;
        double centerY = sensorPosition.getY() + SENSOR_SCREEN_WIDTH / 2.0;

        return new Point2D.Double(centerX, centerY);
    }
}

