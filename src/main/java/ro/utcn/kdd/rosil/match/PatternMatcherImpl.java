package ro.utcn.kdd.rosil.match;

import com.google.common.collect.Range;
import ro.utcn.kdd.rosil.pattern.Pattern;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.join;

public class PatternMatcherImpl implements PatternMatcher {

    private final Map<String, Pattern> indexedPatterns;

    public PatternMatcherImpl(List<Pattern> patterns) {
        this.indexedPatterns = indexPatterns(patterns);
    }

    private Map<String, Pattern> indexPatterns(List<Pattern> patterns) {
        final Map<String, Pattern> indexedPatterns = new HashMap<>();
        patterns.forEach(pattern -> indexedPatterns.put(join(pattern.elements.toArray()), pattern));
        return indexedPatterns;
    }

    @Override
    public List<MatchedPattern> match(String word) {
        final List<MatchedPattern> matchedPatterns = new LinkedList<>();
        for (int startIndex = 0; startIndex < word.length() - 1; startIndex++) {
            for (int endIndex = startIndex + 1; endIndex <= word.length(); endIndex++) {
                final String substring = word.substring(startIndex, endIndex);
                final Pattern pattern = indexedPatterns.get(substring);
                if (pattern != null) {
                    final Range range = Range.closedOpen(startIndex, endIndex);
                    final MatchedPattern.Type patternType = findType(range, word.length());
                    matchedPatterns.add(new MatchedPattern(pattern, range, patternType));
                }
            }
        }
        return matchedPatterns;
    }

    private MatchedPattern.Type findType(Range<Integer> range, int wordLength) {
        final boolean isBegin = range.lowerEndpoint() == 0;
        final boolean isEnd = range.upperEndpoint() == wordLength;
        if (isBegin && isEnd) {
            return MatchedPattern.Type.COMPLETE;
        }
        if (isBegin) {
            return MatchedPattern.Type.BEGIN;
        }
        if (isEnd) {
            return MatchedPattern.Type.END;
        }
        return MatchedPattern.Type.INTERMEDIARY;
    }
}
