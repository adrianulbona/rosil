package ro.utcn.kdd.rosil.weka;

import ro.utcn.kdd.rosil.data.PotentialSyllable;
import ro.utcn.kdd.rosil.data.SyllableDataExtractor;
import ro.utcn.kdd.rosil.data.Word;
import ro.utcn.kdd.rosil.io.PotentialSyllablesWriter;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SyllablePredictor {

    private Classifier classifier;
    private Instances trainSet;
    private Instances testSet;

    public SyllablePredictor(Path trainingDataPath, int splitNumber) throws Exception {
        try (InputStream inputStream = Files.newInputStream(trainingDataPath)) {
            final Instances completeDataSet = loadTrainingInstances(inputStream, true);
            final Instances trainSplits = completeDataSet.trainCV(2, 0);
            this.testSet = completeDataSet.testCV(2, 0);
            double maxPrecision = 0.0;
            for (int i = 0; i < splitNumber; i++) {
                final Instances trainCVSet = trainSplits.trainCV(5, i);
                final Instances testCVSet = trainSplits.testCV(5, i);
                final Classifier classifier = buildClassifier(completeDataSet.numAttributes() - 1, trainCVSet);
                final Evaluation evaluation = new Evaluation(trainCVSet);
                evaluation.evaluateModel(classifier, testCVSet);
                final double precision = evaluation.weightedPrecision();
                if (precision > maxPrecision) {
                    maxPrecision = precision;
                    this.trainSet = trainCVSet;
                    this.classifier = classifier;
                }
            }
        }
    }

    private Instances loadTrainingInstances(InputStream inputStream, boolean randomize) throws IOException {
        final CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(inputStream);
        csvLoader.setFieldSeparator("\t");
        final Instances instances = csvLoader.getDataSet();
        if (randomize) {
            instances.randomize(new Random());
        }
        final int attributeCount = instances.numAttributes() - 1;
        instances.setClassIndex(attributeCount);
        return instances;
    }

    private Classifier buildClassifier(int attributeCount, Instances trainCVSet) throws Exception {
        final RandomForest classifier = new RandomForest();
        classifier.setNumTrees(attributeCount);
        classifier.setNumFeatures(attributeCount);


/*
        final SMO classifier = new SMO();
*/
        classifier.buildClassifier(trainCVSet);

        return classifier;
    }

    public Evaluation testClassifier() throws Exception {
        final Evaluation evaluation = new Evaluation(this.trainSet);
        evaluation.evaluateModel(this.classifier, testSet);
        return evaluation;
    }

    public String predict(String rawWord) throws Exception {
        final Word word = new Word(Collections.singletonList(rawWord));
        final SyllableDataExtractor extractor = new SyllableDataExtractor();
        final List<PotentialSyllable> potentialSyllables = extractor.extractFrom(word, SyllableDataExtractor.DEFAULT_BORDERS);
        final StringWriter stringWriter = new StringWriter();
        new PotentialSyllablesWriter().write(potentialSyllables, SyllableDataExtractor.DEFAULT_BORDERS, stringWriter);

        final byte[] bytes = stringWriter.toString().getBytes(StandardCharsets.UTF_8);
        final InputStream stream = new ByteArrayInputStream(bytes);
        final Instances instances = loadTrainingInstances(stream, false);
        instances.setClassIndex(instances.numAttributes() - 1);
        final StringBuilder result = new StringBuilder();
        for (Instance instance : instances) {
            final double classIndex = this.classifier.classifyInstance(instance);
            //System.out.println(instance + "->" + this.trainSet.classAttribute().value((int) classIndex));
            if (Boolean.valueOf(this.trainSet.classAttribute().value((int) classIndex))) {
                result.append("-");
            }
            result.append(instance.toString(2));
        }

        return result.toString();
    }
}
