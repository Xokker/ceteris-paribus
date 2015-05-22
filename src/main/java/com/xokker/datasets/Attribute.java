package com.xokker.datasets;

/**
 * @author Ernest Sadykov
 * @since 18.05.2015
 */
public interface Attribute {
    String getCategory();

    default boolean isNumeric() {
        return false;
    }

    default double asDouble() {
        throw new UnsupportedOperationException();
    }
}
