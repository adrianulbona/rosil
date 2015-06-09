package ro.utcn.kdd.rosil.predict;

import com.google.common.collect.Range;
import ro.utcn.kdd.rosil.match.MatchedPattern;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Range.closedOpen;
import static org.apache.commons.lang3.StringUtils.join;

public class MatchedPatternChain {

	final List<MatchedPattern> chain;

	public MatchedPatternChain(List<MatchedPattern> chain) {
		this.chain = chain;
	}

	public String toSplitWord() {
		int lastMatchedPathUpperBound = 0;
		final List<String> combinedElements = new LinkedList<>();
		for (MatchedPattern matchedPattern : chain) {
			final Integer upperBound = matchedPattern.getRange().upperEndpoint();
			final Range<Integer> choppedRange = closedOpen(lastMatchedPathUpperBound, upperBound);
			combinedElements.addAll(matchedPattern.getElementsForRange(choppedRange));
			lastMatchedPathUpperBound = upperBound;
		}
		return join(combinedElements, "-");
	}
}
