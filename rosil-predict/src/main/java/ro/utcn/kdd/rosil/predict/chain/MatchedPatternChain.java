package ro.utcn.kdd.rosil.predict.chain;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import ro.utcn.kdd.rosil.input.Word;
import ro.utcn.kdd.rosil.predict.match.MatchedPattern;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Range.closedOpen;

public class MatchedPatternChain {

	public final List<MatchedPattern> patterns;

	public MatchedPatternChain(List<MatchedPattern> patterns) {
		this.patterns = patterns;
	}

	public Word toWord() {
		return new Word(findDistinctPatternElements());
	}


	private List<String> findDistinctPatternElements() {
		int lastMatchedPathUpperBound = 0;
		final List<String> combinedElements = new LinkedList<>();
		for (MatchedPattern matchedPattern : patterns) {
			final Integer upperBound = matchedPattern.getRange().upperEndpoint();
			final Range<Integer> choppedRange = closedOpen(lastMatchedPathUpperBound, upperBound);
			combinedElements.addAll(matchedPattern.getElementsForRange(choppedRange));
			lastMatchedPathUpperBound = upperBound;
		}
		return combinedElements;
	}

	public int overlappingsCount() {
		Range<Integer> coveredRange = closedOpen(0, 0);
		int overlappingsCount = 0;
		for (MatchedPattern pattern : patterns) {
			final Range<Integer> intersection = coveredRange.intersection(pattern.getRange());
			overlappingsCount += ContiguousSet.create(intersection, DiscreteDomain.integers()).size();
			coveredRange = closedOpen(0, pattern.getRange().upperEndpoint());
		}
		return overlappingsCount;
	}
}
