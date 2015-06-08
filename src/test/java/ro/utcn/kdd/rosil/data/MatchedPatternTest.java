package ro.utcn.kdd.rosil.data;

import org.junit.Test;
import ro.utcn.kdd.rosil.match.MatchedPattern;
import ro.utcn.kdd.rosil.pattern.Pattern;

import java.util.Arrays;
import java.util.Collections;

import static com.google.common.collect.Range.closedOpen;
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
        final MatchedPattern.Type type = MatchedPattern.Type.INTERMEDIARY;
        final Pattern pattern0 = new Pattern(Arrays.<String>asList("li", "cop"), -1);
        final MatchedPattern pattern = new MatchedPattern(pattern0, closedOpen(1, 6), type);

        final Pattern pattern1 = new Pattern(Arrays.<String>asList("op", "ter"), -1);
        final MatchedPattern extension1 = new MatchedPattern(pattern1, closedOpen(4, 9), type);
        assertTrue(pattern.isExtendableAfterWith(extension1));

        final Pattern pattern2 = new Pattern(Arrays.<String>asList("cop", "ter"), -1);
        final MatchedPattern extension2 = new MatchedPattern(pattern2, closedOpen(3, 9), type);
        assertTrue(pattern.isExtendableAfterWith(extension2));

        final Pattern pattern4 = new Pattern(Collections.singletonList("ter"), -1);
        final MatchedPattern extension4 = new MatchedPattern(pattern4, closedOpen(6, 9), type);
        assertTrue(pattern.isExtendableAfterWith(extension4));
    }

    @Test
    public void testIsExtendableAfterWithFalse() throws Exception {
        final MatchedPattern.Type type = MatchedPattern.Type.INTERMEDIARY;
        final Pattern pattern0 = new Pattern(Arrays.<String>asList("li", "cop"), -1);
        final MatchedPattern pattern = new MatchedPattern(pattern0, closedOpen(1, 6), type);

        final Pattern pattern1 = new Pattern(Collections.singletonList(""), -1);
        final MatchedPattern extension1 = new MatchedPattern(pattern1, closedOpen(0, 0), type);
        assertFalse(pattern.isExtendableAfterWith(extension1));

        final Pattern pattern2 = new Pattern(Arrays.<String>asList("co", "ter"), -1);
        final MatchedPattern extension2 = new MatchedPattern(pattern2, closedOpen(3, 8), type);
        assertFalse(pattern.isExtendableAfterWith(extension2));

        final Pattern pattern3 = new Pattern(Arrays.<String>asList("e", "li", "p", "te"), -1);
        final MatchedPattern extension3 = new MatchedPattern(pattern3, closedOpen(0, 6), type);
        assertFalse(pattern.isExtendableAfterWith(extension3));

        final Pattern pattern4 = new Pattern(Collections.singletonList("ter"), -1);
        final MatchedPattern extension4 = new MatchedPattern(pattern4, closedOpen(7, 10), type);
        assertFalse(pattern.isExtendableAfterWith(extension4));

        final Pattern pattern5 = new Pattern(Arrays.<String>asList("e", "li", "cop", "te"), -1);
        final MatchedPattern extension5 = new MatchedPattern(pattern5, closedOpen(0, 8), type);
        assertFalse(pattern.isExtendableAfterWith(extension5));
    }
}