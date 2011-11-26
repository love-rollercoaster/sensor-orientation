import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;

public class TransmissionGraph implements DirectedGraph<Sensor, SensorEdge>{
    private final Set<SensorEdge> edges;
    private final Set<Sensor> vertices;
    //I think the sensors are all supposed to have the same range and angle so grabbing from one sensor will be fine
    private Double anglePhi;
    private Double range;
    
    public TransmissionGraph(Set<SensorEdge> myEdges, Set<Sensor> myVertices) {
        edges = myEdges;
        vertices = myVertices;
        if(!vertices.isEmpty()){
        	Iterator<Sensor> verticesIterator = vertices.iterator();
        	//This next check should be unnecessary
        	if(verticesIterator.hasNext()){
        		Sensor s = verticesIterator.next();
        		anglePhi = s.getAngle();
        		range = s.getRange();
        	}        	
        }
    }    
    
    public TransmissionGraph(TransmissionGraph toCopy) {
        edges = toCopy.getEdges();
        vertices = toCopy.getVertices();
        if(!vertices.isEmpty()){
        	Iterator<Sensor> verticesIterator = vertices.iterator();
        	//This next check should be unnecessary
        	if(verticesIterator.hasNext()){
        		Sensor s = verticesIterator.next();
        		anglePhi = s.getAngle();
        		range = s.getRange();
        	}        	
        }        
    }     
    
    public TransmissionGraph(ProximityGraph toCopy) {
        edges = toCopy.getEdges();
        vertices = toCopy.getVertices();
        if(!vertices.isEmpty()){
        	Iterator<Sensor> verticesIterator = vertices.iterator();
        	//This next check should be unnecessary
        	if(verticesIterator.hasNext()){
        		Sensor s = verticesIterator.next();
        		anglePhi = s.getAngle();
        		range = s.getRange();
        	}        	
        }        
    }         
    
    public Set<SensorEdge> getEdges() {
        return edges;
    }

    public Set<Sensor> getVertices() {
        return vertices;
    }

	@Override
	public Set<SensorEdge> getAllEdges(Sensor sourceVertex, Sensor targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SensorEdge getEdge(Sensor sourceVertex, Sensor targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EdgeFactory<Sensor, SensorEdge> getEdgeFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SensorEdge addEdge(Sensor sourceVertex, Sensor targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addEdge(Sensor sourceVertex, Sensor targetVertex,
			SensorEdge e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addVertex(Sensor v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEdge(Sensor sourceVertex, Sensor targetVertex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEdge(SensorEdge e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsVertex(Sensor v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<SensorEdge> edgeSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SensorEdge> edgesOf(Sensor vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllEdges(Collection<? extends SensorEdge> edges) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<SensorEdge> removeAllEdges(Sensor sourceVertex,
			Sensor targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllVertices(Collection<? extends Sensor> vertices) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SensorEdge removeEdge(Sensor sourceVertex, Sensor targetVertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeEdge(SensorEdge e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeVertex(Sensor v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Sensor> vertexSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sensor getEdgeSource(SensorEdge e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sensor getEdgeTarget(SensorEdge e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getEdgeWeight(SensorEdge e) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int inDegreeOf(Sensor vertex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<SensorEdge> incomingEdgesOf(Sensor vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int outDegreeOf(Sensor vertex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<SensorEdge> outgoingEdgesOf(Sensor vertex) {
		// TODO Auto-generated method stub
		return null;
	}    

}
