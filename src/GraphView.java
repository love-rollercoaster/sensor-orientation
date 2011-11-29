import java.awt.Graphics;
import java.util.Set;

import javax.swing.JPanel;

public interface GraphView {

    public abstract void paint(Graphics jgraphGraphics, Set<Sensor> vertices);

    public abstract JPanel getControlPanel(Main main);

}