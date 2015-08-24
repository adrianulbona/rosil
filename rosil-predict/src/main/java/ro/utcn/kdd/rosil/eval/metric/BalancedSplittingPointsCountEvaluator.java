package ro.utcn.kdd.rosil.eval.metric;

import com.google.common.collect.Sets;
import ro.utcn.kdd.rosil.input.Word;

import java.util.Set;

public class BalancedSplittingPointsCountEvaluator implements SplittingEvaluator {

    @Override
    public double evaluate(Word expected, Word toBeEvaluated) {
        final Set<Integer> expectedSyllableIndexes = expected.getSyllableIndexes();
        final Set<Integer> toBeEvaluatedSyllableIndexes = toBeEvaluated.getSyllableIndexes();
        int missingSplitsCount = Sets.difference(expectedSyllableIndexes, toBeEvaluatedSyllableIndexes).size();
        int extraSplitsCount = Sets.difference(toBeEvaluatedSyllableIndexes, expectedSyllableIndexes).size();
        final double missingSplitsComponent = .5 * (1.0 - (double) missingSplitsCount / expectedSyllableIndexes.size());
        final double extraSplitsComponent = .5 * (1.0 - (double) extraSplitsCount / toBeEvaluatedSyllableIndexes.size());
        return missingSplitsComponent + extraSplitsComponent;
    }
}
