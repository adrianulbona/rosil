package ro.utcn.kdd.rosil.predict.strategy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ro.utcn.kdd.rosil.predict.chain.MatchedPatternChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.nullsLast;

public class PathCountBasedSplittingStrategy implements SplittingStrategy {

	public MatchedPatternChain best(List<MatchedPatternChain> chains) {
		final Multimap<String, MatchedPatternChain> groupBySplitting = HashMultimap.create();
		chains.forEach(path -> groupBySplitting.put(path.toString(), path));
		final Optional<Map.Entry<String, Collection<MatchedPatternChain>>> best =
				groupBySplitting.asMap().entrySet().stream()
						.max(nullsLast(comparingInt(group -> group.getValue().size())));
		return best.isPresent() ? best.get().getValue().iterator().next() : null;
	}
}
