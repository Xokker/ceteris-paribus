package com.xokker.predictor;

import com.xokker.datasets.Attribute;
import com.xokker.predictor.impl.Support;

import java.util.Set;

/**
 * @author Ernest Sadykov
 * @since 25.04.2015
 */
public interface PreferencePredictor<A extends Attribute> {

    /**
     * @return true if the object with b attributes is at least as good as
     *          object with attributes a
     *         false otherwise
     */
    Set<Support> predictPreference(Set<A> a, Set<A> b);

    default void init() {
        // nop
    }

    /**
     * Positive number if the first is better than the second.
     */
    default int predict(Set<A> first, Set<A> second) {
        throw new UnsupportedOperationException();
    }
}