package ro.utcn.kdd.rosil.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.String.format;

public class LineBasedSplitter {

    private final Path source;

    public LineBasedSplitter(Path source) {
        this.source = source;
    }

    public void split(double percentage, Path destinationFolder) throws IOException {
        final Map<Path, List<String>> splits = prepareSplitContainers(percentage, destinationFolder);
        final List<String> lines = readLines();
        shuffleLines(lines, splits);
        writeSplits(splits);
    }

    private List<String> readLines() throws IOException {
        final List<String> lines = new LinkedList<>();
        try (final BufferedReader reader = Files.newBufferedReader(source)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private Map<Path, List<String>> prepareSplitContainers(double percentage, Path destinationFolder) {
        final long numberOfSplits = Math.round(1.0 / percentage);
        final Map<Path, List<String>> splitContent = new HashMap<>();
        final int percentage100 = Double.valueOf(percentage * 100).intValue();
        for (int splitIndex = 0; splitIndex < numberOfSplits; splitIndex++) {
            final String splitFilename = format("s%d_%d_%s", percentage100, splitIndex, source.getFileName());
            splitContent.put(destinationFolder.resolve(splitFilename), new LinkedList<>());
        }
        return splitContent;
    }

    private void shuffleLines(List<String> lines, Map<Path, List<String>> splitContent) {
        final Random randomGenerator = new Random();
        final ArrayList<List<String>> splits = new ArrayList<>(splitContent.values());
        final int numberOfSplits = splits.size();
        for (String line : lines) {
            splits.get(randomGenerator.nextInt(numberOfSplits)).add(line);
        }
    }

    private void writeSplits(Map<Path, List<String>> splits) throws IOException {
        for (Map.Entry<Path, List<String>> split : splits.entrySet()) {
            try (final BufferedWriter writer = Files.newBufferedWriter(split.getKey())) {
                for (String line : split.getValue()) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }
}
