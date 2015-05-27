package com.xokker.datasets.sushi;

import com.google.common.collect.ImmutableMap;
import com.xokker.datasets.Attribute;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.Map;

/**
 * @author Ernest Sadykov
 * @since 27.05.2015
 */
public class SushiAttribute implements Attribute<SushiAttribute> {

    private static final Map<Pair<String, String>, String> convert =
            ImmutableMap.<Pair<String, String>, String>builder()
                    .put(Pair.of("0", "style"), "maki")
                    .put(Pair.of("1", "style"), "not maki")
                    .put(Pair.of("0", "major group"), "seafood")
                    .put(Pair.of("1", "major group"), "not seafood")
                    .put(Pair.of("0", "minor group"), "aomono (blue-skinned fish)")
                    .put(Pair.of("1", "minor group"), "akami (red meat fish)")
                    .put(Pair.of("2", "minor group"), "shiromi (white-meat fish)")
                    .put(Pair.of("3", "minor group"), "tare (something like baste; for eel or sea eel)")
                    .put(Pair.of("4", "minor group"), "clam or shell")
                    .put(Pair.of("5", "minor group"), "squid or octopus")
                    .put(Pair.of("6", "minor group"), "shrimp or crab")
                    .put(Pair.of("7", "minor group"), "roe")
                    .put(Pair.of("8", "minor group"), "other seafood")
                    .put(Pair.of("9", "minor group"), "egg")
                    .put(Pair.of("10", "minor group"), "meat other than fish")
                    .put(Pair.of("11", "minor group"), "vegetables")
                    .build();

    private static final Comparator<SushiAttribute> comparator =
            Comparator.comparingDouble(SushiAttribute::asDouble);

    private final String category;
    private final String value;

    public SushiAttribute(String category, String value) {
        this.category = category;
        this.value = value;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public boolean isNumeric() {
        return !convert.containsKey(Pair.of(value, category));
    }

    @Override
    public Comparator<SushiAttribute> comparator() {
        return isNumeric() ? comparator : Attribute.super.comparator();
    }

    @Override
    public double asDouble() {
        if (isNumeric()) {
            return Double.parseDouble(value);
        }
        throw new UnsupportedOperationException("asDouble on nominal attribute (" + getCategory() + ")");
    }

    public static SushiAttribute get(String category, String value) {
        return new SushiAttribute(category, value);
    }

    @Override
    public boolean equals(Object o) {
        // TODO: maybe numeric should check only category?
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SushiAttribute that = (SushiAttribute) o;

        return category.equals(that.category) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + value.hashCode();

        return result;
    }

    @Override
    public String toString() {
        if (isNumeric()) {
            return category + "=" + value;
        } else {
            return convert.get(Pair.of(value, category));
        }
    }
}
