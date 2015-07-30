package ro.utcn.kdd.rosil;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.DirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.io.Word;
import ro.utcn.kdd.rosil.io.WordsReader;
import ro.utcn.kdd.rosil.match.MatchedPattern;
import ro.utcn.kdd.rosil.match.PatternMatcher;
import ro.utcn.kdd.rosil.match.PatternMatcherImpl;
import ro.utcn.kdd.rosil.metric.BalancedSplittingPointsCountEvaluator;
import ro.utcn.kdd.rosil.pattern.Pattern;
import ro.utcn.kdd.rosil.pattern.PatternFinder;
import ro.utcn.kdd.rosil.predict.*;

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
        final Path trainingData = get("data/s50/s50_0_words_no_struct.csv");
        final Path testData = get("data/s50/s50_1_words_no_struct.csv");
        final List<Word> testWords = extractSomeWords(testData, 10000);
        final List<Pattern> patterns = new PatternFinder().find(5, trainingData);
        evaluate(testWords, patterns, new PathLengthBasedSplittingPredictor());
    }


    private void evaluate(List<Word> testWords, List<Pattern> patterns, SplittingPredictor predictor)
            throws IOException {
        final Multimap<Double, Pair<Word, Word>> evaluations = Multimaps.synchronizedMultimap(HashMultimap.create());
        final AtomicInteger evaluatedCount = new AtomicInteger(0);
        testWords.parallelStream().forEach(someWord -> {
            final Word prediction = split(patterns, someWord.toString(), predictor);
            if (prediction != null) {
                final BalancedSplittingPointsCountEvaluator evaluator = new BalancedSplittingPointsCountEvaluator();
                final double evaluationResult = evaluator.evaluate(someWord, prediction);
                evaluations.put(evaluationResult, Pair.of(someWord, prediction));
                LOGGER.trace(format("[%s, %s]->%f", someWord.toSyllabifiedString(), prediction.toSyllabifiedString(),
                        evaluationResult));
                final int currentCount = evaluatedCount.incrementAndGet();
                if (currentCount % (testWords.size() / 100) == 0) {
                    LOGGER.info(format("evaluated %s words.", currentCount));
                }
            } else {
                LOGGER.info(format("[%s, %s]->%f", someWord.toSyllabifiedString(), "(unable to split)", 0.0));
            }
        });
        final int numberOfEvaluations = evaluations.size();
        evaluations.keySet().stream().sorted().forEach(result -> {
            final int countForResult = evaluations.get(result).size();
            LOGGER.info(format("%.3f -> %4d (%.2f%%)", result, countForResult,
                    (double) countForResult / numberOfEvaluations * 100));
        });
        final Double sum = evaluations.entries().stream().map(Map.Entry::getKey).reduce((r1, r2) -> r1 + r2).get();
        LOGGER.info(format("avg = %s", sum / numberOfEvaluations));
        LOGGER.info(format("unsplitted = %s", testWords.size() - numberOfEvaluations));
    }

    private List<Word> extractSomeWords(Path source, int howMany) throws IOException {
        final List<Word> words = new WordsReader().read(source);
        final Random randomGenerator = new Random(0);
        return create(closedOpen(0, howMany), integers()).stream()
                .map(unused -> words.get(randomGenerator.nextInt(words.size())))
                .collect(Collectors.toCollection(LinkedList::new));
    }


    private Word split(List<Pattern> patterns, String word, SplittingPredictor predictor) {
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