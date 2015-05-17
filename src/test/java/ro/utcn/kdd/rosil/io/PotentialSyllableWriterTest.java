package ro.utcn.kdd.rosil.io;

import org.junit.Test;
import ro.utcn.kdd.rosil.data.PotentialSyllable;
import ro.utcn.kdd.rosil.data.SyllableDataExtractor;
import ro.utcn.kdd.rosil.data.Word;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class PotentialSyllableWriterTest {

    @Test
    public void writeTest() throws IOException {
        final Word word = new Word(Arrays.asList("a", "lam", "bi", "cat"));
        final SyllableDataExtractor syllableDataExtractor = new SyllableDataExtractor();
        final List<PotentialSyllable> potentialSyllables = syllableDataExtractor.extractFrom(word, SyllableDataExtractor.DEFAULT_BORDERS);
        new PotentialSyllablesWriter().write(potentialSyllables, Arrays.asList(-1, 0, 1, 2), Paths.get("data/trainset.csv"));
    }
}