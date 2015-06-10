package ro.utcn.kdd.rosil.predict;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class PathLengthBasedSplittingPredictor implements SplittingPredictor {

	public MatchedPatternChain best(List<MatchedPatternChain> chains) {
		final List<MatchedPatternChain> sortedByLength =
				chains.stream().sorted(comparingInt(chain -> chain.patterns.size())).collect(toList());
		final Predicate<MatchedPatternChain> minLengthPredicate =
				chain -> chain.patterns.size() == sortedByLength.get(0).patterns.size();
		final Optional<MatchedPatternChain> best = sortedByLength.stream().filter(minLengthPredicate)
				.sorted(reverseOrder(comparingInt(MatchedPatternChain::overlappingsCount))).findFirst();
		return best.isPresent() ? best.get() : null;
	}
}
