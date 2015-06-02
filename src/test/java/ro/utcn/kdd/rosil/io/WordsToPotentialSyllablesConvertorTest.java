package ro.utcn.kdd.rosil.io;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import org.junit.Test;
import ro.utcn.kdd.rosil.data.SyllableDataExtractor;
import ro.utcn.kdd.rosil.data.Word;
import ro.utcn.kdd.rosil.weka.SyllablePredictor;
import weka.classifiers.evaluation.Evaluation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.join;

public class WordsToPotentialSyllablesConvertorTest {

    @Test
    public void testConvert() throws IOException {
        final List<List<Integer>> bordersList = buildBordersList();


        System.out.println("building training files.");

        final Path trainDataPath = Paths.get("data/s20s50_0/s20_0_s50_0_words_all.txt");
        final Path trainDestination = Paths.get("data/set/train");
        final Table<Integer, Integer, Path> trainFiles = prepareDataSets(bordersList, trainDataPath, trainDestination);

        System.out.println("building test files.");
        final Path testDataPath = Paths.get("data/s20s50_0/s20_1_s50_0_words_all.txt");
        final Path testDestination = Paths.get("data/all/test");
        final Table<Integer, Integer, Path> testFiles = prepareDataSets(bordersList, testDataPath, testDestination);

        final Table<Integer, Integer, Double> results = evaluate(trainFiles, testFiles);
        printResults(results);
    }

    private Table<Integer, Integer, Double> evaluate(Table<Integer, Integer, Path> trainFiles, Table<Integer, Integer, Path> testFiles) {

        final Table<Integer, Integer, Double> results = TreeBasedTable.create();
        trainFiles.cellSet().parallelStream().forEach(cell -> {
                    final int offset = cell.getRowKey();
                    final int size = cell.getColumnKey();
                    final Path trainPath = trainFiles.get(offset, size);
                    final Path testPath = testFiles.get(offset, size);
                    final double weightedPrecision = evaluate(trainPath);
                    synchronized (results) {
                        results.put(offset, size, weightedPrecision);
                    }
                }
        );
        return results;
    }

    private void printResults(Table<Integer, Integer, Double> results) {
        results.columnKeySet().forEach(value -> System.out.printf("\t%10d", value));
        System.out.println();
        for (Integer offset : results.rowKeySet()) {
            System.out.print(offset);
            for (Integer size : results.columnKeySet()) {
                System.out.printf("\t%.3f", results.get(offset, size));
            }
            System.out.println();
        }
    }

    private Table<Integer, Integer, Path> prepareDataSets(List<List<Integer>> bordersList, Path wordsPath, Path destinationFolder) {
        final Table<Integer, Integer, Path> dataSets = TreeBasedTable.create();
        bordersList.parallelStream().forEach(borders -> {
            System.out.println("starting processing of : " + borders);
            final Path csvFile = writePotentialSyllables(destinationFolder, wordsPath, borders);
            synchronized (dataSets) {
                dataSets.put(borders.get(0), borders.size() - 1, csvFile);
            }
        });
        return dataSets;
    }

    private Path writePotentialSyllables(Path destinationFolder, Path wordsPath, List<Integer> borders) {
        final PotentialSyllablesWriter writer = new PotentialSyllablesWriter();
        final String filename = join(writer.computeHeader(borders)) + ".csv";
        final Path csvFile = destinationFolder.resolve(filename);
        if (!Files.exists(csvFile)) {
            try {
                final List<Word> words = new WordsReader().read(wordsPath);
                writer.write(new SyllableDataExtractor().extractFrom(words, borders, false), borders, csvFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    private double evaluate(Path trainDataPath) {
        try {
            System.out.println("started to evaluate: " + trainDataPath);
            final SyllablePredictor predictor = new SyllablePredictor(trainDataPath, 5);
            final Evaluation evaluation = predictor.testClassifier();
            System.out.println(evaluation.toClassDetailsString());
            return evaluation.weightedPrecision();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private List<List<Integer>> buildBordersList() {
        final List<List<Integer>> bordersList = new ArrayList<>();
        bordersList.add(Arrays.<Integer>asList(-3, -2, -1));
        bordersList.add(Arrays.<Integer>asList(-2, -1, 0));
        bordersList.add(Arrays.<Integer>asList(-1, 0, 1));
        bordersList.add(Arrays.<Integer>asList(0, 1, 2));
        bordersList.add(Arrays.<Integer>asList(1, 2, 3));
        bordersList.add(Arrays.<Integer>asList(2, 3, 4));
        bordersList.add(Arrays.<Integer>asList(3, 4, 5));

        bordersList.add(Arrays.<Integer>asList(-3, -2, -1, 0));
        bordersList.add(Arrays.<Integer>asList(-2, -1, 0, 1));
        bordersList.add(Arrays.<Integer>asList(-1, 0, 1, 2));
        bordersList.add(Arrays.<Integer>asList(0, 1, 2, 3));
        bordersList.add(Arrays.<Integer>asList(1, 2, 3, 4));
        bordersList.add(Arrays.<Integer>asList(2, 3, 4, 5));
        bordersList.add(Arrays.<Integer>asList(3, 4, 5, 6));

        bordersList.add(Arrays.<Integer>asList(-3, -2, -1, 0, 1));
        bordersList.add(Arrays.<Integer>asList(-2, -1, 0, 1, 2));
        bordersList.add(Arrays.<Integer>asList(-1, 0, 1, 2, 3));
        bordersList.add(Arrays.<Integer>asList(0, 1, 2, 3, 4));
        bordersList.add(Arrays.<Integer>asList(1, 2, 3, 4, 5));
        bordersList.add(Arrays.<Integer>asList(2, 3, 4, 5, 6));
        bordersList.add(Arrays.<Integer>asList(3, 4, 5, 6, 7));

        bordersList.add(Arrays.<Integer>asList(-3, -2, -1, 0, 1, 2));
        bordersList.add(Arrays.<Integer>asList(-2, -1, 0, 1, 2, 3));
        bordersList.add(Arrays.<Integer>asList(-1, 0, 1, 2, 3, 4));
        bordersList.add(Arrays.<Integer>asList(0, 1, 2, 3, 4, 5));
        bordersList.add(Arrays.<Integer>asList(1, 2, 3, 4, 5, 6));
        bordersList.add(Arrays.<Integer>asList(2, 3, 4, 5, 6, 7));
        bordersList.add(Arrays.<Integer>asList(3, 4, 5, 6, 7, 8));


        bordersList.add(Arrays.<Integer>asList(-3, -2, -1, 0, 1, 2, 3));
        bordersList.add(Arrays.<Integer>asList(-2, -1, 0, 1, 2, 3, 4));
        bordersList.add(Arrays.<Integer>asList(-1, 0, 1, 2, 3, 4, 5));
        bordersList.add(Arrays.<Integer>asList(0, 1, 2, 3, 4, 5, 6));
        bordersList.add(Arrays.<Integer>asList(1, 2, 3, 4, 5, 6, 7));
        bordersList.add(Arrays.<Integer>asList(2, 3, 4, 5, 6, 7, 8));
        bordersList.add(Arrays.<Integer>asList(3, 4, 5, 6, 7, 8, 9));
        return bordersList;
    }
}