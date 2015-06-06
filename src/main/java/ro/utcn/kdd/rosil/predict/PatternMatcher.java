package ro.utcn.kdd.rosil.predict;

import ro.utcn.kdd.rosil.data.MatchedPattern;

import java.util.List;

public interface PatternMatcher {

    List<MatchedPattern> match(String word);
}
