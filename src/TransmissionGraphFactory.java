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
import javax.swing.JTextField;

import org.jgrapht.Graph;

public class TransmissionGraphFactory implements GraphFactory {


    @Override
    public Graph<Sensor, SensorEdge> createGraph(Set<Sensor> vertices) {
        return new TransmissionGraph(vertices);
    }

    @Override
    public void paint(Graphics jgraphGraphics, Set<Sensor> vertices) {
        for (Sensor sensor : vertices) {
            SensorDrawingUtils.PaintSensorAntenna(jgraphGraphics, getDirectionalAntennaeShape(sensor));
        }
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

        final JTextField sensorAngleField = new JTextField();

        final GraphFactory _this = this;

        sensorAngleField.addKeyListener(new NumberKeyAdapter());
        sensorAngleField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double angle = Math.toRadians(Double.parseDouble(sensorAngleField.getText()));
                Sensor.SetAngle(angle);
                main.showGraphWithDifferentGraphFactory(_this);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));
        panel.add(sensorAngleLabel);
        panel.add(sensorAngleField);

        return panel;
    }

    private class NumberKeyAdapter extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent e) {
            char key = e.getKeyChar();

            if (!(Character.isDigit(key) || key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_DELETE)) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                e.consume();
            }
        }
    }
}
