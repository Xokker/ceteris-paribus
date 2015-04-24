package com.xokker.graph.impl;

import com.xokker.Identifiable;
import com.xokker.graph.PreferenceGraph;

/**
 * Matrix-based implementation of preference graph
 *
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public class ArrayPreferenceGraph implements PreferenceGraph {

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
    public void setLeq(Identifiable left, Identifiable right) {
        preferences[left.getId()][right.getId()] = true;
    }

    @Override
    public boolean leq(Identifiable left, Identifiable right) {
        return preferences[left.getId()][right.getId()];
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (boolean[] preference : preferences) {
            for (boolean b : preference) {
                res.append(b).append(" ");
            }
            res.append("\n");
        }

        return res.toString();
    }

}
