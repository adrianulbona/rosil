package ro.utcn.kdd.rosil.eval.metric;

import ro.utcn.kdd.rosil.input.Word;

import java.util.Set;

import static com.google.common.collect.Sets.intersection;

public class SplittingPointsCountEvaluator implements SplittingEvaluator {

    @Override
    public double evaluate(Word expected, Word toBeEvaluated) {
        final Set<Integer> expectedSyllableIndexes = expected.getSyllableIndexes();
        final Set<Integer> toBeEvaluatedSyllableIndexes = toBeEvaluated.getSyllableIndexes();
        final Set<Integer> commonIndexes = intersection(expectedSyllableIndexes, toBeEvaluatedSyllableIndexes);

        return (double) commonIndexes.size() / expectedSyllableIndexes.size();
    }
}
