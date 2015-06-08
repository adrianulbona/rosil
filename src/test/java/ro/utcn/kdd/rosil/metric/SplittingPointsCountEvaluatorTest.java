package ro.utcn.kdd.rosil.metric;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.kdd.rosil.io.Word;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class SplittingPointsCountEvaluatorTest {

    private SplittingPointsCountEvaluator evaluator;

    @Before
    public void setUp() throws Exception {
        this.evaluator = new SplittingPointsCountEvaluator();
    }

    @Test
    public void testEvaluateMissingSplit() throws Exception {
        final Word expected = new Word(asList("an", "al", "fa", "bet"));
        final Word toBeEvaluated = new Word(asList("anal", "fa", "bet"));
        assertEquals(3.0 / 4, evaluator.evaluate(expected, toBeEvaluated), 0.01);
    }

    @Test
    public void testEvaluateExtraSplit() throws Exception {
        final Word expected = new Word(asList("an", "al", "fa", "bet"));
        final Word toBeEvaluated = new Word(asList("a", "n","al", "fa", "bet"));
        assertEquals(1.0, evaluator.evaluate(expected, toBeEvaluated), 0.01); // this will not that nice!
    }
}