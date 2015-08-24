package ro.utcn.kdd.rosil.predict.chain;

import com.google.common.collect.Lists;
import org.jgrapht.DirectedGraph;
import ro.utcn.kdd.rosil.predict.match.MatchedPattern;
import ro.utcn.kdd.rosil.predict.graph.BreadthFirstIterator;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;
import static ro.utcn.kdd.rosil.predict.match.MatchedPattern.Type.BEGIN;
import static ro.utcn.kdd.rosil.predict.match.MatchedPattern.Type.END;

public class MatchedPatternChainFinder {

	public List<MatchedPatternChain> allFor(DirectedGraph<MatchedPattern, String> patternGraph) {
		final Set<MatchedPattern> vertices = new HashSet<>(patternGraph.vertexSet());
		final Predicate<MatchedPattern> filter = v -> v.type == BEGIN;
		final Set<MatchedPattern> startNodes = vertices.stream().filter(filter).collect(Collectors.toSet());
		final List<MatchedPatternChain> chains = new LinkedList<>();
		startNodes.stream().map(v -> chainsFor(patternGraph, v)).forEach(chains::addAll);
		return chains;
	}

	private List<MatchedPatternChain> chainsFor(DirectedGraph<MatchedPattern, String> graph, MatchedPattern start) {
		final BreadthFirstIterator<MatchedPattern, String> iterator = new BreadthFirstIterator<>(graph, start);
		final List<MatchedPattern> endPatterns = new LinkedList<>();
		while (iterator.hasNext()) {
			final MatchedPattern vertex = iterator.next();
			if (vertex.type == END) {
				endPatterns.add(vertex);
			}
		}
		final Map<MatchedPattern, MatchedPattern> bfsTree = iterator.getParents();
		return endPatterns.stream().map(endPattern -> buildChain(bfsTree, endPattern))
				.collect(toCollection(LinkedList::new));
	}

	private MatchedPatternChain buildChain(Map<MatchedPattern, MatchedPattern> parents, MatchedPattern endPattern) {
		final List<MatchedPattern> path = new LinkedList<>();
		path.add(endPattern);
		MatchedPattern currentPattern = endPattern;
		do {
			currentPattern = parents.get(currentPattern);
			path.add(currentPattern);
		} while (currentPattern != parents.get(currentPattern));
		return new MatchedPatternChain(Lists.reverse(path));
	}

}
