package ro.utcn.kdd.rosil.io;

import org.junit.Test;

import java.nio.file.Paths;

/**
 * Created by adibo on 5/7/2015.
 */
public class WordsReaderTest {

    @Test
    public void testRead() throws Exception {
        new WordsReader().read(Paths.get("data/RoSyllabiDict.txt"));
    }
}