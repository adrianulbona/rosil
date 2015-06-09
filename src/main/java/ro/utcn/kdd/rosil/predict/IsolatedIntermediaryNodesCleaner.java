package ro.utcn.kdd.rosil.predict;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import ro.utcn.kdd.rosil.match.MatchedPattern;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static ro.utcn.kdd.rosil.match.MatchedPattern.Type.END;

public class IsolatedIntermediaryNodesCleaner implements GraphTransformer<MatchedPattern, String> {

	@Override
	public DirectedGraph<MatchedPattern, String> transform(DirectedGraph<MatchedPattern, String> graph) {
		final DirectedGraph<MatchedPattern, String> cleanedGraph = new DefaultDirectedGraph<>(String.class);
		Graphs.addGraph(cleanedGraph, graph);
		final Set<MatchedPattern> vertices = new HashSet<>(cleanedGraph.vertexSet());
		final Predicate<MatchedPattern> filter = v -> v.type != END && cleanedGraph.outDegreeOf(v) == 0;
		vertices.stream().filter(filter).forEach(cleanedGraph::removeVertex);
		return cleanedGraph;
	}
}
