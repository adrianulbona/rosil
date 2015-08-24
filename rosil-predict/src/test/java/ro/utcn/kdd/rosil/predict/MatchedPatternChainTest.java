package ro.utcn.kdd.rosil.predict;

import org.junit.Test;
import ro.utcn.kdd.rosil.predict.match.MatchedPattern;
import ro.utcn.kdd.rosil.pattern.Pattern;
import ro.utcn.kdd.rosil.predict.chain.MatchedPatternChain;

import java.util.Arrays;

import static com.google.common.collect.Range.closedOpen;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class MatchedPatternChainTest {

	@Test
	public void testOverlappingsCountExists() throws Exception {
		final MatchedPattern first =
				new MatchedPattern(new Pattern(asList("al", "fa"), -1), closedOpen(0, 4), MatchedPattern.Type.BEGIN);
		final MatchedPattern second =
				new MatchedPattern(new Pattern(asList("fa", "be"), -1), closedOpen(2, 6), MatchedPattern.Type.END);

		final MatchedPatternChain chain = new MatchedPatternChain(Arrays.asList(first, second));
		assertEquals(2, chain.overlappingsCount());
	}

	@Test
	public void testOverlappingsCountZero() throws Exception {
		final MatchedPattern first =
				new MatchedPattern(new Pattern(asList("al", "fa"), -1), closedOpen(0, 4), MatchedPattern.Type.BEGIN);
		final MatchedPattern second =
				new MatchedPattern(new Pattern(singletonList("be"), -1), closedOpen(4, 6), MatchedPattern.Type.END);

		final MatchedPatternChain chain = new MatchedPatternChain(Arrays.asList(first, second));
		assertEquals(0, chain.overlappingsCount());
	}

}