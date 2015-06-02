package ro.utcn.kdd.rosil.io;

import au.com.bytecode.opencsv.CSVWriter;
import ro.utcn.kdd.rosil.data.PotentialSyllable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.String.format;

public class PotentialSyllablesWriter {

    public void write(Collection<PotentialSyllable> potentialSyllables, List<Integer> bounds, Path path)
            throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            write(potentialSyllables, bounds, writer);
        }
    }

    public void write(Collection<PotentialSyllable> potentialSyllables, List<Integer> bounds, Writer writer) {
        final CSVWriter csvWriter = new CSVWriter(writer, '\t', CSVWriter.NO_QUOTE_CHARACTER);
        csvWriter.writeNext(computeHeader(bounds));
        potentialSyllables.forEach(potentialSyllable -> csvWriter.writeNext(potentialSyllable.toStringArray()));
    }

    public String[] computeHeader(List<Integer> bounds) {
        final List<String> names = new ArrayList<>();
        final String template = "%s%d%d";
        for (int i = 1; i < bounds.size(); i++) {
            final Integer previous = bounds.get(i - 1);
            final Integer current = bounds.get(i);
            if (previous < 0) {
                names.add(format(template, "b", abs(previous), abs(current)));
            } else {
                names.add(format(template, "a", previous, current));
            }
        }
        names.add("syl");
        return names.toArray(new String[names.size()]);
    }
}
