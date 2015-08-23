package ro.utcn.kdd.rosil.input;

import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore
public class WordsReaderTest {

    @Test
    public void testRead() throws Exception {
        final List<Word> words = new WordsReader().read(Paths.get("data/words_all.txt"));
        assertEquals(525486, words.size());
    }
}