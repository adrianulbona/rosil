package ro.utcn.kdd.rosil.data;

import com.google.common.collect.Range;

import static com.google.common.collect.Range.closedOpen;

public class MatchedPattern {

    public final Pattern pattern;
    public final Integer startIndex;
    public final Integer endIndex;

    public MatchedPattern(Pattern pattern, Integer startIndex, Integer endIndex) {
        this.pattern = pattern;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public boolean isExtendableAfterWith(MatchedPattern possibleExtension) {
        final Range<Integer> possibleExtensionRange = possibleExtension.getRange();
        final Range<Integer> range = this.getRange();
        if (!range.isConnected(possibleExtensionRange)
                || range.encloses(possibleExtensionRange)
                || possibleExtensionRange.encloses(range)) {
            return false;
        }

        final Range<Integer> intersection = range.intersection(possibleExtensionRange);
        if (intersection.isEmpty()) {
            return range.upperEndpoint() <= intersection.lowerEndpoint();
        }
        return possibleExtension.hasSplitPointAt(intersection.upperEndpoint());
    }

    private boolean hasSplitPointAt(int possibleSplitPoint) {
        int splitPoint = this.getRange().lowerEndpoint();
        for (String element : this.pattern.elements) {
            if (splitPoint == possibleSplitPoint) {
                return true;
            } else if (splitPoint > possibleSplitPoint) {
                return false;
            }
            splitPoint += element.length();
        }
        return false;
    }

    public Range<Integer> getRange() {
        return closedOpen(this.startIndex, this.endIndex);
    }

    @Override
    public String toString() {
        return pattern + "->" + getRange();
    }
}
