import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.VertexRenderer;

public class SensorVertexRenderer extends VertexRenderer {

    private static final long serialVersionUID = 1L;

    /**
     * Paint the renderer. Overrides superclass paint to add specific painting.
     */
    @Override
    public void paint(Graphics g) {
        try {
            if (gradientColor != null && !preview && isOpaque()) {
                setOpaque(false);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, getBackground(),
                        getWidth(), getHeight(), gradientColor, true));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paint(g);
            paintSelectionBorder(g);
        } catch (IllegalArgumentException e) {
            // JDK Bug: Zero length string passed to TextLayout constructor
        }
    }
}
