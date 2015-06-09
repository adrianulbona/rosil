package ro.utcn.kdd.rosil.predict;

import org.jgrapht.DirectedGraph;

public interface GraphTransformer<V, E> {

	DirectedGraph<V, E> transform(DirectedGraph<V, E> graph);
}
