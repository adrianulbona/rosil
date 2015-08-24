package ro.utcn.kdd.rosil.eval.metric;

import ro.utcn.kdd.rosil.input.Word;

public interface SplittingEvaluator {

    double evaluate(Word expected, Word toBeEvaluated);
}
