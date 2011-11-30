package gui;

import java.util.Set;


import network.Sensor;
import network.SensorEdge;

import org.jgrapht.Graph;

public interface GraphFactory extends GraphView {

    Graph<Sensor, SensorEdge> createGraph(Set<Sensor> vertices);
}
