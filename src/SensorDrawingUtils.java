import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class SensorDrawingUtils {

    private final static double SENSOR_SCREEN_WIDTH = 20.0;

    public static Rectangle2D GetBoundsFromSensor(Sensor sensor) {
        Point2D upLeftCorner = ComputeSensorScreenPosition(sensor);
        double offset = SENSOR_SCREEN_WIDTH / 2.0;
        return new Rectangle2D.Double(upLeftCorner.getX()-offset, upLeftCorner.getY()-offset, SENSOR_SCREEN_WIDTH,
                SENSOR_SCREEN_WIDTH);
    }

    public static double GetSensorScreenWidth() {
        return SENSOR_SCREEN_WIDTH;
    }

    public static void PaintSensorRange(Graphics g, Sensor sensor) {
        Point2D sensorCenter = ComputeSensorScreenPosition(sensor);

        double radius = sensor.getRange();
        double x = sensorCenter.getX() - radius / 2.0;
        double y = sensorCenter.getY() - radius / 2.0;

        Shape rangeCircle = new Ellipse2D.Double(x, y, radius, radius);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(ColorTheme.White);
        g2d.draw(rangeCircle);
    }

    private static Point2D ComputeSensorScreenPosition(Sensor sensor) {
        Point2D sensorPosition = sensor.getPosition();
        double centerX = sensorPosition.getX() - SENSOR_SCREEN_WIDTH / 2.0;
        double centerY = sensorPosition.getY() - SENSOR_SCREEN_WIDTH / 2.0;

        return new Point2D.Double(centerX, centerY);
    }

}
