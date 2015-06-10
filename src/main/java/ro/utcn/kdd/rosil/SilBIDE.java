package ro.utcn.kdd.rosil;

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

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;
import static java.nio.file.Paths.get;

public class SilBIDE {
	protected static final Logger LOGGER = LoggerFactory.getLogger(SilBIDE.class);

	public static void main(String[] args) throws Exception {
		final int minSupport = 5;
		final Path wordsPath = get("data/words_all.txt");
		final List<Word> words = new WordsReader().read(wordsPath);
		final List<Pattern> patterns = new PatternFinder().find(minSupport, wordsPath);

		final Random randomGenerator = new Random(0);
		for (int i = 0; i < 200; i++) {
			final Word someWord = words.get(randomGenerator.nextInt(words.size() - 1));
			final Word prediction = split(patterns, someWord.toString());
			if (prediction != null) {
				final BalancedSplittingPointsCountEvaluator evaluator = new BalancedSplittingPointsCountEvaluator();
				final double evaluationResult = evaluator.evaluate(someWord, prediction);
				LOGGER.info(format("[%s, %s]->%f", someWord.toSyllabifiedString(), prediction.toSyllabifiedString(),
						evaluationResult));
			}
			else {
				LOGGER.info(format("[%s, %s]->%f", someWord.toSyllabifiedString(), "(unable to split)", 0.0));
			}
		}
		//splitSomeWords(patterns);
	}

	private static void splitSomeWords(List<Pattern> patterns) {
		showPatternGraph(patterns, "libelula");
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
		showPatternGraph(patterns, "împărat");
		showPatternGraph(patterns, "gunoier");
		showPatternGraph(patterns, "moșneag");
	}

	private static Word showPatternGraph(List<Pattern> patterns, String word) {
		final PatternMatcher matcher = new PatternMatcherImpl(patterns);
		final PatternGraphBuilder patternGraphBuilder = new PatternGraphBuilder();
		final List<MatchedPattern> matchedPatterns = matcher.match(word);
		final DirectedGraph<MatchedPattern, String> patternGraph = patternGraphBuilder.build(matchedPatterns);
		//new PatternGraphViewer().showGraphAndWait(patternGraph);
		final DirectedGraph<MatchedPattern, String> cleanedGraph =
				new IsolatedIntermediaryNodesCleaner().transform(patternGraph);
		//new PatternGraphViewer().showGraphAndWait(patternGraph);
		final List<MatchedPatternChain> chains = new MatchedPatternChainFinder().allFor(cleanedGraph);
		final MatchedPatternChain best = new SplittingPredictor(chains).best();
		return best != null ? best.toWord() : null;
	}


	private static Word split(List<Pattern> patterns, String word) {
		final PatternMatcher matcher = new PatternMatcherImpl(patterns);
		final PatternGraphBuilder patternGraphBuilder = new PatternGraphBuilder();
		final List<MatchedPattern> matchedPatterns = matcher.match(word);
		final DirectedGraph<MatchedPattern, String> patternGraph = patternGraphBuilder.build(matchedPatterns);
		final DirectedGraph<MatchedPattern, String> cleanedGraph =
				new IsolatedIntermediaryNodesCleaner().transform(patternGraph);
		final List<MatchedPatternChain> chains = new MatchedPatternChainFinder().allFor(cleanedGraph);
		final MatchedPatternChain best = new SplittingPredictor(chains).best();
		return best != null ? best.toWord() : null;
	}
}