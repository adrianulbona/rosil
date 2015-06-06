package ro.utcn.kdd.rosil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.data.Pattern;
import ro.utcn.kdd.rosil.pattern.PatternFinder;
import ro.utcn.kdd.rosil.predict.PatternGraphBuilder;
import ro.utcn.kdd.rosil.predict.PatternMatcher;
import ro.utcn.kdd.rosil.predict.PatternMatcherImpl;

import java.nio.file.Path;
import java.util.List;

import static java.nio.file.Paths.get;

public class SilBIDE {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SilBIDE.class);

    public static void main(String[] args) throws Exception {
        final int minSupport = 10;
        final Path wordsPath = get("data/words_all.txt");
        final List<Pattern> patterns = new PatternFinder().find(minSupport, wordsPath);
        final PatternMatcher matcher = new PatternMatcherImpl(patterns);

        final PatternGraphBuilder graphBuilder = new PatternGraphBuilder();
        System.out.println(graphBuilder.build(matcher.match("elicopter")));
        matcher.match("mașina");
        matcher.match("aglutinare");
        matcher.match("usturoi");
        matcher.match("castravete");
        matcher.match("împărat");
        matcher.match("gunoier");
        matcher.match("moșneag");
    }
}