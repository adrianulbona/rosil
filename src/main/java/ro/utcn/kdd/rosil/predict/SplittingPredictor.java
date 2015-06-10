package ro.utcn.kdd.rosil.predict;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.nullsLast;

public class SplittingPredictor {

	private final List<MatchedPatternChain> chains;

	public SplittingPredictor(List<MatchedPatternChain> chains) {
		this.chains = chains;
	}

	public MatchedPatternChain best() {
		final Multimap<String, MatchedPatternChain> groupBySplitting = groupBySplitting();
		final Optional<Map.Entry<String, Collection<MatchedPatternChain>>> best =
				groupBySplitting.asMap().entrySet().stream().max(
						nullsLast(comparingInt(group -> group.getValue().size())));
		return best.isPresent() ? best.get().getValue().iterator().next() : null;
	}

	private Multimap<String, MatchedPatternChain> groupBySplitting() {
		final Multimap<String, MatchedPatternChain> aggregated = HashMultimap.create();
		this.chains.forEach(path -> aggregated.put(path.toString(), path));
		return aggregated;
	}
}
