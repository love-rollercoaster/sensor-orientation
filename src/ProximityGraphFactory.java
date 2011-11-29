import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Set;

import javax.swing.JPanel;

import org.jgrapht.Graph;


public class ProximityGraphFactory implements GraphFactory {

    @Override
    public Graph<Sensor, SensorEdge> createGraph(Set<Sensor> vertices) {
        return new ProximityGraph(vertices);
    }

    @Override
    public void paint(Graphics jgraphGraphics, Set<Sensor> vertices) {
        for (Sensor sensor : vertices) {
            SensorDrawingUtils.PaintSensorAntenna(jgraphGraphics, getOmnidirectionalAntennaShape(sensor));
        }
    }

    private Shape getOmnidirectionalAntennaShape(Sensor sensor) {
        Point2D sensorCenter = SensorDrawingUtils.ConvertCoordinateSystem(sensor.getPosition());
        double radius = Sensor.GetRange();
        double x = sensorCenter.getX() - radius;
        double y = sensorCenter.getY() - radius;

        return new Ellipse2D.Double(x, y, radius*2, radius*2);
    }

    @Override
    public JPanel getControlPanel(Main main) {
        return new JPanel();
    }

}
