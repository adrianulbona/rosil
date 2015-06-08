package ro.utcn.kdd.rosil.match;

import com.google.common.collect.Range;
import ro.utcn.kdd.rosil.pattern.Pattern;

public class MatchedPattern {

    public final Pattern pattern;
    public final Type type;

    private final Range<Integer> range;

    public MatchedPattern(Pattern pattern, Range<Integer> range, Type type) {
        this.pattern = pattern;
        this.range = range;
        this.type = type;
    }

    public boolean isExtendableAfterWith(MatchedPattern possibleExtension) {
        final Range<Integer> possibleExtensionRange = possibleExtension.getRange();
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
        int splitPoint = this.range.lowerEndpoint();
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
        return this.range;
    }

    @Override
    public String toString() {
        return pattern + "->" + this.range;
    }

    public enum Type {
        BEGIN, INTERMEDIARY, COMPLETE, END
    }
}
