package ro.utcn.kdd.rosil.io;

import org.junit.Test;

import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static org.junit.Assert.assertTrue;

public class LineBasedSplitterTest {

    @Test
    public void testSplit50() throws Exception {
        final LineBasedSplitter splitter = new LineBasedSplitter(get("data/words_no_struct.csv"));
        splitter.split(0.5, get("data"));
        assertTrue(exists(get("data/s50_0_words_no_struct.csv")));
        assertTrue(exists(get("data/s50_1_words_no_struct.csv")));
    }
}