package ro.utcn.kdd.rosil.data;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.synchronizedCollection;

public class SyllableDataExtractor {

    public static final List<Integer> DEFAULT_BORDERS = Arrays.<Integer>asList(-2, -1, 0, 1, 2, 3);

    public List<PotentialSyllable> extractFrom(Word word, List<Integer> borders) {
        final String wordAsString = word.toString().toLowerCase();
        final List<PotentialSyllable> potentialSyllables = new ArrayList<>();
        for (int letterIndex = 0; letterIndex < wordAsString.length(); letterIndex++) {
            final List<String> features = new ArrayList<>();
            for (int borderIndex = 0; borderIndex < borders.size() - 1; borderIndex++) {
                final int beginIndex = letterIndex + borders.get(borderIndex);
                final Integer endIndex = letterIndex + borders.get(borderIndex + 1);
                features.add(safeSubstring(wordAsString, beginIndex, endIndex));
            }
            final boolean syllableStartsAt = word.syllableStartsAt(letterIndex);
            potentialSyllables.add(new PotentialSyllable(features, syllableStartsAt));
        }
        return potentialSyllables;
    }

    private String safeSubstring(String string, int beginIndex, int endIndex) {
        final StringBuilder substring = new StringBuilder();
        if (beginIndex < 0) {
            substring.append(new String(new char[min(0, endIndex) - beginIndex]).replace('\0', '-'));
        }
        if (beginIndex < string.length() && endIndex > 0) {
            substring.append(string.substring(max(beginIndex, 0), min(string.length(), endIndex)));
        }
        if (endIndex > string.length()) {
            substring.append(new String(new char[endIndex - max(string.length(), beginIndex)]).replace('\0', '-'));
        }
        return substring.toString();
    }

    public Collection<PotentialSyllable> extractFrom(List<Word> words, List<Integer> borders, boolean restrictToSet) {
        final Collection<PotentialSyllable> baseCollection = restrictToSet ? new HashSet<>() : new LinkedList<>();
        final Collection<PotentialSyllable> potentialSyllables = synchronizedCollection(baseCollection);
        words.parallelStream().forEach(word -> potentialSyllables.addAll(extractFrom(word, borders)));
        return potentialSyllables;
    }
}
