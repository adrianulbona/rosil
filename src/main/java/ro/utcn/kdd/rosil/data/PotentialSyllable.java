package ro.utcn.kdd.rosil.data;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PotentialSyllable implements Comparable<PotentialSyllable> {
    public final List<String> features;
    public final boolean isSyllable;

    public PotentialSyllable(List<String> features, boolean isSyllable) {
        this.features = features;
        this.isSyllable = isSyllable;
    }

    public String[] toStringArray() {
        final String[] strings = this.features.toArray(new String[this.features.size() + 1]);
        strings[this.features.size()] = Boolean.toString(this.isSyllable);
        return strings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotentialSyllable that = (PotentialSyllable) o;
        return Objects.equals(isSyllable, that.isSyllable) &&
                Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(features, isSyllable);
    }

    @Override
    public int compareTo(PotentialSyllable other) {
        if (other == null) {
            return 1;
        }
        if (this.equals(other)) {
            return 0;
        }
        final Iterator<String> iterator = other.features.iterator();
        for (String feature : this.features) {
            if (!iterator.hasNext()) {
                return 1;
            }
            final String nextFeatureFromOther = iterator.next();
            if (!feature.equals(nextFeatureFromOther)) {
                return feature.compareTo(nextFeatureFromOther);
            }
        }
        return Boolean.valueOf(isSyllable).compareTo(other.isSyllable);
    }
}
