package ro.utcn.kdd.rosil.match;

import java.util.List;

public interface PatternMatcher {

    List<MatchedPattern> match(String word);
}
