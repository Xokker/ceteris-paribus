package com.xokker.graph.impl;

import com.xokker.graph.PreferenceGraph;

/**
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public class ArrayPreferenceGraph<I extends Number> implements PreferenceGraph<I> {

    private final boolean[][] preferences;

    public ArrayPreferenceGraph(int numberOfDifferentElements) {
        preferences = new boolean[numberOfDifferentElements][];
        for (int i = 0; i < numberOfDifferentElements; i++) {
            preferences[i] = new boolean[numberOfDifferentElements];
            preferences[i][i] = true;
        }
    }

    public ArrayPreferenceGraph(boolean[][] preferences) {
        this.preferences = preferences;
    }

    @Override
    public void setLeq(I left, I right) {
        preferences[left.intValue()][right.intValue()] = true;
    }

    @Override
    public boolean leq(I left, I right) {
        return preferences[left.intValue()][right.intValue()];
    }

}
