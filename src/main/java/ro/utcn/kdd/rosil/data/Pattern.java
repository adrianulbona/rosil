package ro.utcn.kdd.rosil.data;

import java.util.List;

import static java.lang.String.format;

public class Pattern {
    public final List<String> elements;
    public final int support;

    public Pattern(List<String> elements, int support) {
        this.elements = elements;
        this.support = support;
    }

    @Override
    public String toString() {
        return format("%s(%s)", elements, support);
    }
}
