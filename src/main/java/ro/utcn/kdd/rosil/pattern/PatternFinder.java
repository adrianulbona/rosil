package ro.utcn.kdd.rosil.pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import jp.ac.titech.cs.se.sparesort.SequenceDatabase;
import jp.ac.titech.cs.se.sparesort.bide.ConcurrentBIDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.data.Pattern;
import ro.utcn.kdd.rosil.data.Word;
import ro.utcn.kdd.rosil.io.WordsReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;

public class PatternFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatternFinder.class);
    private static final String CACHED_PATTERNS_FILE_NAME_FORMAT = "patterns_%s.json";

    public List<Pattern> find(int minSupport, Path wordsPath) {
        final Path cachedPatternsPath = get(format(CACHED_PATTERNS_FILE_NAME_FORMAT, minSupport));
        final Path patternsPath = wordsPath.getParent().resolve(cachedPatternsPath);
        if (!Files.exists(patternsPath)) {
            try {
                computeAndCachePatterns(minSupport, wordsPath, patternsPath);
            } catch (IOException e) {
                LOGGER.error("Unable to cache patterns. ", e);
            }
        }
        LOGGER.info("Reading patterns...");
        return getFromFile(patternsPath);
    }

    private List<Pattern> getFromFile(Path patternsPath) {
        final List<Pattern> patterns = new LinkedList<>();
        try (final BufferedReader reader = Files.newBufferedReader(patternsPath, Charsets.UTF_8)) {
            final Type listType = new TypeToken<List<Pattern>>() {
            }.getType();
            patterns.addAll(new Gson().fromJson(reader, listType));
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            LOGGER.error("Unable to read patterns from: " + patternsPath, e);
        }
        return patterns;
    }

    private void computeAndCachePatterns(int minSupport, Path wordsPath, Path patternsPath) throws IOException {
        final List<Word> words = new WordsReader().read(wordsPath);
        LOGGER.info(format("Loaded %s words...", words.size()));
        final SequenceDatabase<String> sdb = createSequenceDatabase(words);
        LOGGER.info("Created sequence database...");
        sdb.setMiningStrategy(new ConcurrentBIDE<>());
        final Map<List<String>, Integer> rawPatterns = mine(sdb, minSupport);
        final Stream<Map.Entry<List<String>, Integer>> patternsStream = rawPatterns.entrySet().parallelStream();
        final List<Pattern> patterns = patternsStream.map(p -> new Pattern(p.getKey(), p.getValue())).collect(toList());
        writePatterns(patternsPath, patterns);
    }

    private SequenceDatabase<String> createSequenceDatabase(List<Word> words) {
        final SequenceDatabase<String> sdb = new SequenceDatabase<>();
        for (Word word : words) {
            sdb.addSequence(Lists.newArrayList(word.getSyllables()));
        }
        return sdb;
    }

    private void writePatterns(Path destination, List<Pattern> patterns) throws IOException {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        try (final BufferedWriter writer = Files.newBufferedWriter(destination, Charsets.UTF_8)) {
            gsonBuilder.create().toJson(patterns, writer);
        }
    }

    private Map<List<String>, Integer> mine(SequenceDatabase<String> sdb, int support) {
        try {
            final Map<List<String>, Integer> rawPatterns = sdb.mineFrequentClosedSequences(support);
            LOGGER.info(format("Found %s patterns.", rawPatterns.size()));
            return rawPatterns;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while mining patterns.", e);
        }
        return Collections.emptyMap();
    }

}
