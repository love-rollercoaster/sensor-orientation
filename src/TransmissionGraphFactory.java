import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgrapht.Graph;
import org.jgrapht.alg.StrongConnectivityInspector;

public class TransmissionGraphFactory implements GraphFactory {

    boolean stronglyConnected = false;

    @Override
    public Graph<Sensor, SensorEdge> createGraph(Set<Sensor> vertices) {
        TransmissionGraph graph = new TransmissionGraph(vertices);
        this.stronglyConnected = isStronglyConnected(graph);
        return graph;
    }

    @Override
    public void paint(Graphics jgraphGraphics, Set<Sensor> vertices) {
        for (Sensor sensor : vertices) {
            SensorDrawingUtils.PaintSensorAntenna(jgraphGraphics, getDirectionalAntennaeShape(sensor));
        }
    }

    private boolean isStronglyConnected(TransmissionGraph graph) {
        StrongConnectivityInspector<Sensor, SensorEdge> connectivityInspector = new StrongConnectivityInspector<Sensor, SensorEdge>(graph);
        return connectivityInspector.isStronglyConnected();
    }

    private static Shape getDirectionalAntennaeShape(Sensor sensor) {
        double sensorRange = Sensor.GetRange();

        double width, height;
        width = height = sensorRange * 2;

        double orientation = Math.toDegrees(sensor.getOrientation());
        double extent = Math.toDegrees(Sensor.GetAngle());
        double start = orientation - extent / 2.0;

        Point2D sensorPosition = SensorDrawingUtils.ConvertCoordinateSystem(sensor.getPosition());
        double x = sensorPosition.getX() - sensorRange;
        double y = sensorPosition.getY() - sensorRange;

        return new Arc2D.Double(x, y, width, height, start, extent, Arc2D.PIE);
    }


    @Override
    public JPanel getControlPanel(final Main main) {

        JLabel sensorAngleLabel = new JLabel("Sensor Angle: ");
        JLabel stronglyConnectedLabel = new JLabel("Strongly Connected: ");
        JLabel stronglyConnectedValueLabel = new JLabel(stronglyConnected? "true" : "false");

        int max = 360;
        int min = 0;
        double angle = Math.toDegrees(Sensor.GetAngle());

        final SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(angle, min, max, 1);
        JSpinner angleSpinner = new JSpinner(spinnerNumberModel);

        final GraphFactory _this = this;

        spinnerNumberModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Sensor.SetAngle(Math.toRadians(spinnerNumberModel.getNumber().doubleValue()));
                main.showGraphWithDifferentGraphFactory(_this);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));
        panel.add(sensorAngleLabel);
        panel.add(angleSpinner);
        panel.add(stronglyConnectedLabel);
        panel.add(stronglyConnectedValueLabel);

        return panel;
    }
}
