package ro.utcn.kdd.rosil.weka;

import org.junit.Test;

import java.nio.file.Paths;

public class SyllablePredictorTest {

    @Test
    public void testPredict() throws Exception {
        final SyllablePredictor predictor = new SyllablePredictor(Paths.get("data/b21b10a01a12a23syl.csv"));
        predictor.predict("alambicat");
    }
}