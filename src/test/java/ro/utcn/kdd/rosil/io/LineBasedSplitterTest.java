package ro.utcn.kdd.rosil.io;

import org.junit.Test;

import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static org.junit.Assert.assertTrue;

public class LineBasedSplitterTest {

    @Test
         public void testSplit50() throws Exception {
        final LineBasedSplitter splitter = new LineBasedSplitter(get("data/words_all.txt"));
        splitter.split(0.5, get("data"));
        assertTrue(exists(get("data/s50_0_words_all.txt")));
        assertTrue(exists(get("data/s50_1_words_all.txt")));
    }

    @Test
    public void testSplit20() throws Exception {
        final LineBasedSplitter splitter = new LineBasedSplitter(get("data/words_all.txt"));
        splitter.split(0.2, get("data"));
        assertTrue(exists(get("data/s20_0_words_all.txt")));
        assertTrue(exists(get("data/s20_1_words_all.txt")));
        assertTrue(exists(get("data/s20_2_words_all.txt")));
        assertTrue(exists(get("data/s20_3_words_all.txt")));
        assertTrue(exists(get("data/s20_4_words_all.txt")));
    }
    @Test
    public void testSplit20from50() throws Exception {
        final LineBasedSplitter splitter = new LineBasedSplitter(get("data/s50_0_words_all.txt"));
        splitter.split(0.2, get("data"));
        assertTrue(exists(get("data/s20_0_s50_0_words_all.txt")));
        assertTrue(exists(get("data/s20_1_s50_0_words_all.txt")));
        assertTrue(exists(get("data/s20_2_s50_0_words_all.txt")));
        assertTrue(exists(get("data/s20_3_s50_0_words_all.txt")));
        assertTrue(exists(get("data/s20_4_s50_0_words_all.txt")));
    }
}