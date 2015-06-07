package ro.utcn.kdd.rosil.predict;

import ro.utcn.kdd.rosil.data.MatchedPattern;

import static ro.utcn.kdd.rosil.predict.PatternNodeType.*;

public class PatternNode {

    public final MatchedPattern matchedPattern;
    public final PatternNodeType type;

    private PatternNode(MatchedPattern matchedPattern, PatternNodeType type) {
        this.matchedPattern = matchedPattern;
        this.type = type;
    }

    public static PatternNode createStartNode(MatchedPattern matchedPattern) {
        return new PatternNode(matchedPattern, START);
    }

    public static PatternNode createIntermediarNode(MatchedPattern matchedPattern) {
        return new PatternNode(matchedPattern, INTERMEDIAR);
    }

    public static PatternNode createStopNode(MatchedPattern matchedPattern) {
        return new PatternNode(matchedPattern, STOP);
    }

    @Override
    public String toString() {
        return matchedPattern.toString();
    }
}
