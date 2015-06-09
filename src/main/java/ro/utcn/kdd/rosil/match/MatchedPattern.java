package ro.utcn.kdd.rosil.match;

import com.google.common.collect.Range;
import ro.utcn.kdd.rosil.pattern.Pattern;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toCollection;

public class MatchedPattern {

	public final Pattern pattern;
	public final Type type;

	private final Range<Integer> range;
	private final Map<Range<Integer>, String> rangesForElements;

	public MatchedPattern(Pattern pattern, Range<Integer> range, Type type) {
		this.pattern = pattern;
		this.range = range;
		this.type = type;
		int index = this.range.lowerEndpoint();
		this.rangesForElements = new LinkedHashMap<>();
		for (String element : this.pattern.elements) {
			final int length = element.length();
			rangesForElements.put(Range.closedOpen(index, index + length), element);
			index += length;
		}
	}

	public boolean isExtendableAfterWith(MatchedPattern possibleExtension) {
		final Range<Integer> possibleExtensionRange = possibleExtension.getRange();
		if (!range.isConnected(possibleExtensionRange) || range.encloses(possibleExtensionRange) ||
				possibleExtensionRange.encloses(range)) {
			return false;
		}

		final Range<Integer> intersection = range.intersection(possibleExtensionRange);
		if (intersection.isEmpty()) {
			return range.upperEndpoint() <= intersection.lowerEndpoint();
		}
		return possibleExtension.hasSplitPointAt(intersection.upperEndpoint());
	}

	private boolean hasSplitPointAt(int possibleSplitPoint) {
		int splitPoint = this.range.lowerEndpoint();
		for (String element : this.pattern.elements) {
			if (splitPoint == possibleSplitPoint) {
				return true;
			}
			else if (splitPoint > possibleSplitPoint) {
				return false;
			}
			splitPoint += element.length();
		}
		return false;
	}

	public Range<Integer> getRange() {
		return this.range;
	}

	@Override
	public String toString() {
		return pattern + "->" + this.range;
	}

	public List<String> getElementsForRange(Range<Integer> range) {
		return rangesForElements.keySet().stream().filter(range::encloses).map(rangesForElements::get)
				.collect(toCollection(LinkedList::new));
	}

	public enum Type {
		BEGIN,
		INTERMEDIARY,
		COMPLETE,
		END
	}
}
