package ro.utcn.kdd.rosil.metric;

import ro.utcn.kdd.rosil.data.Word;

public interface SplittingEvaluator {

    double evaluate(Word expected, Word toBeEvaluated);
}
