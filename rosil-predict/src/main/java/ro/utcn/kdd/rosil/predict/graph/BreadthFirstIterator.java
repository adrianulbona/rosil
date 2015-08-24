package ro.utcn.kdd.rosil.predict.graph;

import org.jgrapht.Graph;
import org.jgrapht.traverse.CrossComponentIterator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

public class BreadthFirstIterator<V, E> extends CrossComponentIterator<V, E, Object> {

	private Deque<V> queue = new ArrayDeque<V>();

	private Map<V, V> parents = new LinkedHashMap<>();

	private V lastProvidedVertex = null;

	/**
	 * Creates a new breadth-first iterator for the specified graph. Iteration
	 * will start at the specified start vertex and will be limited to the
	 * connected component that includes that vertex. If the specified start
	 * vertex is <code>null</code>, iteration will start at an arbitrary vertex
	 * and will not be limited, that is, will be able to traverse all the graph.
	 *
	 * @param g           the graph to be iterated.
	 * @param startVertex the vertex iteration to be started.
	 */
	public BreadthFirstIterator(Graph<V, E> g, V startVertex) {
		super(g, startVertex);
	}

	/**
	 * @see CrossComponentIterator#isConnectedComponentExhausted()
	 */
	@Override
	protected boolean isConnectedComponentExhausted() {
		return queue.isEmpty();
	}

	/**
	 * @see CrossComponentIterator#encounterVertex(Object, Object)
	 */
	@Override
	protected void encounterVertex(V vertex, E edge) {
		putSeenData(vertex, null);
		if (lastProvidedVertex == null) {
			parents.put(vertex, vertex);
		}
		else {
			parents.put(vertex, lastProvidedVertex);
		}
		queue.add(vertex);
	}

	/**
	 * @see CrossComponentIterator#encounterVertexAgain(Object, Object)
	 */
	@Override
	protected void encounterVertexAgain(V vertex, E edge) {
	}

	/**
	 * @see CrossComponentIterator#provideNextVertex()
	 */
	@Override
	protected V provideNextVertex() {
		final V vertexToBeProvided = queue.removeFirst();
		lastProvidedVertex = vertexToBeProvided;
		return vertexToBeProvided;
	}

	public Map<V, V> getParents() {
		return parents;
	}
}