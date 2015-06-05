package ro.utcn.kdd.rosil.weka;

import org.junit.Test;

import java.nio.file.Paths;

public class SyllablePredictorTest {

    @Test
    public void testPredict() throws Exception {
        final SyllablePredictor predictor1 = new SyllablePredictor(Paths.get("data/all/train/b21b10a01a12a23syl.csv"), 5);
        funny(predictor1);
    }

    private void funny(SyllablePredictor predictor) throws Exception {
        System.out.println(predictor.testClassifier().toClassDetailsString());
        System.out.println(predictor.predict("abecedar"));
        System.out.println(predictor.predict("castravete"));
        System.out.println(predictor.predict("analfabet"));
        System.out.println(predictor.predict("gladiator"));
        System.out.println(predictor.predict("programator"));
        System.out.println(predictor.predict("usturoi"));
    }
}