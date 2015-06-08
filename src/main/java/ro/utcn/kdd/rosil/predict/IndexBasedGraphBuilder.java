package ro.utcn.kdd.rosil.predict;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import ro.utcn.kdd.rosil.match.MatchedPattern;
import ro.utcn.kdd.rosil.pattern.Pattern;

import java.util.List;

public class IndexBasedGraphBuilder {

    public DirectedGraph<Integer, Pattern> build(List<MatchedPattern> matchedPatterns) {
        final DirectedGraph<Integer, Pattern> patternGraph = new DefaultDirectedGraph<>(Pattern.class);
        for (MatchedPattern matchedPattern : matchedPatterns) {
            final Integer startIndex = matchedPattern.getRange().lowerEndpoint();
            patternGraph.addVertex(startIndex);
            final Integer endIndex = matchedPattern.getRange().upperEndpoint();
            patternGraph.addVertex(endIndex);
            patternGraph.addEdge(startIndex, endIndex, matchedPattern.pattern);
/*            int elementIndex = matchedPattern.startIndex;
            for (String patternElement : matchedPattern.pattern.elements) {
                patternGraph.addVertex(elementIndex);
                final int elementEndIndex = elementIndex + patternElement.length();
                patternGraph.addVertex(elementEndIndex);
                final Pattern subPattern = new Pattern(singletonList(patternElement), matchedPattern.pattern.support);
                patternGraph.addEdge(elementIndex, elementEndIndex, subPattern);
                elementIndex += patternElement.length();
            }*/
        }
        return patternGraph;
    }
}
