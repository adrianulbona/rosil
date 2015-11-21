package ro.utcn.kdd.rosil.input;

import org.junit.Test;

import static java.nio.file.Paths.get;

public class LineBasedSplitterTest {

    @Test
    public void testSplit50() throws Exception {
        final LineBasedSplitter splitter = new LineBasedSplitter(get("../data/words_no_struct.csv"));
        splitter.split(0.02, get("../data/ro"));
    }
}