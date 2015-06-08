package ro.utcn.kdd.rosil.predict;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import ro.utcn.kdd.rosil.match.MatchedPattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternGraphBuilder {

    public DirectedGraph<MatchedPattern, String> build(List<MatchedPattern> matchedPatterns, int wordLength) {
        final DirectedGraph<MatchedPattern, String> patternGraph = new DefaultDirectedGraph<>(String.class);
        final Map<MatchedPattern, MatchedPattern> nodes = new HashMap<>();
        for (int firstIndex = 0; firstIndex < matchedPatterns.size() - 1; firstIndex++) {
            final MatchedPattern matchedPattern1 = matchedPatterns.get(firstIndex);
            for (int secondIndex = firstIndex + 1; secondIndex < matchedPatterns.size(); secondIndex++) {
                final MatchedPattern matchedPattern2 = matchedPatterns.get(secondIndex);
                if (matchedPattern1.pattern.elements.size() > 1) {
                    final boolean extendableBefore = matchedPattern2.isExtendableAfterWith(matchedPattern1);
                    final boolean extendableAfter = matchedPattern1.isExtendableAfterWith(matchedPattern2);
                    final boolean subPattern = extendableBefore && extendableAfter;
                    if (!subPattern && extendableBefore) {
                        addGraphEdge(patternGraph, matchedPattern2, matchedPattern1);
                    }
                    if (!subPattern && extendableAfter) {
                        addGraphEdge(patternGraph, matchedPattern1, matchedPattern2);
                    }
                }
            }
        }
        return patternGraph;
    }

    private void addGraphEdge(DirectedGraph<MatchedPattern, String> patternGraph, MatchedPattern source, MatchedPattern destination) {
        patternGraph.addVertex(source);
        patternGraph.addVertex(destination);
        final String edge = source.getRange() + destination.getRange().toString();
        patternGraph.addEdge(source, destination, edge);
    }
}
