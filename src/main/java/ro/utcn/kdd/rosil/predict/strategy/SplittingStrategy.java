package ro.utcn.kdd.rosil.predict.strategy;

import ro.utcn.kdd.rosil.predict.chain.MatchedPatternChain;

import java.util.List;

public interface SplittingStrategy {

	MatchedPatternChain best(List<MatchedPatternChain> chains);
}
