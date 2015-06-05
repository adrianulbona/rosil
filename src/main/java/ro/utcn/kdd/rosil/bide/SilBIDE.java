package ro.utcn.kdd.rosil.bide;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jp.ac.titech.cs.se.sparesort.SequenceDatabase;
import jp.ac.titech.cs.se.sparesort.bide.ConcurrentBIDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.data.Word;
import ro.utcn.kdd.rosil.io.WordsReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SilBIDE {
    protected static final Logger logger = LoggerFactory.getLogger(SilBIDE.class);

    public static void main(String[] args) throws Exception {
        final int minSupport = 100;
        final List<Pattern> patterns = loadPatternsFor(minSupport);
        logger.info("tralala");
//        logger.error();


/*
        Multimap<String, List<String>> index = HashMultimap.<String, List<String>>create();
        patterns.keySet().forEach(p -> index.put(p.get(0), p));
        //index.keySet().forEach(e -> System.out.println(e + "->" + index.get(e).size()));
        final Map<String, List<String>> stringifiedPatterns = new HashMap<>();
        patterns.entrySet().forEach(x -> stringifiedPatterns.put(StringUtils.join(x.getKey().toArray()), x.getKey()));


        splitWord(stringifiedPatterns, "maşină");
        splitWord(stringifiedPatterns, "inginer");
        splitWord(stringifiedPatterns, "aglutinare");
        splitWord(stringifiedPatterns, "usturoi");
        splitWord(stringifiedPatterns, "castravete");
        splitWord(stringifiedPatterns, "împărat");
        splitWord(stringifiedPatterns, "gunoier");
        splitWord(stringifiedPatterns, "moşneag");
*/
    }

    private static List<Pattern> loadPatternsFor(int minSupport) throws Exception {
        final Path patternsPath = Paths.get(String.format("data/patterns_%s.json", minSupport));
        if (!Files.exists(patternsPath)) {
            computeAndCachePatterns(minSupport, patternsPath);
        }
        logger.info("Reading patterns...");
        return getFromFile(patternsPath);
    }

    private static List<Pattern> getFromFile(Path patternsPath) throws IOException {
        try (final BufferedReader reader = Files.newBufferedReader(patternsPath, Charsets.UTF_8)) {
            Type listType = new TypeToken<List<Pattern>>() {}.getType();
            return new Gson().fromJson(reader, listType);
        }
    }

    private static void computeAndCachePatterns(int minSupport, Path patternsPath) throws Exception {
        final List<Word> words = new WordsReader().read(Paths.get("data/words_all.txt"));
        logger.info(String.format("Loaded %s words...", words.size()));
        final SequenceDatabase<String> sdb = createSequenceDatabase(words);
        logger.info("Created sequence database...");
        sdb.setMiningStrategy(new ConcurrentBIDE<>());
        final Map<List<String>, Integer> rawPatterns = mine(sdb, minSupport);
        final List<Pattern> patterns = new LinkedList<>();
        rawPatterns.entrySet().forEach(p -> patterns.add(new Pattern(p.getKey(), p.getValue())));
        writePatterns(patternsPath, patterns);
    }

    private static SequenceDatabase<String> createSequenceDatabase(List<Word> words) {
        final SequenceDatabase<String> sdb = new SequenceDatabase<>();
        for (Word word : words) {
            sdb.addSequence(Lists.newArrayList(word.getSyllables()));
        }
        return sdb;
    }

    private static void writePatterns(Path destination, List<Pattern> patterns) throws IOException {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        try (final BufferedWriter writer = Files.newBufferedWriter(destination, Charsets.UTF_8)) {
            gsonBuilder.create().toJson(patterns, writer);
        }
    }

    private static void splitWord(Map<String, List<String>> stringifiedPatterns, String word) {
        for (int i = word.length(); i > 0; i--) {
            final List<String> potentialPattern = stringifiedPatterns.get(word.substring(0, i));
            if (potentialPattern != null) {
                final String firstSyllable = potentialPattern.get(0);
                System.out.println(firstSyllable);
                word = word.substring(firstSyllable.length(), word.length());
                i = word.length();
            }
        }
    }

    private static Map<List<String>, Integer> mine(SequenceDatabase<String> sdb, int support) throws Exception {
        final Map<List<String>, Integer> patterns = sdb.mineFrequentClosedSequences(support);
        logger.info(String.format("Found %s patterns.", patterns.size()));
        return patterns;
    }

    public static class Pattern {
        public final List<String> elements;
        public final int support;

        public Pattern(List<String> elements, int support) {
            this.elements = elements;
            this.support = support;
        }
    }
}
