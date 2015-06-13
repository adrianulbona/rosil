package ro.utcn.kdd.rosil;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.DirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.io.PatternGraphViewer;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.google.common.collect.ContiguousSet.create;
import static com.google.common.collect.DiscreteDomain.integers;
import static com.google.common.collect.Range.closedOpen;
import static java.lang.String.format;
import static java.nio.file.Paths.get;

public class SilBIDE {
	protected static final Logger LOGGER = LoggerFactory.getLogger(SilBIDE.class);

	public static void main(String[] args) throws Exception {
		final int minSupport = 10;
		final Path trainingData = get("data/s50/s50_0_words_all.txt");
		final Path testData = get("data/s50/s50_1_words_all.txt");
		final List<Pattern> patterns = new PatternFinder().find(minSupport, trainingData);
		splitSomeWords(patterns);
		//test(testData, 10000, patterns, new PathOverlappingBasedSplittingPredictor());
		//longEvaluation();
	}


	public static void longEvaluation() throws IOException {
		final Path trainingData = get("data/s50/s50_0_words_all.txt");
		final Path testData = get("data/s50/s50_1_words_all.txt");
		final LinkedList<Word> testWords = extractSomeWords(testData, 10000);
		for (int minSupport = 50; minSupport > 0; minSupport -= 5) {
			LOGGER.info("Starting evaluation for minsupport " + minSupport + ".");
			final List<Pattern> patterns = new PatternFinder().find(minSupport, trainingData);

			LOGGER.info("Paths count evaluation.");
			test(testWords, patterns, new PathCountBasedSplittingPredictor());
			LOGGER.info("Min. path evaluation.");
			test(testWords, patterns, new PathLengthBasedSplittingPredictor());
			LOGGER.info("Max. overlapping evaluation.");
			test(testWords, patterns, new PathOverlappingBasedSplittingPredictor());
		}
	}

	private static void test(LinkedList<Word> testWords, List<Pattern> patterns, SplittingPredictor predictor)
			throws IOException {
		final Multimap<Double, Pair<Word, Word>> evaluations = HashMultimap.create();
		testWords.forEach(someWord -> {
			final Word prediction = split(patterns, someWord.toString(), predictor);
			if (prediction != null) {
				final BalancedSplittingPointsCountEvaluator evaluator = new BalancedSplittingPointsCountEvaluator();
				final double evaluationResult = evaluator.evaluate(someWord, prediction);
				evaluations.put(evaluationResult, Pair.of(someWord, prediction));
				LOGGER.trace(format("[%s, %s]->%f", someWord.toSyllabifiedString(), prediction.toSyllabifiedString(),
						evaluationResult));
			}
			else {
				LOGGER.trace(format("[%s, %s]->%f", someWord.toSyllabifiedString(), "(unable to split)", 0.0));
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

	private static LinkedList<Word> extractSomeWords(Path source, int howMany) throws IOException {
		final List<Word> words = new WordsReader().read(source);
		final Random randomGenerator = new Random(0);
		return create(closedOpen(0, howMany), integers()).stream()
				.map(unused -> words.get(randomGenerator.nextInt(words.size())))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	private static void splitSomeWords(List<Pattern> patterns) {
		showPatternGraph(patterns, "libelula");
		showPatternGraph(patterns, "împărat");
		showPatternGraph(patterns, "soare");
		showPatternGraph(patterns, "graunte");
		showPatternGraph(patterns, "curcubete");
		showPatternGraph(patterns, "genealogie");
		showPatternGraph(patterns, "informatica");
		showPatternGraph(patterns, "programator");
		showPatternGraph(patterns, "camaraderie");
		showPatternGraph(patterns, "copilandru");
		showPatternGraph(patterns, "basculanta");
		showPatternGraph(patterns, "locomotiva");
		showPatternGraph(patterns, "epilepsie");
		showPatternGraph(patterns, "vocabular");
		showPatternGraph(patterns, "elicopter");
		showPatternGraph(patterns, "aglutinare");
		showPatternGraph(patterns, "usturoi");
		showPatternGraph(patterns, "castravete");
		showPatternGraph(patterns, "gunoier");
		showPatternGraph(patterns, "moșneag");
	}

	private static void showPatternGraph(List<Pattern> patterns, String word) {
		final PatternMatcher matcher = new PatternMatcherImpl(patterns);
		final PatternGraphBuilder patternGraphBuilder = new PatternGraphBuilder();
		final List<MatchedPattern> matchedPatterns = matcher.match(word);
		final DirectedGraph<MatchedPattern, String> patternGraph = patternGraphBuilder.build(matchedPatterns);
		new PatternGraphViewer().showGraphAndWait(patternGraph);
		final DirectedGraph<MatchedPattern, String> cleanedGraph =
				new IsolatedIntermediaryNodesCleaner().transform(patternGraph);
		new PatternGraphViewer().showGraphAndWait(cleanedGraph);
		final List<MatchedPatternChain> chains = new MatchedPatternChainFinder().allFor(cleanedGraph);
		final MatchedPatternChain bestPathCount = new PathCountBasedSplittingPredictor().best(chains);
		if (bestPathCount != null) {
			LOGGER.info("count" + bestPathCount.toWord().toSyllabifiedString());
		}
		final MatchedPatternChain bestPathLength = new PathLengthBasedSplittingPredictor().best(chains);
		if (bestPathLength != null) {
			LOGGER.info("length: " + bestPathLength.toWord().toSyllabifiedString());
		}
		final MatchedPatternChain bestOverlapping = new PathOverlappingBasedSplittingPredictor().best(chains);
		if (bestOverlapping != null) {
			LOGGER.info("length: " + bestOverlapping.toWord().toSyllabifiedString());
		}
	}


	private static Word split(List<Pattern> patterns, String word, SplittingPredictor predictor) {
		final PatternMatcher matcher = new PatternMatcherImpl(patterns);
		final PatternGraphBuilder patternGraphBuilder = new PatternGraphBuilder();
		final List<MatchedPattern> matchedPatterns = matcher.match(word);
		final DirectedGraph<MatchedPattern, String> patternGraph = patternGraphBuilder.build(matchedPatterns);
		final DirectedGraph<MatchedPattern, String> cleanedGraph =
				new IsolatedIntermediaryNodesCleaner().transform(patternGraph);
		final List<MatchedPatternChain> chains = new MatchedPatternChainFinder().allFor(cleanedGraph);
		final MatchedPatternChain best = predictor.best(chains);
		return best != null ? best.toWord() : null;
	}
}