package ro.utcn.kdd.rosil.predict;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import ro.utcn.kdd.rosil.data.MatchedPattern;
import ro.utcn.kdd.rosil.data.Pattern;

import java.util.List;

public class IndexBasedGraphBuilder {

    public DirectedGraph<Integer, Pattern> build(List<MatchedPattern> matchedPatterns) {
        final DirectedGraph<Integer, Pattern> patternGraph = new DefaultDirectedGraph<>(Pattern.class);
        for (MatchedPattern matchedPattern : matchedPatterns) {
            patternGraph.addVertex(matchedPattern.startIndex);
            patternGraph.addVertex(matchedPattern.endIndex);
            patternGraph.addEdge(matchedPattern.startIndex, matchedPattern.endIndex, matchedPattern.pattern);
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
