package ro.utcn.kdd.rosil.predict;

import ro.utcn.kdd.rosil.data.MatchedPattern;
import ro.utcn.kdd.rosil.data.Pattern;

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
                    matchedPatterns.add(new MatchedPattern(pattern, startIndex, endIndex));
                }
            }
        }
        return matchedPatterns;
    }
}
