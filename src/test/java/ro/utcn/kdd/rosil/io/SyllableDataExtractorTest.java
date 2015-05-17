package ro.utcn.kdd.rosil.io;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.kdd.rosil.data.PotentialSyllable;
import ro.utcn.kdd.rosil.data.SyllableDataExtractor;
import ro.utcn.kdd.rosil.data.Word;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static ro.utcn.kdd.rosil.data.SyllableDataExtractor.DEFAULT_BORDERS;

public class SyllableDataExtractorTest {

    private static final Word ALAMBICAT = new Word(asList("a", "lam", "bi", "cat"));
    private static final Word EVENTUAL = new Word(asList("e", "ven", "tual"));

    private SyllableDataExtractor syllableDataExtractor;

    @Before
    public void setUp() throws Exception {
        this.syllableDataExtractor = new SyllableDataExtractor();
    }

    @Test
    public void testExtractFromWord() {
        final List<PotentialSyllable> potentialSyllables = syllableDataExtractor.extractFrom(ALAMBICAT, DEFAULT_BORDERS);
        assertEquals(ALAMBICAT.toString().length(), potentialSyllables.size());
    }

    @Test
    public void testExtractFromWords() {
        final List<Word> words = asList(ALAMBICAT, EVENTUAL);
        final Set<PotentialSyllable> potentialSyllables = syllableDataExtractor.extractFrom(words, DEFAULT_BORDERS);
        assertEquals(EVENTUAL.toString().length() + ALAMBICAT.toString().length(), potentialSyllables.size());
    }

}