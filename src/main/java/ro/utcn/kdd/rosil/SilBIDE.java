package ro.utcn.kdd.rosil;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import org.jgrapht.DirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.match.MatchedPattern;
import ro.utcn.kdd.rosil.match.PatternMatcher;
import ro.utcn.kdd.rosil.match.PatternMatcherImpl;
import ro.utcn.kdd.rosil.pattern.Pattern;
import ro.utcn.kdd.rosil.pattern.PatternFinder;
import ro.utcn.kdd.rosil.predict.BreadthFirstIterator;
import ro.utcn.kdd.rosil.predict.PatternGraphBuilder;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.collect.Range.closedOpen;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.join;
import static ro.utcn.kdd.rosil.match.MatchedPattern.Type.BEGIN;
import static ro.utcn.kdd.rosil.match.MatchedPattern.Type.END;

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
		removeIsolatedPatterns(patternGraph);
		//new PatternGraphViewer().showGraphAndWait(patternGraph);
		final List<List<MatchedPattern>> allCompletePaths = findAllCompletePaths(patternGraph);
		final Multimap<String, List<MatchedPattern>> aggregatedResults = aggregatePaths(allCompletePaths);
		//System.out.println(aggregatedResults);
		aggregatedResults.keySet()
				.forEach(splitting -> System.out.println(splitting + " -> " + aggregatedResults.get(splitting).size()));
	}

	private static Multimap<String, List<MatchedPattern>> aggregatePaths(List<List<MatchedPattern>> paths) {
		final Multimap<String, List<MatchedPattern>> aggregated = HashMultimap.create();
		paths.forEach(path -> aggregated.put(convertPathToSplittedWord(path), path));
		return aggregated;
	}

	private static String convertPathToSplittedWord(List<MatchedPattern> path) {
		int lastMatchedPathUpperBound = 0;
		final List<String> combinedElements = new LinkedList<>();
		for (MatchedPattern matchedPattern : path) {
			final Integer upperBound = matchedPattern.getRange().upperEndpoint();
			final Range<Integer> choppedRange = closedOpen(lastMatchedPathUpperBound, upperBound);
			combinedElements.addAll(matchedPattern.getElementsForRange(choppedRange));
			lastMatchedPathUpperBound = upperBound;
		}
		return join(combinedElements, "-");
	}

	private static List<List<MatchedPattern>> findAllCompletePaths(DirectedGraph<MatchedPattern, String> patternGraph) {
		final Set<MatchedPattern> vertices = new HashSet<>(patternGraph.vertexSet());
		final Predicate<MatchedPattern> filter = v -> v.type == BEGIN;
		final Set<MatchedPattern> startNodes = vertices.stream().filter(filter).collect(Collectors.toSet());
		final List<List<MatchedPattern>> allCompletePaths = new LinkedList<>();
		startNodes.stream().forEach(v -> allCompletePaths.addAll(findCompletePath(patternGraph, v)));
		return allCompletePaths;
	}

	private static List<List<MatchedPattern>> findCompletePath(DirectedGraph<MatchedPattern, String> patternGraph,
															   MatchedPattern v) {
		final BreadthFirstIterator<MatchedPattern, String> iterator = new BreadthFirstIterator<>(patternGraph, v);
		final List<MatchedPattern> endPatterns = new LinkedList<>();
		while (iterator.hasNext()) {
			final MatchedPattern vertex = iterator.next();
			if (vertex.type == END) {
				endPatterns.add(vertex);
			}
		}

		return endPatterns.stream().map(endPattern -> printPathToBeginning(iterator.getParents(), endPattern))
				.collect(toList());
	}

	private static List<MatchedPattern> printPathToBeginning(Map<MatchedPattern, MatchedPattern> parents,
															 MatchedPattern endPattern) {
		final List<MatchedPattern> path = new LinkedList<>();
		path.add(endPattern);
		MatchedPattern currentPattern = endPattern;
		do {
			currentPattern = parents.get(currentPattern);
			path.add(currentPattern);
		} while (currentPattern != parents.get(currentPattern));
		return Lists.reverse(path);
	}

	private static void removeIsolatedPatterns(DirectedGraph<MatchedPattern, String> patternGraph) {
		final Set<MatchedPattern> vertices = new HashSet<>(patternGraph.vertexSet());
		final Predicate<MatchedPattern> filter = v -> v.type != END && patternGraph.outDegreeOf(v) == 0;
		vertices.stream().filter(filter).forEach(patternGraph::removeVertex);
	}
}