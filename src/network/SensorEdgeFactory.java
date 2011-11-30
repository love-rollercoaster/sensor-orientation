package network;

import org.jgrapht.EdgeFactory;


public class SensorEdgeFactory implements EdgeFactory<Sensor, SensorEdge> {

	/**
	 * Creates a new edge whose endpoints are the specified source and target vertices.
	 */
	public SensorEdge createEdge(Sensor sourceVertex, Sensor targetVertex) {
		return new SensorEdge(sourceVertex, targetVertex);
	}

}
