package ro.utcn.kdd.rosil.data;

import com.google.common.collect.Range;

import static com.google.common.collect.Range.closedOpen;
import static ro.utcn.kdd.rosil.data.MatchedPattern.ExtensionType.AFTER;
import static ro.utcn.kdd.rosil.data.MatchedPattern.ExtensionType.BEFORE;

public class MatchedPattern {

    public final Pattern pattern;
    public final Integer startIndex;
    public final Integer endIndex;

    public MatchedPattern(Pattern pattern, Integer startIndex, Integer endIndex) {
        this.pattern = pattern;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public boolean isExtendableBeforeWith(MatchedPattern possibleExtension) {
        return isExtendableWith(possibleExtension, BEFORE);

    }

    public boolean isExtendableAfterWith(MatchedPattern possibleExtension) {
        return isExtendableWith(possibleExtension, AFTER);

    }

    private boolean isExtendableWith(MatchedPattern possibleExtension, ExtensionType extensionType) {
        if (!this.getRange().isConnected(possibleExtension.getRange())) {
            return false;
        }

        final Range<Integer> intersection = this.getRange().intersection(possibleExtension.getRange());
        int splitPointToMatch = extensionType == BEFORE ? intersection.lowerEndpoint() : intersection.upperEndpoint();
        return intersection.isEmpty() || possibleExtension.hasSplitPointAt(splitPointToMatch);
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

    private Range<Integer> getRange() {
        return closedOpen(this.startIndex, this.endIndex);
    }

    public enum ExtensionType {
        BEFORE,
        AFTER
    }
}
