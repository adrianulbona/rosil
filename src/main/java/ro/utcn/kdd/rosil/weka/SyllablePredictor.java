package ro.utcn.kdd.rosil.weka;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SyllablePredictor {

    public SyllablePredictor(Path trainingDataPath) throws Exception {
        buildModel(trainingDataPath);
    }

    private void buildModel(Path trainingDataPath) throws Exception {
        final Instances instances = loadTrainingInstances(trainingDataPath);
        instances.setClassIndex(instances.numAttributes() - 1);

        final RandomForest classifier = new RandomForest();
        classifier.setNumTrees(5);


        classifier.buildClassifier(instances.trainCV(20,1));
    }

    private Instances loadTrainingInstances(Path trainingDataPath) throws Exception {
        final CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(trainingDataPath.toFile());
        csvLoader.setFieldSeparator("\t");
        final Instances dataSet = csvLoader.getDataSet();
        dataSet.randomize(new Random(0));
        return dataSet;
    }

    public List<Integer> predict(String rawWord) {
        return Collections.singletonList(-1);
    }
}
