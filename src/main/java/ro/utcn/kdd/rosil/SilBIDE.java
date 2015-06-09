package ro.utcn.kdd.rosil;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jgrapht.DirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.match.MatchedPattern;
import ro.utcn.kdd.rosil.match.PatternMatcher;
import ro.utcn.kdd.rosil.match.PatternMatcherImpl;
import ro.utcn.kdd.rosil.pattern.Pattern;
import ro.utcn.kdd.rosil.pattern.PatternFinder;
import ro.utcn.kdd.rosil.predict.IsolatedIntermediaryNodesCleaner;
import ro.utcn.kdd.rosil.predict.MatchedPatternChain;
import ro.utcn.kdd.rosil.predict.MatchedPatternChainFinder;
import ro.utcn.kdd.rosil.predict.PatternGraphBuilder;

import java.nio.file.Path;
import java.util.List;

import static java.nio.file.Paths.get;

public class SilBIDE {
	protected static final Logger LOGGER = LoggerFactory.getLogger(SilBIDE.class);

	public static void main(String[] args) throws Exception {
		final int minSupport = 5;
		final Path wordsPath = get("data/words_all.txt");
		final List<Pattern> patterns = new PatternFinder().find(minSupport, wordsPath);


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

	private static void showPatternGraph(List<Pattern> patterns, String word) {
		final PatternMatcher matcher = new PatternMatcherImpl(patterns);
		final PatternGraphBuilder patternGraphBuilder = new PatternGraphBuilder();
		final List<MatchedPattern> matchedPatterns = matcher.match(word);
		final DirectedGraph<MatchedPattern, String> patternGraph = patternGraphBuilder.build(matchedPatterns);
		//new PatternGraphViewer().showGraphAndWait(patternGraph);
		final DirectedGraph<MatchedPattern, String> cleanedGraph =
				new IsolatedIntermediaryNodesCleaner().transform(patternGraph);
		//new PatternGraphViewer().showGraphAndWait(patternGraph);
		final List<MatchedPatternChain> chains = new MatchedPatternChainFinder().allFor(cleanedGraph);
		final Multimap<String, MatchedPatternChain> counts = aggregatePaths(chains);
		//System.out.println(counts);
		counts.keySet().forEach(splitting -> System.out.println(splitting + " -> " + counts.get(splitting).size()));
	}

	private static Multimap<String, MatchedPatternChain> aggregatePaths(List<MatchedPatternChain> chains) {
		final Multimap<String, MatchedPatternChain> aggregated = HashMultimap.create();
		chains.forEach(path -> aggregated.put(path.toSplitWord(), path));
		return aggregated;
	}


}