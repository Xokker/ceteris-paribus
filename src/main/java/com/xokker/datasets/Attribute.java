package com.xokker.datasets;

import java.util.Comparator;

/**
 * @author Ernest Sadykov
 * @since 18.05.2015
 */
public interface Attribute<T extends Attribute> {

    String getCategory();

    default boolean isNumeric() {
        return false;
    }

    default Comparator<T> comparator() {
        assert !isNumeric(): "comparator() must be implemented!";
        throw new UnsupportedOperationException();
    }

    default double asDouble() {
        throw new UnsupportedOperationException();
    }
}
