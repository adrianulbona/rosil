package ro.utcn.kdd.rosil.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.join;

public class Word {
    private final Map<Integer, String> syllables;

    public Word(final List<String> syllables) {
        this.syllables = new LinkedHashMap<>();
        int index = 0;
        for (String syllable : syllables) {
            this.syllables.put(index, syllable);
            index+=syllable.length();
        }
    }

    public String toString() {
        return join(syllables.values(), "");
    }

    public Collection<String> getSyllables() {
        return syllables.values();
    }

    public boolean syllableStartsAt(int index) {
        return syllables.containsKey(index);
    }
}
