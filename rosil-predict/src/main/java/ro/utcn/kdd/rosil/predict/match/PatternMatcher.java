package ro.utcn.kdd.rosil.predict.match;

import java.util.List;

public interface PatternMatcher {

    List<MatchedPattern> match(String word);
}
