import java.awt.Graphics;
import java.util.Set;

import javax.swing.JPanel;

import org.jgrapht.Graph;

public interface GraphFactory {

    Graph<Sensor, SensorEdge> createGraph(Set<Sensor> vertices);

    void paint(Graphics jgraphGraphics, Set<Sensor> vertices);

    JPanel getControlPanel(Main main);
}
