package ro.utcn.kdd.rosil.predict;

import java.util.List;

public interface SplittingPredictor {

	MatchedPatternChain best(List<MatchedPatternChain> chains);
}
