package ro.utcn.kdd.rosil.io;

import com.google.common.base.Charsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordsReader {

    public List<Word> read(Path path) throws IOException {
        final List<Word> words = new ArrayList<>();
        try (final BufferedReader reader = Files.newBufferedReader(path, Charsets.UTF_8)) {
            for (String line; (line = reader.readLine()) != null; ) {
                final String[] splitByWhitespace = line.split("\\s");
                if (splitByWhitespace.length == 1) {
                    final List<String> syllables = Arrays.asList(splitByWhitespace[0].split("-"));
                    words.add(new Word(syllables));
                }
            }
        }
        return words;
    }
}
