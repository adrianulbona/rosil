package ro.utcn.kdd.rosil.data;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MatchedPatternTest {

/*    @Test
    public void testIsExtendableBeforeWithTrue() throws Exception {
        final MatchedPattern pattern = new MatchedPattern(new Pattern(Arrays.<String>asList("li", "co"), -1), 1, 5);

        final Pattern pattern1 = new Pattern(Arrays.<String>asList("e", "li"), -1);
        final MatchedPattern extension1 = new MatchedPattern(pattern1, 0, 3);
        assertTrue(pattern.isExtendableBeforeWith(extension1));

        final Pattern pattern2 = new Pattern(Arrays.<String>asList("e", "l"), -1);
        final MatchedPattern extension2 = new MatchedPattern(pattern2, 0, 2);
        assertTrue(pattern.isExtendableBeforeWith(extension2));

        final Pattern pattern3 = new Pattern(Arrays.<String>asList("e", "li", "co", "te"), -1);
        final MatchedPattern extension3 = new MatchedPattern(pattern3, 0, 7);
        assertTrue(pattern.isExtendableBeforeWith(extension3));

        final Pattern pattern4 = new Pattern(Collections.singletonList("e"), -1);
        final MatchedPattern extension4 = new MatchedPattern(pattern4, 0, 1);
        assertTrue(pattern.isExtendableBeforeWith(extension4));
    }

    @Test
    public void testIsExtendableBeforeWithFalse() throws Exception {
        final MatchedPattern pattern = new MatchedPattern(new Pattern(Arrays.<String>asList("li", "co"), -1), 1, 5);

        final Pattern pattern1 = new Pattern(Collections.singletonList(""), -1);
        final MatchedPattern extension1 = new MatchedPattern(pattern1, 0, 0);
        assertFalse(pattern.isExtendableBeforeWith(extension1));

        final Pattern pattern2 = new Pattern(Collections.singletonList("el"), -1);
        final MatchedPattern extension2 = new MatchedPattern(pattern2, 0, 2);
        assertFalse(pattern.isExtendableBeforeWith(extension2));

        final Pattern pattern3 = new Pattern(Arrays.<String>asList("el", "li"), -1);
        final MatchedPattern extension3 = new MatchedPattern(pattern3, 0, 5);
        assertFalse(pattern.isExtendableBeforeWith(extension3));
    }*/

    @Test
    public void testIsExtendableAfterWithTrue() throws Exception {
        final MatchedPattern pattern = new MatchedPattern(new Pattern(Arrays.<String>asList("li", "cop"), -1), 1, 6);

        final Pattern pattern1 = new Pattern(Arrays.<String>asList("op", "ter"), -1);
        final MatchedPattern extension1 = new MatchedPattern(pattern1, 4, 9);
        assertTrue(pattern.isExtendableAfterWith(extension1));

        final Pattern pattern2 = new Pattern(Arrays.<String>asList("cop", "ter"), -1);
        final MatchedPattern extension2 = new MatchedPattern(pattern2, 3, 9);
        assertTrue(pattern.isExtendableAfterWith(extension2));

        final Pattern pattern3 = new Pattern(Arrays.<String>asList("e", "li", "cop", "te"), -1);
        final MatchedPattern extension3 = new MatchedPattern(pattern3, 0, 8);
        assertTrue(pattern.isExtendableAfterWith(extension3));

        final Pattern pattern4 = new Pattern(Collections.singletonList("ter"), -1);
        final MatchedPattern extension4 = new MatchedPattern(pattern4, 6, 9);
        assertTrue(pattern.isExtendableAfterWith(extension4));
    }

    @Test
    public void testIsExtendableAfterWithFalse() throws Exception {
        final MatchedPattern pattern = new MatchedPattern(new Pattern(Arrays.<String>asList("li", "cop"), -1), 1, 6);

        final Pattern pattern1 = new Pattern(Collections.singletonList(""), -1);
        final MatchedPattern extension1 = new MatchedPattern(pattern1, 0, 0);
        assertFalse(pattern.isExtendableAfterWith(extension1));

        final Pattern pattern2 = new Pattern(Arrays.<String>asList("co", "ter"), -1);
        final MatchedPattern extension2 = new MatchedPattern(pattern2, 3, 8);
        assertFalse(pattern.isExtendableAfterWith(extension2));

        final Pattern pattern3 = new Pattern(Arrays.<String>asList("e", "li", "p", "te"), -1);
        final MatchedPattern extension3 = new MatchedPattern(pattern3, 0, 6);
        assertFalse(pattern.isExtendableAfterWith(extension3));

        final Pattern pattern4 = new Pattern(Collections.singletonList("ter"), -1);
        final MatchedPattern extension4 = new MatchedPattern(pattern4, 7, 10);
        assertFalse(pattern.isExtendableAfterWith(extension4));
    }
}