package ro.utcn.kdd.rosil.bide;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import jp.ac.titech.cs.se.sparesort.SequenceDatabase;
import jp.ac.titech.cs.se.sparesort.bide.ConcurrentBIDE;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.utcn.kdd.rosil.data.Word;
import ro.utcn.kdd.rosil.io.WordsReader;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.join;

public class SilBIDE {
    protected static final Logger logger = LoggerFactory.getLogger(SilBIDE.class);

    public static void main(String[] args) throws Exception {
        final int minSupport = 10;
        final List<Pattern> patterns = loadPatternsFor(minSupport);
        final Map<String, Pattern> indexedPatterns = indexPatterns(patterns);


/*        splitWord(indexedPatterns, "elicopter");
        splitWord(indexedPatterns, "mașina");*/
        splitWord(indexedPatterns, "inginer");
/*        splitWord(indexedPatterns, "aglutinare");
        splitWord(indexedPatterns, "usturoi");
        splitWord(indexedPatterns, "castravete");
        splitWord(indexedPatterns, "împărat");
        splitWord(indexedPatterns, "gunoier");
        splitWord(indexedPatterns, "moșneag");*/
    }

    private static void splitWord(Map<String, Pattern> indexedPatterns, String word) throws InterruptedException {
        final List<MatchedPattern> matchedPatterns = new LinkedList<>();
        for (int startIndex = 0; startIndex < word.length() - 1; startIndex++) {
            for (int endIndex = startIndex + 1; endIndex <= word.length(); endIndex++) {
                final String substring = word.substring(startIndex, endIndex);
                final Pattern pattern = indexedPatterns.get(substring);
                if (pattern != null && pattern.elements.size() > 1) {
                    matchedPatterns.add(new MatchedPattern(pattern, startIndex, endIndex));
                }
            }
        }
        final DirectedGraph<Integer, Pattern> patternGraph = new DefaultDirectedGraph<>(Pattern.class);
        for (MatchedPattern matchedPattern : matchedPatterns) {
            patternGraph.addVertex(matchedPattern.startIndex);
            patternGraph.addVertex(matchedPattern.endIndex);
            patternGraph.addEdge(matchedPattern.startIndex, matchedPattern.endIndex, matchedPattern.pattern);
            int elementIndex = matchedPattern.startIndex;
            for (String patternElement : matchedPattern.pattern.elements) {
                patternGraph.addVertex(elementIndex);
                final int elementEndIndex = elementIndex + patternElement.length();
                patternGraph.addVertex(elementEndIndex);
                final Pattern subPattern = new Pattern(singletonList(patternElement), matchedPattern.pattern.support);
                patternGraph.addEdge(elementIndex, elementEndIndex, subPattern);
                elementIndex+=patternElement.length();
            }
        }
        //showGraph(patternGraph);
        for (int i = 0; i < word.length(); i++) {
            if (patternGraph.containsVertex(i)) {
                logger.info(word.charAt(i) + " - " + Integer.toString(patternGraph.incomingEdgesOf(i).size()));
            } else {
                logger.info(word.charAt(i) + " - 0");
            }
        }
        logger.info(patternGraph.toString());
    }

    private static void showGraph(DirectedGraph<Integer, Pattern> patternGraph) throws InterruptedException {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        AtomicBoolean windowClosed = new AtomicBoolean(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                windowClosed.set(true);
            }
        });
        frame.setSize(800, 600);
        final JGraphXAdapter<Integer, Pattern> graphXAdapter = new JGraphXAdapter<>(patternGraph);
        frame.getContentPane().add(new mxGraphComponent(graphXAdapter));
        frame.setVisible(true);
        final mxFastOrganicLayout mxFastOrganicLayout = new mxFastOrganicLayout(graphXAdapter);
        mxFastOrganicLayout.setForceConstant(150);
        mxFastOrganicLayout.execute(graphXAdapter.getDefaultParent());
        while (!windowClosed.get()) {
            Thread.sleep(1000);
        }
    }

    private static Map<String, Pattern> indexPatterns(List<Pattern> patterns) {
        final Map<String, Pattern> indexedPatterns = new HashMap<>();
        patterns.forEach(pattern -> indexedPatterns.put(join(pattern.elements.toArray()), pattern));
        return indexedPatterns;
    }

    private static List<Pattern> loadPatternsFor(int minSupport) throws Exception {
        final Path patternsPath = Paths.get(format("../data/patterns_%s.json", minSupport));
        if (!Files.exists(patternsPath)) {
            computeAndCachePatterns(minSupport, patternsPath);
        }
        logger.info("Reading patterns...");
        return getFromFile(patternsPath);
    }

    private static List<Pattern> getFromFile(Path patternsPath) throws IOException {
        try (final BufferedReader reader = Files.newBufferedReader(patternsPath, Charsets.UTF_8)) {
            Type listType = new TypeToken<List<Pattern>>() {
            }.getType();
            return new Gson().fromJson(reader, listType);
        }
    }

    private static void computeAndCachePatterns(int minSupport, Path patternsPath) throws Exception {
        final List<Word> words = new WordsReader().read(Paths.get("data/words_all.txt"));
        logger.info(format("Loaded %s words...", words.size()));
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

    private static Map<List<String>, Integer> mine(SequenceDatabase<String> sdb, int support) throws Exception {
        final Map<List<String>, Integer> patterns = sdb.mineFrequentClosedSequences(support);
        logger.info(format("Found %s patterns.", patterns.size()));
        return patterns;
    }

    public static class Pattern {
        public final List<String> elements;
        public final int support;

        public Pattern(List<String> elements, int support) {
            this.elements = elements;
            this.support = support;
        }

        @Override
        public String toString() {
            return format("%s(%s)", elements, support);
        }
    }

    public static class MatchedPattern {
        public final Pattern pattern;
        public final Integer startIndex;
        public final Integer endIndex;

        public MatchedPattern(Pattern pattern, Integer startIndex, Integer endIndex) {
            this.pattern = pattern;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }
}
