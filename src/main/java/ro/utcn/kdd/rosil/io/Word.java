package ro.utcn.kdd.rosil.io;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.join;

public class Word {
	private final Map<Integer, String> syllables;

	public Word(final List<String> syllables) {
		this.syllables = new LinkedHashMap<>();
		int index = 0;
		for (String syllable : syllables) {
			this.syllables.put(index, syllable);
			index += syllable.length();
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

	public Set<Integer> getSyllableIndexes() {
		return syllables.keySet();
	}

	public String toSyllabifiedString() {
		return join(syllables.values(), "-");
	}

}
