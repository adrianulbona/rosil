package ro.utcn.kdd.rosil.input;

import org.junit.Test;

import static java.nio.file.Paths.get;
import static org.junit.Assert.assertTrue;

public class LineBasedSplitterTest {

    @Test
    public void testSplit50() throws Exception {
        final LineBasedSplitter splitter = new LineBasedSplitter(get("data/english/data.txt"));
        splitter.split(0.5, get("data/english"));
    }
}