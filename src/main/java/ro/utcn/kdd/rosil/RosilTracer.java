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

    public static void main(String[] args) {
        final int minSupport = 100;
        final Path trainingData = get("data/s50/s50_0_words_no_struct.csv");
        final Path testData = get("data/s50/s50_1_words_all.txt");
        final List<Pattern> patterns = new PatternFinder().find(minSupport, trainingData);
        splitSomeWords(patterns);
    }


    private static void splitSomeWords(List<Pattern> patterns) {
        showPatternGraph(patterns, "vocabular");
        showPatternGraph(patterns, "informatica");
        showPatternGraph(patterns, "împărat");
        showPatternGraph(patterns, "soare");
        showPatternGraph(patterns, "graunte");
        showPatternGraph(patterns, "curcubete");
        showPatternGraph(patterns, "genealogie");
        showPatternGraph(patterns, "programator");
        showPatternGraph(patterns, "camaraderie");
        showPatternGraph(patterns, "copilandru");
        showPatternGraph(patterns, "basculanta");
        showPatternGraph(patterns, "locomotiva");
        showPatternGraph(patterns, "epilepsie");
        showPatternGraph(patterns, "aglutinare");
        showPatternGraph(patterns, "usturoi");
        showPatternGraph(patterns, "gunoier");
        showPatternGraph(patterns, "moșneag");
        showPatternGraph(patterns, "castravete");
        showPatternGraph(patterns, "firav");

    }

    private static void showPatternGraph(List<Pattern> patterns, String word) {
        final PatternMatcher matcher = new PatternMatcherImpl(patterns);
        final PatternGraphBuilder patternGraphBuilder = new PatternGraphBuilder();
        final List<MatchedPattern> matchedPatterns = matcher.match(word);
        final DirectedGraph<MatchedPattern, String> patternGraph = patternGraphBuilder.build(matchedPatterns);
        //new PatternGraphViewer().showGraphAndWait(patternGraph);
        final DirectedGraph<MatchedPattern, String> cleanedGraph =
                new IsolatedIntermediaryNodesCleaner().transform(patternGraph);
        new PatternGraphViewer().showGraphAndWait(cleanedGraph);
        final List<MatchedPatternChain> chains = new MatchedPatternChainFinder().allFor(cleanedGraph);
        final MatchedPatternChain bestPathCount = new PathCountBasedSplittingStrategy().best(chains);
        if (bestPathCount != null) {
            LOGGER.info("count: " + bestPathCount.toWord().toSyllabifiedString());
        }
        final MatchedPatternChain bestPathLength = new PathLengthBasedSplittingStrategy().best(chains);
        if (bestPathLength != null) {
            LOGGER.info("length: " + bestPathLength.toWord().toSyllabifiedString());
        }
        final MatchedPatternChain bestOverlapping = new PathOverlappingBasedSplittingStrategy().best(chains);
        if (bestOverlapping != null) {
            LOGGER.info("overlapping: " + bestOverlapping.toWord().toSyllabifiedString());
        }
    }
}
