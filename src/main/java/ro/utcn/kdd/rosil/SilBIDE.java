package ro.utcn.kdd.rosil;

import org.jgrapht.DirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.data.MatchedPattern;
import ro.utcn.kdd.rosil.data.Pattern;
import ro.utcn.kdd.rosil.io.PatternGraphViewer;
import ro.utcn.kdd.rosil.pattern.PatternFinder;
import ro.utcn.kdd.rosil.predict.PatternGraphBuilder;
import ro.utcn.kdd.rosil.predict.PatternMatcher;
import ro.utcn.kdd.rosil.predict.PatternMatcherImpl;
import ro.utcn.kdd.rosil.predict.PatternNode;

import java.nio.file.Path;
import java.util.List;

import static java.nio.file.Paths.get;

public class SilBIDE {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SilBIDE.class);

    public static void main(String[] args) throws Exception {
        final int minSupport = 10;
        final Path wordsPath = get("data/words_all.txt");
        final List<Pattern> patterns = new PatternFinder().find(minSupport, wordsPath);

        showPatternGraph(patterns, "elicopter");
        showPatternGraph(patterns, "aglutinare");
        showPatternGraph(patterns, "usturoi");
        showPatternGraph(patterns, "castravete");
        showPatternGraph(patterns, "împărat");
        showPatternGraph(patterns, "gunoier");
        showPatternGraph(patterns, "moșneag");
    }

    private static void showPatternGraph(List<Pattern> patterns, String word) {
        final PatternMatcher matcher = new PatternMatcherImpl(patterns);
        final PatternGraphBuilder patternGraphBuilder = new PatternGraphBuilder();
        final List<MatchedPattern> matchedPatterns = matcher.match(word);
        final DirectedGraph<PatternNode, String> patternGraph = patternGraphBuilder.build(matchedPatterns, word.length());
        new PatternGraphViewer().showGraphAndWait(patternGraph);
    }
}