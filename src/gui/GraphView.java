package gui;

import java.awt.Graphics;
import java.util.Set;

import javax.swing.JPanel;

import network.Sensor;

public interface GraphView {

    /**
     * This hooks into JGraph's paint method
     *
     * @param jgraphOffscreenBufferGraphics
     *            The off-screen buffer graphics context of a JGraph
     * @param vertices
     */
    public abstract void paint(Graphics jgraphOffscreenBufferGraphics, Set<Sensor> vertices);

    /**
     * @param graphApplet
     *            Used by action listeners which need to call an applet's
     *            methods.
     * @return a JPanel which is used to control parameters of that Graph represented by this view.
     */
    public abstract JPanel getControlPanel(GraphApplet graphApplet);

}
