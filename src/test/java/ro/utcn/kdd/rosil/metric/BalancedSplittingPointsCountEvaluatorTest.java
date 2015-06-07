package ro.utcn.kdd.rosil.metric;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.kdd.rosil.data.Word;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class BalancedSplittingPointsCountEvaluatorTest {

    private BalancedSplittingPointsCountEvaluator evaluator;

    @Before
    public void setUp() throws Exception {
        this.evaluator = new BalancedSplittingPointsCountEvaluator();
    }

    @Test
    public void testEvaluateMissingSplit() throws Exception {
        final Word expected = new Word(asList("an", "al", "fa", "bet"));
        final Word toBeEvaluated = new Word(asList("anal", "fa", "bet"));
        assertEquals((1.0 - 1.0 / 4) * .5 + .5, evaluator.evaluate(expected, toBeEvaluated), 0.01);
    }

    @Test
    public void testEvaluateExtraSplit() throws Exception {
        final Word expected = new Word(asList("an", "al", "fa", "bet"));
        final Word toBeEvaluated = new Word(asList("a", "n", "al", "fa", "bet"));
        assertEquals(.5 + (1.0 - 1.0 / 5) * .5, evaluator.evaluate(expected, toBeEvaluated), 0.01);
    }
}