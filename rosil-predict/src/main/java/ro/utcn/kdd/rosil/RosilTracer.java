package ro.utcn.kdd.rosil;

import org.jgrapht.DirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.eval.PatternGraphViewer;
import ro.utcn.kdd.rosil.predict.match.MatchedPattern;
import ro.utcn.kdd.rosil.predict.match.PatternMatcher;
import ro.utcn.kdd.rosil.predict.match.PatternMatcherImpl;
import ro.utcn.kdd.rosil.pattern.Pattern;
import ro.utcn.kdd.rosil.pattern.PatternFinder;
import ro.utcn.kdd.rosil.predict.chain.MatchedPatternChain;
import ro.utcn.kdd.rosil.predict.chain.MatchedPatternChainFinder;
import ro.utcn.kdd.rosil.predict.graph.PatternGraphBuilder;
import ro.utcn.kdd.rosil.predict.graph.IsolatedIntermediaryNodesCleaner;
import ro.utcn.kdd.rosil.predict.strategy.PathCountBasedSplittingStrategy;
import ro.utcn.kdd.rosil.predict.strategy.PathLengthBasedSplittingStrategy;
import ro.utcn.kdd.rosil.predict.strategy.PathOverlappingBasedSplittingStrategy;

import java.nio.file.Path;
import java.util.List;

import static java.nio.file.Paths.get;

public class RosilTracer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RosilTracer.class);
    private List<Pattern> patterns;

    public RosilTracer() {
        cachePatterns();
    }

    public static void main(String[] args) {
        RosilTracer tracer = new RosilTracer();
        if (args.length == 0) {
            tracer.splitSomeWords();
        } else {
            tracer.splitWord(args[0], true);
        }
    }

    private void cachePatterns() {
        final int minSupport = 10;
        final Path trainingData = get("../data/s50/s50_0_words_no_struct.csv");
        this.patterns = new PatternFinder().find(minSupport, trainingData);
    }

    private void splitSomeWords() {
        splitWord("vocabular", true);
        splitWord("informatica", true);
        splitWord("împărat", true);
        splitWord("soare", true);
        splitWord("graunte", true);
        splitWord("curcubete", true);
        splitWord("genealogie", true);
        splitWord("programator", true);
        splitWord("camaraderie", true);
        splitWord("copilandru", true);
        splitWord("basculanta", true);
        splitWord("locomotiva", true);
        splitWord("epilepsie", true);
        splitWord("aglutinare", true);
        splitWord("usturoi", true);
        splitWord("gunoier", true);
        splitWord("moșneag", true);
        splitWord("castravete", true);
        splitWord("firav", true);
    }

    public RosilSolutions splitWord(String word) {
        return splitWord(word, false);
    }

    private RosilSolutions splitWord(String word, boolean showGraph) {
        final PatternMatcher matcher = new PatternMatcherImpl(patterns);
        final PatternGraphBuilder patternGraphBuilder = new PatternGraphBuilder();
        final List<MatchedPattern> matchedPatterns = matcher.match(word);
        final DirectedGraph<MatchedPattern, String> patternGraph = patternGraphBuilder.build(matchedPatterns);
        //new PatternGraphViewer().showGraphAndWait(patternGraph);
        final DirectedGraph<MatchedPattern, String> cleanedGraph =
                new IsolatedIntermediaryNodesCleaner().transform(patternGraph);
        if (showGraph) {
            new PatternGraphViewer().showGraphAndWait(cleanedGraph);
        }
        final List<MatchedPatternChain> chains = new MatchedPatternChainFinder().allFor(cleanedGraph);

        final MatchedPatternChain bestPathCount = new PathCountBasedSplittingStrategy().best(chains);
        final MatchedPatternChain bestPathLength = new PathLengthBasedSplittingStrategy().best(chains);
        final MatchedPatternChain bestOverlapping = new PathOverlappingBasedSplittingStrategy().best(chains);

        if (bestPathLength != null && bestOverlapping != null && bestPathCount != null) {
            final String overlapping = bestOverlapping.toWord().toSyllabifiedString();
            final String counting = bestPathCount.toWord().toSyllabifiedString();
            final String shortest = bestPathLength.toWord().toSyllabifiedString();
            LOGGER.info("count: " + counting);
            LOGGER.info("length: " + shortest);
            LOGGER.info("overlapping: " + overlapping);
            return new RosilSolutions(shortest, overlapping, counting);
        }
        return null;
    }
}
