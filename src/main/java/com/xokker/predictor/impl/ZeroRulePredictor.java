package com.xokker.predictor.impl;

import com.xokker.datasets.Attribute;
import com.xokker.predictor.PreferencePredictor;

import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

/**
 * @author Ernest Sadykov
 * @since 25.04.2015
 */
public class ZeroRulePredictor<A extends Attribute> implements PreferencePredictor<A> {

    private final boolean positive;

    /**
     * If positive is true, #predictPreference always returns true;
     * if positive is false, #predictPreference always returns false
     */
    public ZeroRulePredictor(boolean positive) {
        this.positive = positive;
    }

    @Override
    public Set<Support> predictPreference(Set<A> a, Set<A> b) {
        return positive ? singleton(Support.OK) : emptySet();
    }
}
