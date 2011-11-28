import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.*;
import org.jgrapht.traverse.BreadthFirstIterator;

public class TransmissionGraph implements DirectedGraph<Sensor, SensorEdge> {
	private final Set<SensorEdge> edges;
	private final Set<Sensor> vertices;
	private SensorEdgeFactory edgeFactory = null;
	// I think the sensors are all supposed to have the same range and angle so
	// grabbing from one sensor will be fine
	private Double anglePhi;
	private Double range;
	private ProximityGraph proxGraph;
	private ProximityGraph minSpanTree;
	private StrongConnectivityInspector<Sensor, SensorEdge> connectivityInsp = null;

	public TransmissionGraph(Set<SensorEdge> myEdges, Set<Sensor> myVertices) {
		KruskalMinimumSpanningTree<Sensor, SensorEdge> myMST;
		edges = new HashSet<SensorEdge>();
		vertices = myVertices;
		anglePhi = Sensor.GetAngle();
		range = Sensor.GetRange();		
		edgeFactory = new SensorEdgeFactory();
		//proxGraph = new ProximityGraph(myEdges, myVertices);
		proxGraph = new ProximityGraph(myVertices);
		myMST = new KruskalMinimumSpanningTree<Sensor, SensorEdge>(proxGraph);
		//minSpanTree = new ProximityGraph(myMST.getEdgeSet(), myVertices);
		minSpanTree = new ProximityGraph(myVertices);
		Set<SensorEdge> mstEdges = new HashSet<SensorEdge>(minSpanTree.edgeSet());
		minSpanTree.removeAllEdges(mstEdges);
		for(SensorEdge e : myMST.getEdgeSet()){
			minSpanTree.addEdge(e.getSource(), e.getDestination());
		}
		orientAntennae();
		connectivityInsp = new StrongConnectivityInspector<Sensor, SensorEdge>(this);
		//assert(connectivityInsp.isStronglyConnected() == true);
		if(connectivityInsp.isStronglyConnected()){
			System.out.println("Graph is strongly connected.");
		}
	}

	// TODO Clean this up. If we use getters or something for angle/range it
	// would be better...
	public TransmissionGraph(TransmissionGraph toCopy) {
		KruskalMinimumSpanningTree<Sensor, SensorEdge> myMST;
		edges = new HashSet<SensorEdge>();
		vertices = toCopy.vertexSet();
		anglePhi = Sensor.GetAngle();
		range = Sensor.GetRange();		
		edgeFactory = new SensorEdgeFactory();
		//proxGraph = new ProximityGraph(edges, vertices);
		proxGraph = new ProximityGraph(vertices);
		myMST = new KruskalMinimumSpanningTree<Sensor, SensorEdge>(proxGraph);
		//minSpanTree = new ProximityGraph(myMST.getEdgeSet(), vertices);
		minSpanTree = new ProximityGraph(vertices);
		Set<SensorEdge> mstEdges = new HashSet<SensorEdge>(minSpanTree.edgeSet());
		minSpanTree.removeAllEdges(mstEdges);
		for(SensorEdge e : myMST.getEdgeSet()){
			minSpanTree.addEdge(e.getSource(), e.getDestination());
		}		
		orientAntennae();
		connectivityInsp = new StrongConnectivityInspector<Sensor, SensorEdge>(this);
		//assert(connectivityInsp.isStronglyConnected() == true);	
		if(connectivityInsp.isStronglyConnected()){
			System.out.println("Graph is strongly connected.");
		}
	}

	public TransmissionGraph(ProximityGraph toCopy) {
		KruskalMinimumSpanningTree<Sensor, SensorEdge> myMST;
		edges = new HashSet<SensorEdge>();
		vertices = toCopy.vertexSet();
		anglePhi = Sensor.GetAngle();
		range = Sensor.GetRange();		
		edgeFactory = new SensorEdgeFactory();
		proxGraph = toCopy;
		myMST = new KruskalMinimumSpanningTree<Sensor, SensorEdge>(proxGraph);
		//minSpanTree = new ProximityGraph(myMST.getEdgeSet(), vertices);
		minSpanTree = new ProximityGraph(vertices);
		Set<SensorEdge> mstEdges = new HashSet<SensorEdge>(minSpanTree.edgeSet());
		minSpanTree.removeAllEdges(mstEdges);
		for(SensorEdge e : myMST.getEdgeSet()){
			minSpanTree.addEdge(e.getSource(), e.getDestination());
		}				
		orientAntennae();
		connectivityInsp = new StrongConnectivityInspector<Sensor, SensorEdge>(this);
		//assert(connectivityInsp.isStronglyConnected() == true);
		if(connectivityInsp.isStronglyConnected()){
			System.out.println("Graph is strongly connected.");
		}		
	}

	private void orientAntennae() {
		Set<SensorEdge> matching = new HashSet<SensorEdge>();
		// Note that it's possible for a leaf to be in the matching
		Set<Sensor> leaves = new HashSet<Sensor>();
		Set<Sensor> verticesAdded = new HashSet<Sensor>();
		// Grab the first vertex in our set and use it to root the bfs
		// Make sure we have a vertex to work with
		if (vertices.iterator().hasNext()) {
			Sensor startVtx = vertices.iterator().next();
			BreadthFirstIterator<Sensor, SensorEdge> bfsIt = new BreadthFirstIterator<Sensor, SensorEdge>(
					minSpanTree, startVtx);
			if (bfsIt.hasNext()) {
				Sensor firstCoupled = bfsIt.next();
				matching.add(minSpanTree.getEdge(startVtx, firstCoupled));
				verticesAdded.add(startVtx);
				verticesAdded.add(firstCoupled);
				// This while loop creates the matching
				while (bfsIt.hasNext()) {
					Sensor temp = bfsIt.next();
					// "if leaf node or edge between it and its parent is in matching M, continue"
					// Note that if the degree of this vertex is 1 it's a leaf
					// Also note that if the node is a source/target of an edge
					// in the matching then the edge between it and its parent
					// is in M
					if (minSpanTree.degreeOf(temp) > 1) {
						// Loop through the matching's edges and check for our
						// target
						boolean moveOn = false;
						Set<SensorEdge> edgesOfTemp = minSpanTree.edgesOf(temp);
						for (SensorEdge e : edgesOfTemp) {
							if (matching.contains(e)) {
								moveOn = true;
							}
						}
						if (!moveOn) {
							// pick an edge between temp and one of its children
							// and add to M
							Iterator<SensorEdge> neighbourIt = edgesOfTemp.iterator();
							while (!moveOn && neighbourIt.hasNext()) {
								SensorEdge neighbourEdge = neighbourIt.next();
								// Note that temp is going to be one of source
								// or dest
								Sensor nSource = neighbourEdge.getSource();
								Sensor nDest = neighbourEdge.getDestination();
								// This check is so we don't move back up the
								// tree
								if (!verticesAdded.contains(nSource)
										&& !verticesAdded.contains(nDest)) {
									verticesAdded.add(nSource);
									verticesAdded.add(nDest);
									matching.add(neighbourEdge);
									moveOn = true;
								}
							} /* while(!moveOn && neighbourIt.hasNext()) */
						} /* if(!moveOn) */
					} else {
						if (minSpanTree.degreeOf(temp) == 1) {
							leaves.add(temp);
						}
					} /* if(minSpanTree.degreeOf(temp) > 1) */
				} /* while(bfsIt.hasNext()) */
				for (Sensor s : leaves) {
					// Connect it to it's parent. Because it's a leaf, it's
					// parent is it's neighbour.
					Set<SensorEdge> edgesOfS = minSpanTree.edgesOf(s);
					// This should be an unnecessary check because the graph
					// should be connected
					if (edgesOfS.iterator().hasNext()) {
						SensorEdge tempEdge = edgesOfS.iterator().next();
						if (tempEdge.getSource().equals(s)) {
							addEdge(s, tempEdge.getDestination());
							double orientation = angleBetweenTwoSensors(tempEdge.getDestination(), s);
							s.setOrientation(orientation);
						} else if (tempEdge.getDestination().equals(s)) {
							addEdge(s, tempEdge.getSource());
							double orientation = angleBetweenTwoSensors(tempEdge.getSource(), s);						
							s.setOrientation(orientation);
						}
					}
				} /* for(Sensor s : leaves) */
				
				for(SensorEdge e : matching){
					Sensor source = e.getSource();
					Sensor destination = e.getDestination();
					addEdge(source, destination);
					addEdge(destination, source);
					double angleFromSourceToDest = angleBetweenTwoSensors(destination, source);
					double angleFromDestToSource = angleBetweenTwoSensors(source, destination);
					//Orient them so that they see each other at the edge of their vision
					source.setOrientation(angleFromSourceToDest + (anglePhi/2));
					destination.setOrientation(angleFromDestToSource + (anglePhi/2));
					//FIXME: This algorithm orients, then considers neighbours. It would be nice if we could consider neighbours
					//and then orient.
					for(Sensor neighbour : vertices){
						if(!neighbour.equals(source) && !neighbour.equals(destination)){
							if(source.getPosition().distance(neighbour.getPosition()) <= range){
								double sourceNeighbourAngle = angleBetweenTwoSensors(neighbour, source);
								double sourceOrient = source.getOrientation();
								//If adding and subtracting the angles doesn't overlap the origin
								if(((sourceOrient + (anglePhi/2)) <= (2*Math.PI)) && ((sourceOrient - (anglePhi/2)) >= 0)){
									if((sourceNeighbourAngle <= (sourceOrient + (anglePhi/2)))
										&& (sourceNeighbourAngle >= (sourceOrient - (anglePhi/2)))){
										//Add the edge between them
										addEdge(source, neighbour);
									}									
								} else {
									if((sourceOrient + (anglePhi/2)) > (2*Math.PI)){
										if((sourceOrient - (anglePhi/2)) < 0){
											//Unlikely situation. In any case, the sweep is more than 2*PI so just accept it.
											addEdge(source, neighbour);
										} else {
											if(((sourceNeighbourAngle >= (sourceOrient - (anglePhi/2))) 
													&& (sourceNeighbourAngle <= (2*Math.PI)))
												|| ((sourceNeighbourAngle >= 0)
												    && (sourceNeighbourAngle <= ((sourceOrient + (anglePhi/2)) - (2*Math.PI))))){
												addEdge(source, neighbour);
											}
										}
									} else {
										if((sourceOrient - (anglePhi/2)) < 0){
											if(((sourceNeighbourAngle <= (sourceOrient + (anglePhi/2)))
													&& (sourceNeighbourAngle >= 0))
													|| ((sourceNeighbourAngle >= ((2*Math.PI) + (sourceOrient - (anglePhi/2)))))){
												addEdge(source, neighbour);
											}
										}
									}
								} /* if(((sourceOrient + (anglePhi/2)) <= (2*Math.PI)) && ((sourceOrient - (anglePhi/2)) >= 0)) */
							} /* if(source.getPosition().distance(neighbour.getPosition()) <= range) */	
							
							if(destination.getPosition().distance(neighbour.getPosition()) <= range){
								double destinationNeighbourAngle = angleBetweenTwoSensors(neighbour, destination);
								double destinationOrient = destination.getOrientation();
								//If adding and subtracting the angles doesn't overlap the origin
								if(((destinationOrient + (anglePhi/2)) <= (2*Math.PI)) && ((destinationOrient - (anglePhi/2)) >= 0)){
									if((destinationNeighbourAngle <= (destination.getOrientation() + (anglePhi/2)))
										&& (destinationNeighbourAngle >= (destination.getOrientation() - (anglePhi/2)))){
										//Add the edge between them
										addEdge(destination, neighbour);
									}
								} else {
									if((destinationOrient + (anglePhi/2)) > (2*Math.PI)){
										if((destinationOrient - (anglePhi/2)) < 0){
											//Unlikely situation. In any case, the sweep is more than 2*PI so just accept it.
											addEdge(destination, neighbour);
										} else {
											if(((destinationNeighbourAngle >= (destinationOrient - (anglePhi/2))) 
												    && (destinationNeighbourAngle <= (2*Math.PI)))
												|| ((destinationNeighbourAngle >= 0)
													&& (destinationNeighbourAngle <= ((destinationOrient + (anglePhi/2)) - (2*Math.PI))))){
												addEdge(destination, neighbour);
											}
										}
									} else {
										if((destinationOrient - (anglePhi/2)) < 0){
											if(((destinationNeighbourAngle <= (destinationOrient + (anglePhi/2)))
													&& (destinationNeighbourAngle >= 0))
												|| ((destinationNeighbourAngle >= ((2*Math.PI) + (destinationOrient - (anglePhi/2)))))){
												addEdge(destination, neighbour);
											}
										}
									}									
								} /* if(((destinationOrient + (anglePhi/2)) <= (2*Math.PI)) && ((destinationOrient - (anglePhi/2)) >= 0)) */
							} /* if(destination.getPosition().distance(neighbour.getPosition()) <= range) */							
							
						} /* if(!neighbour.equals(source) && !neighbour.equals(destination)) */						
					} /* for(Sensor neighbour : vertices) */
					
				} /* for(SensorEdge e : matching) */
				
			} /* if(bfsIt.hasNext()) */
		} /* if(vertices.iterator().hasNext()) */
		return;
	}

	/**
	 * 
	 * @param to - the vertex we are measuring to
	 * @param from - the vertex we are measuring from
	 * @return the angle between the two vertices expressed as a value between 0 and 2pi
	 */
	private double angleBetweenTwoSensors(Sensor to, Sensor from){
		double heightDiff = to.getPosition().getY() - from.getPosition().getY();
		double strideDiff = to.getPosition().getX() - from.getPosition().getX();
		double result = Math.atan( heightDiff / strideDiff);
		//Correct for quadrant so we get a value between 0 and 2*PI
		if(heightDiff >= 0){
			if(strideDiff < 0){
				//The dest must be in quadrant 2 and result must be negative
				//This will give us an angle between pi/2 and pi
				result = Math.PI + result;
			}
		} else {
			if(strideDiff >= 0){
				//The dest must be in quadrant 4 and the result must be negative
				//This will give us an angle between 3pi/2 and 2pi
				result = (2*Math.PI) + result;
			} else {
				//The dest must be in quadrant 3 and must be positive
				//This will give us an angle between pi and 3pi/2
				result = Math.PI + result;
			}
		}
		return result;
	}

	/**
	 * Returns a set of all edges connecting source vertex to target vertex if
	 * such vertices exist in this graph. If any of the vertices does not exist
	 * or is null, returns null. If both vertices exist but no edges found,
	 * returns an empty set.
	 * 
	 * In undirected graphs, some of the returned edges may have their source
	 * and target vertices in the opposite order. In simple graphs the returned
	 * set is either singleton set or empty set.
	 * 
	 * @Parameters: sourceVertex - source vertex of the edge. targetVertex -
	 *              target vertex of the edge.
	 * @Returns: a set of all edges connecting source vertex to target vertex.
	 */
	public Set<SensorEdge> getAllEdges(Sensor sourceVertex, Sensor targetVertex) {
		Set<SensorEdge> result = null;
		if ((sourceVertex != null) && (targetVertex != null)) {
			if (vertices.contains(sourceVertex)
					&& vertices.contains(targetVertex)) {
				result = new HashSet<SensorEdge>();
				Iterator<SensorEdge> vertexIt = edges.iterator();
				while (vertexIt.hasNext()) {
					SensorEdge myEdge = vertexIt.next();
					if (myEdge.getSource().equals(sourceVertex)
							&& myEdge.getDestination().equals(targetVertex)) {
						result.add(myEdge);
					}
				} /* while(vertexIt.hasNext()) */
			}
		}
		return result;
	}

	/**
	 * Returns an edge connecting source vertex to target vertex if such
	 * vertices and such edge exist in this graph. Otherwise returns null. If
	 * any of the specified vertices is null returns null. Note we'll return the
	 * first we find. In undirected graphs, the returned edge may have its
	 * source and target vertices in the opposite order.
	 * 
	 * @Parameters: sourceVertex - source vertex of the edge. targetVertex -
	 *              target vertex of the edge.
	 * @Returns: an edge connecting source vertex to target vertex.
	 * 
	 */
	public SensorEdge getEdge(Sensor sourceVertex, Sensor targetVertex) {
		if ((sourceVertex != null) && (targetVertex != null)) {
			if (vertices.contains(sourceVertex)
					&& vertices.contains(targetVertex)) {
				Iterator<SensorEdge> vertexIt = edges.iterator();
				while (vertexIt.hasNext()) {
					SensorEdge myEdge = vertexIt.next();
					if (myEdge.getSource().equals(sourceVertex)
							&& myEdge.getDestination().equals(targetVertex)) {
						return myEdge;
					}
				} /* while(vertexIt.hasNext()) */
			}
		}
		return null;
	}

	/**
	 * Returns the edge factory using which this graph creates new edges. The
	 * edge factory is defined when the graph is constructed and must not be
	 * modified.
	 * 
	 * @Returns: the edge factory using which this graph creates new edges.
	 * 
	 */
	public EdgeFactory<Sensor, SensorEdge> getEdgeFactory() {
		return edgeFactory;
	}

	/**
	 * Creates a new edge in this graph, going from the source vertex to the
	 * target vertex, and returns the created edge. Some graphs do not allow
	 * edge-multiplicity. In such cases, if the graph already contains an edge
	 * from the specified source to the specified target, then this method does
	 * not change the graph and returns null.
	 * 
	 * The source and target vertices must already be contained in this graph.
	 * If they are not found in graph IllegalArgumentException is thrown.
	 * 
	 * This method creates the new edge e using this graph's EdgeFactory. For
	 * the new edge to be added e must not be equal to any other edge the graph
	 * (even if the graph allows edge-multiplicity). More formally, the graph
	 * must not contain any edge e2 such that e2.equals(e). If such e2 is found
	 * then the newly created edge e is abandoned, the method leaves this graph
	 * unchanged returns null.
	 * 
	 * @Parameters: sourceVertex - source vertex of the edge. targetVertex -
	 *              target vertex of the edge.
	 * @Returns: The newly created edge if added to the graph, otherwise null.
	 * @Throws: java.lang.IllegalArgumentException - if source or target
	 *          vertices are not found in the graph.
	 *          java.lang.NullPointerException - if any of the specified
	 *          vertices is null.
	 */
	public SensorEdge addEdge(Sensor sourceVertex, Sensor targetVertex) {
		SensorEdge result = null;
		if ((sourceVertex == null) || (targetVertex == null)) {
			throw new NullPointerException();
		}
		if (!vertices.contains(sourceVertex)
				|| !vertices.contains(targetVertex)) {
			throw new IllegalArgumentException();
		}
		SensorEdge toAdd = edgeFactory.createEdge(sourceVertex, targetVertex);
		if (!edges.contains(toAdd)) {
			result = toAdd;
			edges.add(result);
		}
		return result;
	}

	/**
	 * Adds the specified edge to this graph, going from the source vertex to
	 * the target vertex. More formally, adds the specified edge, e, to this
	 * graph if this graph contains no edge e2 such that e2.equals(e). If this
	 * graph already contains such an edge, the call leaves this graph unchanged
	 * and returns false. Some graphs do not allow edge-multiplicity. In such
	 * cases, if the graph already contains an edge from the specified source to
	 * the specified target, then this method does not change the graph and
	 * returns false. If the edge was added to the graph, returns true.
	 * 
	 * The source and target vertices must already be contained in this graph.
	 * If they are not found in graph IllegalArgumentException is thrown.
	 * 
	 * @Parameters: sourceVertex - source vertex of the edge. targetVertex -
	 *              target vertex of the edge. e - edge to be added to this
	 *              graph.
	 * @Returns: true if this graph did not already contain the specified edge.
	 * @Throws: java.lang.IllegalArgumentException - if source or target
	 *          vertices are not found in the graph.
	 *          java.lang.ClassCastException - if the specified edge is not
	 *          assignment compatible with the class of edges produced by the
	 *          edge factory of this graph. java.lang.NullPointerException - if
	 *          any of the specified vertices is null.
	 */
	public boolean addEdge(Sensor sourceVertex, Sensor targetVertex,
			SensorEdge e) {
		boolean result = false;
		if ((sourceVertex == null) || (targetVertex == null) || (e == null)) {
			throw new NullPointerException();
		}
		if (!vertices.contains(sourceVertex)
				|| !vertices.contains(targetVertex)
				|| !sourceVertex.equals(e.getSource())
				|| !targetVertex.equals(e.getDestination())) {
			throw new IllegalArgumentException();
		}
		if (!(e instanceof SensorEdge)) {
			// FIXME Not sure if I'm understanding this rule right...
			// Are we supposed to treat e like a pointer, assign it to what the
			// factory returns and return it indirectly?
			throw new java.lang.ClassCastException();
		}
		if (!edges.contains(e)) {
			result = edges.add(e);
		}
		return result;
	}

	/**
	 * Adds the specified vertex to this graph if not already present. More
	 * formally, adds the specified vertex, v, to this graph if this graph
	 * contains no vertex u such that u.equals(v). If this graph already
	 * contains such vertex, the call leaves this graph unchanged and returns
	 * false. In combination with the restriction on constructors, this ensures
	 * that graphs never contain duplicate vertices.
	 * 
	 * @Parameters: v - vertex to be added to this graph.
	 * @Returns: true if this graph did not already contain the specified
	 *           vertex.
	 * @Throws: java.lang.NullPointerException - if the specified vertex is
	 *          null.
	 */
	public boolean addVertex(Sensor v) {
		boolean result = false;
		if (v == null) {
			throw new NullPointerException();
		}
		// I know I don't need to check contains because the add does it for me,
		// but it doesn't really hurt to make sure
		if (!vertices.contains(v)) {
			result = vertices.add(v);
		}
		return result;
	}

	/**
	 * Returns true if and only if this graph contains an edge going from the
	 * source vertex to the target vertex. In undirected graphs the same result
	 * is obtained when source and target are inverted. If any of the specified
	 * vertices does not exist in the graph, or if is null, returns false.
	 * 
	 * @Parameters: sourceVertex - source vertex of the edge. targetVertex -
	 *              target vertex of the edge.
	 * @Returns: true if this graph contains the specified edge.
	 */
	public boolean containsEdge(Sensor sourceVertex, Sensor targetVertex) {
		boolean result = false;
		if (!(sourceVertex == null) && !(targetVertex == null)) {
			if (vertices.contains(sourceVertex)
					&& vertices.contains(targetVertex)) {
				// TODO Don't know if this way will work or if we need to
				// iterate through the edges like before
				SensorEdge tempA = edgeFactory.createEdge(sourceVertex,
						targetVertex);
				if (edges.contains(tempA)) {
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * Returns true if this graph contains the specified edge. More formally,
	 * returns true if and only if this graph contains an edge e2 such that
	 * e.equals(e2). If the specified edge is null returns false.
	 * 
	 * @Parameters: e - edge whose presence in this graph is to be tested.
	 * @Returns: true if this graph contains the specified edge.
	 */
	public boolean containsEdge(SensorEdge e) {
		boolean result = false;
		if (e != null) {
			result = edges.contains(e);
		}
		return result;
	}

	/**
	 * Returns true if this graph contains the specified vertex. More formally,
	 * returns true if and only if this graph contains a vertex u such that
	 * u.equals(v). If the specified vertex is null returns false.
	 * 
	 * @Parameters: v - vertex whose presence in this graph is to be tested.
	 * @Returns: true if this graph contains the specified vertex.
	 */
	public boolean containsVertex(Sensor v) {
		boolean result = false;
		if (v != null) {
			result = vertices.contains(v);
		}
		return result;
	}

	/**
	 * Returns a set of the edges contained in this graph. The set is backed by
	 * the graph, so changes to the graph are reflected in the set. If the graph
	 * is modified while an iteration over the set is in progress, the results
	 * of the iteration are undefined.
	 * 
	 * The graph implementation may maintain a particular set ordering (e.g. via
	 * LinkedHashSet) for deterministic iteration, but this is not required. It
	 * is the responsibility of callers who rely on this behavior to only use
	 * graph implementations which support it.
	 * 
	 * @Returns: a set of the edges contained in this graph.
	 */
	public Set<SensorEdge> edgeSet() {
		// FIXME I'm not sure what it means to be backed by the set. I'm just
		// going to return 'edges'...
		return edges;
	}

	/**
	 * Returns a set of all edges touching the specified vertex. If no edges are
	 * touching the specified vertex returns an empty set.
	 * 
	 * @Parameters: vertex - the vertex for which a set of touching edges is to
	 *              be returned.
	 * @Returns: a set of all edges touching the specified vertex.
	 * @Throws: java.lang.IllegalArgumentException - if vertex is not found in
	 *          the graph. java.lang.NullPointerException - if vertex is null.
	 */
	public Set<SensorEdge> edgesOf(Sensor vertex) {
		if (vertex == null) {
			throw new NullPointerException();
		}
		if (!vertices.contains(vertex)) {
			throw new IllegalArgumentException();
		}
		Set<SensorEdge> result = new HashSet<SensorEdge>();
		Iterator<SensorEdge> vertexIt = edges.iterator();
		while (vertexIt.hasNext()) {
			SensorEdge myEdge = vertexIt.next();
			if (myEdge.getSource().equals(vertex)
					|| myEdge.getDestination().equals(vertex)) {
				result.add(myEdge);
			}
		} /* while(vertexIt.hasNext()) */
		return result;
	}

	/**
	 * Removes all the edges in this graph that are also contained in the
	 * specified edge collection. After this call returns, this graph will
	 * contain no edges in common with the specified edges. This method will
	 * invoke the removeEdge(Object) method.
	 * 
	 * @Parameters: edges - edges to be removed from this graph.
	 * @Returns: true if this graph changed as a result of the call
	 * @Throws: java.lang.NullPointerException - if the specified edge
	 *          collection is null.
	 * @See Also: removeEdge(Object), containsEdge(Object)
	 */
	public boolean removeAllEdges(Collection<? extends SensorEdge> disjoinEdges) {
		boolean result = false;
		if (disjoinEdges == null) {
			throw new NullPointerException();
		}
		Iterator<? extends SensorEdge> edgeIt = disjoinEdges.iterator();
		while (edgeIt.hasNext()) {
			SensorEdge nextEdge = edgeIt.next();
			if (containsEdge(nextEdge)) {
				if (removeEdge(nextEdge)) {
					result = true;
				}
			}
		} /* while(edgeIt()) */

		return result;
	}

	/**
	 * Removes all the edges going from the specified source vertex to the
	 * specified target vertex, and returns a set of all removed edges. Returns
	 * null if any of the specified vertices does not exist in the graph. If
	 * both vertices exist but no edge is found, returns an empty set. This
	 * method will either invoke the removeEdge(Object) method, or the
	 * removeEdge(Object, Object) method.
	 * 
	 * @Parameters: sourceVertex - source vertex of the edge. targetVertex -
	 *              target vertex of the edge.
	 * @Returns: the removed edges, or null if no either vertex not part of
	 *           graph
	 */
	public Set<SensorEdge> removeAllEdges(Sensor sourceVertex,
			Sensor targetVertex) {
		Set<SensorEdge> result = null;
		if ((sourceVertex != null) && (targetVertex != null)
				&& containsVertex(sourceVertex) && containsVertex(targetVertex)) {
			result = new HashSet<SensorEdge>();
			SensorEdge temp = null;
			do {
				temp = removeEdge(sourceVertex, targetVertex);
				if (temp != null) {
					result.add(temp);
				}
			} while (temp != null);
		}
		return result;
	}

	/**
	 * Removes all the vertices in this graph that are also contained in the
	 * specified vertex collection. After this call returns, this graph will
	 * contain no vertices in common with the specified vertices. This method
	 * will invoke the removeVertex(Object) method.
	 * 
	 * @Parameters: vertices - vertices to be removed from this graph.
	 * @Returns: true if this graph changed as a result of the call
	 * @Throws: java.lang.NullPointerException - if the specified vertex
	 *          collection is null.
	 * @See Also: removeVertex(Object), containsVertex(Object)
	 */
	public boolean removeAllVertices(
			Collection<? extends Sensor> disjoinVertices) {
		boolean result = false;
		if (disjoinVertices == null) {
			throw new NullPointerException();
		}
		Iterator<? extends Sensor> vtxIt = disjoinVertices.iterator();
		while (vtxIt.hasNext()) {
			Sensor nextSensor = vtxIt.next();
			if (containsVertex(nextSensor)) {
				if (removeVertex(nextSensor)) {
					result = true;
				}
			}
		} /* while(edgeIt()) */
		return result;
	}

	/**
	 * Removes an edge going from source vertex to target vertex, if such
	 * vertices and such edge exist in this graph. Returns the edge if removed
	 * or null otherwise.
	 * 
	 * @Parameters: sourceVertex - source vertex of the edge. targetVertex -
	 *              target vertex of the edge.
	 * @Returns: The removed edge, or null if no edge removed.
	 */
	public SensorEdge removeEdge(Sensor sourceVertex, Sensor targetVertex) {
		SensorEdge result = null;
		if ((sourceVertex != null) && (targetVertex != null)) {
			result = getEdge(sourceVertex, targetVertex);
			if (result != null) {
				removeEdge(result);
			}
		}
		return result;
	}

	/**
	 * Removes the specified edge from the graph. Removes the specified edge
	 * from this graph if it is present. More formally, removes an edge e2 such
	 * that e2.equals(e), if the graph contains such edge. Returns true if the
	 * graph contained the specified edge. (The graph will not contain the
	 * specified edge once the call returns).
	 * 
	 * If the specified edge is null returns false.
	 * 
	 * @Parameters: e - edge to be removed from this graph, if present.
	 * @Returns: true if and only if the graph contained the specified edge.
	 */
	public boolean removeEdge(SensorEdge e) {
		boolean result = false;
		if (e != null) {
			// This check is probably unnecessary because the remove function
			// does it for us, but better to do it.
			if (containsEdge(e)) {
				// This should set it to true
				result = edges.remove(e);
			}
		}
		return result;
	}

	/**
	 * Removes the specified vertex from this graph including all its touching
	 * edges if present. More formally, if the graph contains a vertex u such
	 * that u.equals(v), the call removes all edges that touch u and then
	 * removes u itself. If no such u is found, the call leaves the graph
	 * unchanged. Returns true if the graph contained the specified vertex. (The
	 * graph will not contain the specified vertex once the call returns).
	 * 
	 * If the specified vertex is null returns false.
	 * 
	 * @Parameters: v - vertex to be removed from this graph, if present.
	 * @Returns: true if the graph contained the specified vertex; false
	 *           otherwise.
	 */
	public boolean removeVertex(Sensor v) {
		boolean result = false;
		if (v != null) {
			if (containsVertex(v)) {
				Iterator<SensorEdge> edgeIt = edges.iterator();
				while (edgeIt.hasNext()) {
					SensorEdge myEdge = edgeIt.next();
					if (myEdge.getSource().equals(v)
							|| myEdge.getDestination().equals(v)) {
						removeEdge(myEdge);
					}
				} /* while(vertexIt.hasNext()) */
				result = vertices.remove(v);
			}
		}
		return result;
	}

	/**
	 * Returns a set of the vertices contained in this graph. The set is backed
	 * by the graph, so changes to the graph are reflected in the set. If the
	 * graph is modified while an iteration over the set is in progress, the
	 * results of the iteration are undefined.
	 * 
	 * The graph implementation may maintain a particular set ordering (e.g. via
	 * LinkedHashSet) for deterministic iteration, but this is not required. It
	 * is the responsibility of callers who rely on this behavior to only use
	 * graph implementations which support it.
	 * 
	 * @Returns: a set view of the vertices contained in this graph.
	 */
	public Set<Sensor> vertexSet() {
		// FIXME I'm not sure what it means to be backed by the set. I'm just
		// going to return 'vertices'...
		return vertices;
	}

	/**
	 * Returns the source vertex of an edge. For an undirected graph, source and
	 * target are distinguishable designations (but without any mathematical
	 * meaning).
	 * 
	 * @Parameters: e - edge of interest
	 * @Returns: source vertex
	 */
	public Sensor getEdgeSource(SensorEdge e) {
		return e.getSource();
	}

	/**
	 * Returns the target vertex of an edge. For an undirected graph, source and
	 * target are distinguishable designations (but without any mathematical
	 * meaning).
	 * 
	 * @Parameters: e - edge of interest
	 * @Returns: target vertex
	 */
	public Sensor getEdgeTarget(SensorEdge e) {
		return e.getDestination();
	}

	/**
	 * Returns the weight assigned to a given edge. Unweighted graphs return 1.0
	 * (as defined by WeightedGraph.DEFAULT_EDGE_WEIGHT), allowing
	 * weighted-graph algorithms to apply to them where meaningful.
	 * 
	 * @Parameters: e - edge of interest
	 * @Returns: edge weight
	 * @See Also: WeightedGraph
	 */
	public double getEdgeWeight(SensorEdge e) {
		return WeightedGraph.DEFAULT_EDGE_WEIGHT;
	}

	/**
	 * Returns the "in degree" of the specified vertex. An in degree of a vertex
	 * in a directed graph is the number of inward directed edges from that
	 * vertex. See http://mathworld.wolfram.com/Indegree.html.
	 * 
	 * @Parameters: vertex - vertex whose degree is to be calculated.
	 * @Returns: the degree of the specified vertex.
	 */
	public int inDegreeOf(Sensor vertex) {
		int count = 0;
		// Doesn't say to do this check, but better than not...
		if (vertex != null) {
			if (containsVertex(vertex)) {
				Iterator<SensorEdge> edgeIt = edges.iterator();
				while (edgeIt.hasNext()) {
					SensorEdge myEdge = edgeIt.next();
					// If it's the target
					if (myEdge.getDestination().equals(vertex)) {
						++count;
					}
				} /* while(vertexIt.hasNext()) */
			}
		}
		return count;
	}

	/**
	 * Returns a set of all edges incoming into the specified vertex.
	 * 
	 * @Parameters: vertex - the vertex for which the list of incoming edges to
	 *              be returned.
	 * @Returns: a set of all edges incoming into the specified vertex.
	 */
	public Set<SensorEdge> incomingEdgesOf(Sensor vertex) {
		// Doesn't say to return null if it doesn't exist or whatever, so we'll
		// return empty
		Set<SensorEdge> result = new HashSet<SensorEdge>();
		if (vertex != null) {
			if (containsVertex(vertex)) {
				Iterator<SensorEdge> edgeIt = edges.iterator();
				while (edgeIt.hasNext()) {
					SensorEdge myEdge = edgeIt.next();
					// If it's the target
					if (myEdge.getDestination().equals(vertex)) {
						result.add(myEdge);
					}
				} /* while(vertexIt.hasNext()) */
			}
		}
		return result;
	}

	/**
	 * Returns the "out degree" of the specified vertex. An out degree of a
	 * vertex in a directed graph is the number of outward directed edges from
	 * that vertex. See http://mathworld.wolfram.com/Outdegree.html.
	 * 
	 * @Parameters: vertex - vertex whose degree is to be calculated.
	 * @Returns: the degree of the specified vertex.
	 */
	public int outDegreeOf(Sensor vertex) {
		int count = 0;
		// Doesn't say to do this check, but better than not...
		if (vertex != null) {
			if (containsVertex(vertex)) {
				Iterator<SensorEdge> edgeIt = edges.iterator();
				while (edgeIt.hasNext()) {
					SensorEdge myEdge = edgeIt.next();
					// If it's the source
					if (myEdge.getSource().equals(vertex)) {
						++count;
					}
				} /* while(vertexIt.hasNext()) */
			}
		}
		return count;
	}

	/**
	 * Returns a set of all edges outgoing from the specified vertex.
	 * 
	 * @Parameters: vertex - the vertex for which the list of outgoing edges to
	 *              be returned.
	 * @Returns: a set of all edges outgoing from the specified vertex.
	 */
	public Set<SensorEdge> outgoingEdgesOf(Sensor vertex) {
		// Doesn't say to return null if it doesn't exist or whatever, so we'll
		// return empty
		Set<SensorEdge> result = new HashSet<SensorEdge>();
		if (vertex != null) {
			if (containsVertex(vertex)) {
				Iterator<SensorEdge> edgeIt = edges.iterator();
				while (edgeIt.hasNext()) {
					SensorEdge myEdge = edgeIt.next();
					// If it's the source
					if (myEdge.getSource().equals(vertex)) {
						result.add(myEdge);
					}
				} /* while(vertexIt.hasNext()) */
			}
		}
		return result;
	}

}
