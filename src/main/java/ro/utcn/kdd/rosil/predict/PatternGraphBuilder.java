package ro.utcn.kdd.rosil.predict;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import ro.utcn.kdd.rosil.data.MatchedPattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ro.utcn.kdd.rosil.predict.PatternNode.*;

public class PatternGraphBuilder {

    public DirectedGraph<PatternNode, String> build(List<MatchedPattern> matchedPatterns, int wordLength) {
        final DirectedGraph<PatternNode, String> patternGraph = new DefaultDirectedGraph<>(String.class);
        final Map<MatchedPattern, PatternNode> nodes = new HashMap<>();
        for (int firstIndex = 0; firstIndex < matchedPatterns.size() - 1; firstIndex++) {
            final MatchedPattern matchedPattern1 = matchedPatterns.get(firstIndex);
            for (int secondIndex = firstIndex + 1; secondIndex < matchedPatterns.size(); secondIndex++) {
                final MatchedPattern matchedPattern2 = matchedPatterns.get(secondIndex);
                if (matchedPattern1.pattern.elements.size() > 1) {
                    final boolean extendableBefore = matchedPattern2.isExtendableAfterWith(matchedPattern1);
                    final boolean extendableAfter = matchedPattern1.isExtendableAfterWith(matchedPattern2);
                    final boolean subPattern = extendableBefore && extendableAfter;
                    final PatternNode node1 = findNode(nodes, matchedPattern1, wordLength);
                    final PatternNode node2 = findNode(nodes, matchedPattern2, wordLength);
                    if (!subPattern && extendableBefore) {
                        addGraphEdge(patternGraph, node2, node1);
                    }
                    if (!subPattern && extendableAfter) {
                        addGraphEdge(patternGraph, node1, node2);
                    }
                }
            }
        }
        return patternGraph;
    }

    private PatternNode findNode(Map<MatchedPattern, PatternNode> nodes, MatchedPattern pattern, int wordLength) {
        if (nodes.get(pattern) == null) {
            nodes.put(pattern, createNodeFor(pattern, wordLength));
        }
        return nodes.get(pattern);
    }

    private PatternNode createNodeFor(MatchedPattern pattern, int wordLength) {
        if (pattern.startIndex == 0) {
            return createStartNode(pattern);
        }
        if (pattern.endIndex == wordLength) {
            return createStopNode(pattern);
        }
        return createIntermediarNode(pattern);
    }

    private void addGraphEdge(DirectedGraph<PatternNode, String> patternGraph, PatternNode source, PatternNode destination) {
        patternGraph.addVertex(source);
        patternGraph.addVertex(destination);
        final String edge = source.matchedPattern.getRange() + destination.matchedPattern.getRange().toString();
        patternGraph.addEdge(source, destination, edge);
    }
}
