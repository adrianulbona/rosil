package ro.utcn.kdd.rosil.data;

public class MatchedPattern {

    public final Pattern pattern;
    public final Integer startIndex;
    public final Integer endIndex;

    public MatchedPattern(Pattern pattern, Integer startIndex, Integer endIndex) {
        this.pattern = pattern;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
}
