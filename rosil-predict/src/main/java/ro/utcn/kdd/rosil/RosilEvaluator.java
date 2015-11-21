package ro.utcn.kdd.rosil;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.DirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.input.Word;
import ro.utcn.kdd.rosil.input.WordsReader;
import ro.utcn.kdd.rosil.predict.match.MatchedPattern;
import ro.utcn.kdd.rosil.predict.match.PatternMatcher;
import ro.utcn.kdd.rosil.predict.match.PatternMatcherImpl;
import ro.utcn.kdd.rosil.eval.metric.BalancedSplittingPointsCountEvaluator;
import ro.utcn.kdd.rosil.pattern.Pattern;
import ro.utcn.kdd.rosil.pattern.PatternFinder;
import ro.utcn.kdd.rosil.predict.chain.MatchedPatternChain;
import ro.utcn.kdd.rosil.predict.chain.MatchedPatternChainFinder;
import ro.utcn.kdd.rosil.predict.graph.PatternGraphBuilder;
import ro.utcn.kdd.rosil.predict.graph.IsolatedIntermediaryNodesCleaner;
import ro.utcn.kdd.rosil.predict.strategy.PathCountBasedSplittingStrategy;
import ro.utcn.kdd.rosil.predict.strategy.PathLengthBasedSplittingStrategy;
import ro.utcn.kdd.rosil.predict.strategy.PathOverlappingBasedSplittingStrategy;
import ro.utcn.kdd.rosil.predict.strategy.SplittingStrategy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.collect.ContiguousSet.create;
import static com.google.common.collect.DiscreteDomain.integers;
import static com.google.common.collect.Range.closedOpen;
import static java.lang.String.format;
import static java.nio.file.Paths.get;
import static java.util.Collections.singletonList;

public class RosilEvaluator {
    protected static final Logger LOGGER = LoggerFactory.getLogger(RosilEvaluator.class);

    public static void main(String[] args) throws Exception {
        new RosilEvaluator().run();
    }

    private void run() throws IOException {
        final Path trainingData = get("data/english_complete/train.txt");
        final Path testData = get("data/english_complete/test.txt");
        final List<Word> testWords = extractSomeWords(testData, 5000);
        //evaluateSupportInfluence(trainingData, testWords, new PathLengthBasedSplittingStrategy());
        //evaluateSupportInfluence(trainingData, testWords, new PathCountBasedSplittingStrategy());
        evaluateSupportInfluence(trainingData, testWords, new PathOverlappingBasedSplittingStrategy());
    }

    private void evaluateSupportInfluence(Path trainingData, List<Word> testWords, SplittingStrategy predictor) throws IOException {
        for (int i = 50; i > 0; i -= 5) {
            evaluateWithSupport(trainingData, testWords, predictor, i);
        }
        evaluateWithSupport(trainingData, testWords, predictor, 2);
    }

    private void evaluateWithSupport(Path trainingData, List<Word> testWords, SplittingStrategy predictor, int i) throws IOException {
        LOGGER.info(predictor.getClass().getSimpleName() + "(sup = " + i + ")");
        long startTime = System.currentTimeMillis();
        for (int j = 0; j < 4; j++) {
            final List<Pattern> patterns = new PatternFinder().find(i, trainingData);
        }
        LOGGER.info(String.format("Done in %ss", (System.currentTimeMillis() - startTime) / (1000.0 * 4)));
/*        for (int j = 0; j < 5; j++) {
            evaluate(testWords, patterns, predictor);
        }*/
    }


    private void evaluate(List<Word> testWords, List<Pattern> patterns, SplittingStrategy predictor)
            throws IOException {
        final Multimap<Double, Pair<Word, Word>> evaluations = Multimaps.synchronizedMultimap(HashMultimap.create());
        final AtomicInteger evaluatedCount = new AtomicInteger(0);
        testWords.parallelStream().forEach(someWord -> {
            final Word prediction = split(patterns, someWord.toString(), predictor);
            if (prediction != null) {
                final BalancedSplittingPointsCountEvaluator evaluator = new BalancedSplittingPointsCountEvaluator();
                final double evaluationResult = evaluator.evaluate(someWord, prediction);
                evaluations.put(evaluationResult, Pair.of(someWord, prediction));
/*
                LOGGER.trace(format("[%s, %s]->%f", someWord.toSyllabifiedString(), prediction.toSyllabifiedString(),
                        evaluationResult));
                final int currentCount = evaluatedCount.incrementAndGet();
                if (currentCount % (testWords.size() / 100) == 0) {
                    LOGGER.info(format("evaluated %s words.", currentCount));
                }
*/
            } else {
//                LOGGER.info(format("[%s, %s]->%f", someWord.toSyllabifiedString(), "(unable to split)", 0.0));
            }
        });
        final int numberOfEvaluations = evaluations.size();
/*
        evaluations.keySet().stream().sorted().forEach(result -> {
            final int countForResult = evaluations.get(result).size();
            LOGGER.info(format("%.3f -> %4d (%.2f%%)", result, countForResult,
                    (double) countForResult / numberOfEvaluations * 100));
        });
*/
        final Double sum = evaluations.entries().stream().map(Map.Entry::getKey).reduce((r1, r2) -> r1 + r2).get();
/*        LOGGER.info(format("avg = %s", sum / numberOfEvaluations));
        LOGGER.info(format("unsplitted = %s", testWords.size() - numberOfEvaluations));*/
    }

    private List<Word> extractSomeWords(Path source, int howMany) throws IOException {
        final List<Word> words = new WordsReader().read(source);
        final Random randomGenerator = new Random(0);
        return create(closedOpen(0, howMany), integers()).stream()
                .map(unused -> words.get(randomGenerator.nextInt(words.size())))
                .collect(Collectors.toCollection(LinkedList::new));
    }


    private Word split(List<Pattern> patterns, String word, SplittingStrategy predictor) {
        final PatternMatcher matcher = new PatternMatcherImpl(patterns);
        final PatternGraphBuilder patternGraphBuilder = new PatternGraphBuilder();
        final List<MatchedPattern> matchedPatterns = matcher.match(word);
        final Optional<MatchedPattern> potentialCompletePattern =
                matchedPatterns.stream().filter(p -> p.type == MatchedPattern.Type.COMPLETE).findFirst();
        if (potentialCompletePattern.isPresent()) {
            return new MatchedPatternChain(singletonList(potentialCompletePattern.get())).toWord();
        }
        final DirectedGraph<MatchedPattern, String> patternGraph = patternGraphBuilder.build(matchedPatterns);
        final DirectedGraph<MatchedPattern, String> cleanedGraph =
                new IsolatedIntermediaryNodesCleaner().transform(patternGraph);
        final List<MatchedPatternChain> chains = new MatchedPatternChainFinder().allFor(cleanedGraph);
        final MatchedPatternChain best = predictor.best(chains);
        return best != null ? best.toWord() : null;
    }
}