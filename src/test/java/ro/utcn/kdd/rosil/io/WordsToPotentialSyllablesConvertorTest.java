package ro.utcn.kdd.rosil.io;

import org.junit.Test;
import ro.utcn.kdd.rosil.data.PotentialSyllable;
import ro.utcn.kdd.rosil.data.SyllableDataExtractor;
import ro.utcn.kdd.rosil.data.Word;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.join;

public class WordsToPotentialSyllablesConvertorTest {

    @Test
    public void testConvert() throws IOException {
        final List<Word> words = new WordsReader().read(Paths.get("data/words_all.txt"));
        System.out.println(String.format("words loaded (%d).", words.size()));

        final List<List<Integer>> bordersList = new ArrayList<>();
        bordersList.add(Arrays.<Integer>asList(-2, 0, 3));


        for (List<Integer> borders : bordersList) {
            System.out.println("starting processing of : "+ borders);
            final Set<PotentialSyllable> potentialSyllables = new SyllableDataExtractor().extractFrom(words, borders);
            System.out.println(String.format("potential syllables extracted (%d).", potentialSyllables.size()));
            final PotentialSyllablesWriter writer = new PotentialSyllablesWriter();
            final String destination = "data/" + join(writer.computeHeader(borders)) + ".csv";
            writer.write(potentialSyllables, borders, Paths.get(destination));
            System.out.println(String.format("potential syllables written (%d).", potentialSyllables.size()));
        }
    }
}