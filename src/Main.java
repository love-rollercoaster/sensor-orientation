import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgrapht.Graph;
import org.jgrapht.demo.JGraphAdapterDemo;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.SimpleDirectedGraph;

// FIXME: Lots of side effects
public class Main extends JApplet {

    private static final long serialVersionUID = 3256444702936019250L;
    private static final int GRAPH_WIDTH = 900;
    private static final int GRAPH_HEIGHT = 900;

    private static final Dimension DEFAULT_SIZE = new Dimension(GRAPH_WIDTH, GRAPH_HEIGHT);
    private static int sensorRange = 100;

    private JGraph jgraph;
    private Set<Sensor> vertices;
    private MouseAdapter mouseAdapter;
    private GraphFactory selectedGraphFactory;
    private JPanel graphFactoryPanel;
    private Random random = new Random();
    private SpinnerNumberModel numberOfSensorsSpinnerModel;
    private boolean drawSensorAntenna = true;




    public static void main(String[] args) {
        JGraphAdapterDemo applet = new JGraphAdapterDemo();
        applet.init();
        applet.setBackground(ColorTheme.Black);

        initFrame(applet);
    }

    @Override
    public void init() {
        super.init();
        Sensor.SetRange(sensorRange); // FIXME
        Sensor.SetAngle(3 * Math.PI / 2);

        initRadioButtons();
        initSpinners();
        initDrawSensorAntennaCheckbox();
        initMouseAdapter();

        GraphFactory graphFactory = new ProximityGraphHelper();
        showGraph(graphFactory, graphFactory.createGraph(createTestSensors()));

        numberOfSensorsSpinnerModel.setValue(vertices.size());
        numberOfSensorsSpinnerModel.addChangeListener(new NumberOfSensorsChangeListener(numberOfSensorsSpinnerModel,
                vertices));

        resize(DEFAULT_SIZE);
    }

    @Override
    public void paint(Graphics g) {
        Graphics jgraphGraphics = jgraph.getOffgraphics();

        if (drawSensorAntenna) {
            selectedGraphFactory.paint(jgraphGraphics, vertices);
        }

        super.paint(g);
    }

    public void showGraphWithDifferentGraphFactory(GraphFactory jgraphtFactory) {
        showGraph(jgraphtFactory, jgraphtFactory.createGraph(vertices));
        repaint();
    }

    public void reset() {
        showGraphWithDifferentGraphFactory(selectedGraphFactory);
    }

    // FIXME: Hack
    public void showGraph(GraphFactory jgraphtFactory, Graph<Sensor, SensorEdge> jgrapht) {
        showGraph(jgraphtFactory, jgrapht, true);
    }

    // FIXME: Side effects
    private void showGraph(GraphFactory jgraphtFactory, Graph<Sensor, SensorEdge> jgrapht,
            boolean updateNumberOfSensorSpinnerModel) {
        this.vertices = jgrapht.vertexSet();
        this.selectedGraphFactory = jgraphtFactory;
        initGraphFactoryPanel(jgraphtFactory);

        JGraphModelAdapter<Sensor, SensorEdge> jgraphAdapter = (new JGraphConverter()).convertFromJGraphT(jgrapht);
        resetJGraph(jgraphAdapter, vertices);

        if (updateNumberOfSensorSpinnerModel) {
            numberOfSensorsSpinnerModel.setValue(vertices.size());
        }
    }

    // FIXME: Side effects
    private void initGraphFactoryPanel(GraphView jgraphtFactory) {
        if (graphFactoryPanel != null) {
            remove(graphFactoryPanel);
        }
        graphFactoryPanel = selectedGraphFactory.getControlPanel(this);
        add(graphFactoryPanel);
    }

    // FIXME: Side effects
    private void resetJGraph(JGraphModelAdapter<Sensor, SensorEdge> jgraphAdapter, Set<Sensor> vertices) {
        if (jgraph != null) {
            remove(jgraph);
        }
        jgraph = createJGraph(jgraphAdapter);
        add(jgraph);
        positionSensorsOnJGraph(jgraphAdapter, vertices);
    }

    private JGraph createJGraph(GraphModel model) {
        JGraph jgraph = new JGraph(model);
        jgraph.addMouseListener(mouseAdapter);
        jgraph.setPreferredSize(DEFAULT_SIZE);
        jgraph.setBackground(ColorTheme.Black);
        jgraph.setDragEnabled(false);
        jgraph.setAntiAliased(true);
        jgraph.setDropEnabled(false);
        jgraph.setEditable(false);
        jgraph.setMoveBeyondGraphBounds(false);
        jgraph.setBackground(null);

        return jgraph;
    }

    private void updateSensorPositionFromCell(GraphModel model, DefaultGraphCell cell) {
        Sensor sensor = (Sensor) model.getValue(cell);

        AttributeMap cellAttributes = cell.getAttributes();
        Rectangle2D cellBounds = GraphConstants.getBounds(cellAttributes);

        double centerX = cellBounds.getCenterX();
        double centerY = GRAPH_HEIGHT - cellBounds.getCenterY();

        sensor.setPosition(centerX, centerY);
    }

    private void positionSensorsOnJGraph(JGraphModelAdapter<Sensor, SensorEdge> jgraphAdapter, Set<Sensor> sensors) {
        for (Sensor sensor : sensors) {
            positionSensorOnJgraph(jgraphAdapter, sensor);
        }
    }

    private void initMouseAdapter() {
        mouseAdapter = new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent event) {
                Object[] cells = jgraph.getSelectionCells().clone();

                if (cells == null)
                    return;

                for (Object cellObject : cells) {
                    updateSensorPositionFromCell(jgraph.getModel(), (DefaultGraphCell) cellObject);
                }

                showGraphWithDifferentGraphFactory(selectedGraphFactory);
            }

        };
    }

    @SuppressWarnings("unchecked")
    private void positionSensorOnJgraph(JGraphModelAdapter<Sensor, SensorEdge> jgraphAdapter, Sensor sensor) {
        DefaultGraphCell cell = jgraphAdapter.getVertexCell((Object) sensor);

        AttributeMap cellAttributes = cell.getAttributes();
        GraphConstants.setBounds(cellAttributes, SensorDrawingUtils.GetBoundsFromSensor(sensor));

        AttributeMap cellAttr = new AttributeMap();
        cellAttr.put(cell, cellAttributes);
        jgraphAdapter.edit(cellAttr, null, null, null);
    }

    private void initRadioButtons() {
        Container c = getContentPane();
        c.setBackground(ColorTheme.Black);
        c.setLayout(new FlowLayout());

        final JRadioButton proximityButton = new JRadioButton(" Show Omnidirectional Graph ");
        final JRadioButton transmissionButton = new JRadioButton(" Show Directed Antennae Graph ");

        proximityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGraphWithDifferentGraphFactory(new ProximityGraphHelper());
            }
        });

        transmissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGraphWithDifferentGraphFactory(new TransmissionGraphHelper());
            }
        });

        ButtonGroup radioButtons = new ButtonGroup();
        radioButtons.add(proximityButton);
        radioButtons.add(transmissionButton);

        JPanel radioPanel = new JPanel(new FlowLayout());
        radioPanel.add(proximityButton);
        radioPanel.add(transmissionButton);

        proximityButton.setSelected(true);
        c.add(radioPanel);
    }

    private static void initFrame(JGraphAdapterDemo applet) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("JGraphT Adapter to JGraph Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setBackground(ColorTheme.Black);
    }

    private Set<Sensor> createTestSensors() {
        Set<Sensor> sensors = new HashSet<Sensor>();
        sensors.add(new Sensor(new Point2D.Double(130, 40)));
        sensors.add(new Sensor(new Point2D.Double(130, 100)));
        sensors.add(new Sensor(new Point2D.Double(190, 100)));
        // sensors.add(new Sensor(new Point2D.Double(10, 10)));
        // sensors.add(new Sensor(new Point2D.Double(19, 10)));
        // sensors.add(new Sensor(new Point2D.Double(57, 63)));
        sensors.add(new Sensor(new Point2D.Double(494, 188)));
        sensors.add(new Sensor(new Point2D.Double(105, 347)));
        // sensors.add(new Sensor(new Point2D.Double(265, 656)));
        sensors.add(new Sensor(new Point2D.Double(283, 243)));
        sensors.add(new Sensor(new Point2D.Double(249, 301)));
        // sensors.add(new Sensor(new Point2D.Double(373, 571)));

        sensors.add(new Sensor(new Point2D.Double(200, 700)));
        sensors.add(new Sensor(new Point2D.Double(300, 700)));

        return sensors;
    }

    private JSpinner makeRangeSpinner() {
        final SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(Sensor.GetRange(), 0, GRAPH_WIDTH, 1);
        JSpinner rangeSpinner = new JSpinner(spinnerNumberModel);

        spinnerNumberModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Sensor.SetRange(spinnerNumberModel.getNumber().doubleValue());
                reset();
            }
        });

        return rangeSpinner;
    }

    private class NumberOfSensorsChangeListener implements ChangeListener {
        private final Stack<Sensor> sensorsStack = new Stack<Sensor>();
        private final SpinnerNumberModel numberOfSensorsSpinnerModel;

        public NumberOfSensorsChangeListener(SpinnerNumberModel numberOfSensorsSpinnerModel, Set<Sensor> vertices) {
            this.numberOfSensorsSpinnerModel = numberOfSensorsSpinnerModel;
            for (Sensor sensor : vertices) {
                sensorsStack.push(sensor);
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            int numberOfSensors = numberOfSensorsSpinnerModel.getNumber().intValue();
            int sensorNumberDifference = numberOfSensors - sensorsStack.size();

            if (sensorNumberDifference == 0) {
                return;
            } else if (sensorNumberDifference < 0) {
                while (sensorNumberDifference++ < 0) {
                    Sensor sensor = sensorsStack.pop();
                    System.out.println("Stack: " + sensor);
                    System.out.println("  Set: " + vertices + "\n");
                    vertices = new HashSet<Sensor>(vertices);
                    vertices.remove(sensor);
                }
            } else if (sensorNumberDifference > 0) {
                while (sensorNumberDifference-- > 0) {
                    Sensor sensor = makeSensorWithRandomPosition();
                    sensorsStack.push(sensor);
                    vertices = new HashSet<Sensor>(vertices);
                    vertices.add(sensor);
                }
            }

            showGraph(selectedGraphFactory, selectedGraphFactory.createGraph(vertices), false);
        }

        private Sensor makeSensorWithRandomPosition() {
            double x = random.nextInt((int) (GRAPH_WIDTH - SensorDrawingUtils.GetSensorScreenWidth()));
            double y = random.nextInt((int) (GRAPH_WIDTH - SensorDrawingUtils.GetSensorScreenWidth()));
            return new Sensor(new Point2D.Double(x, y));
        }
    }

    private JSpinner makeNumberOfSensorSpinner() {
        this.numberOfSensorsSpinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        JSpinner numberOfSensorsSpinner = new JSpinner(numberOfSensorsSpinnerModel);

        return numberOfSensorsSpinner;
    }

    private void initDrawSensorAntennaCheckbox() {
        final JCheckBox drawSensorAntennaCheckbox = new JCheckBox("Draw sensor antenna");
        drawSensorAntennaCheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                drawSensorAntenna = drawSensorAntennaCheckbox.isSelected();
            }
        });
        this.getContentPane().add(drawSensorAntennaCheckbox);
    }

    private void initSpinners() {
        JLabel sensorRangeLabel = new JLabel("Sensor Range: ");
        JLabel numberOfSensorLabel = new JLabel("Sensor #: ");
        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(0, 2));
        panel.add(sensorRangeLabel);
        panel.add(makeRangeSpinner());
        panel.add(numberOfSensorLabel);
        panel.add(makeNumberOfSensorSpinner());
        this.getContentPane().add(panel);
    }

    private ProximityGraph createTestProximityGraph() {
        return new ProximityGraph(createTestSensors());
    }

    private SimpleDirectedGraph<Sensor, SensorEdge> createTestDirectedGraph() {
        Sensor a = new Sensor(new Point2D.Double(130, 40));
        Sensor b = new Sensor(new Point2D.Double(130, 100));
        Sensor c = new Sensor(new Point2D.Double(190, 100));

        SimpleDirectedGraph<Sensor, SensorEdge> graph = new SimpleDirectedGraph<Sensor, SensorEdge>(
                new SensorEdgeFactory());
        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addEdge(a, b);
        graph.addEdge(c, b);

        return graph;
    }
}
