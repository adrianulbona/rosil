package ro.utcn.kdd.rosil.predict.strategy;

import ro.utcn.kdd.rosil.predict.chain.MatchedPatternChain;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class PathOverlappingBasedSplittingStrategy implements SplittingStrategy {

	@Override
	public MatchedPatternChain best(List<MatchedPatternChain> chains) {
		final List<MatchedPatternChain> sortedByOverlappingCount =
				chains.stream().sorted(reverseOrder(comparingInt(MatchedPatternChain::overlappingsCount)))
						.collect(toList());


		final Predicate<MatchedPatternChain> maxOverlappingPredicate =
				chain -> chain.overlappingsCount() == sortedByOverlappingCount.get(0).overlappingsCount();
		final Optional<MatchedPatternChain> best = sortedByOverlappingCount.stream().filter(maxOverlappingPredicate)
				.sorted(comparingInt(chain -> chain.patterns.size())).findFirst();
		return best.isPresent() ? best.get() : null;
	}
}
