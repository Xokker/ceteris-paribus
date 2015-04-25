package com.xokker;

import java.util.Set;

/**
 * @author Ernest Sadykov
 * @since 25.04.2015
 */
public class ZeroRulePredictor<A> implements PreferencePredictor<A> {

    private final boolean positive;

    /**
     * If positive is true, #predictPreference always returns true;
     * if positive is false, #predictPreference always returns false
     */
    public ZeroRulePredictor(boolean positive) {
        this.positive = positive;
    }

    @Override
    public boolean predictPreference(Set<A> a, Set<A> b) {
        return positive;
    }
}
