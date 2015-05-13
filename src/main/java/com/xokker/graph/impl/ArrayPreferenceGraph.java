package com.xokker.graph.impl;

import com.google.common.base.Preconditions;
import com.xokker.Identifiable;
import com.xokker.graph.PrefState;
import com.xokker.graph.PreferenceGraph;

import java.util.Arrays;

/**
 * Matrix-based implementation of preference graph
 *
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public class ArrayPreferenceGraph implements PreferenceGraph {

    private final PrefState[][] preferences;

    public ArrayPreferenceGraph(int numberOfDifferentElements) {
        preferences = new PrefState[numberOfDifferentElements][];
        for (int i = 0; i < numberOfDifferentElements; i++) {
            preferences[i] = new PrefState[numberOfDifferentElements];
            for (int j = 0; j < preferences[i].length; j++) {
                preferences[i][j] = PrefState.Unknown;
            }
            preferences[i][i] = PrefState.Leq;
        }
    }

    public ArrayPreferenceGraph(PrefState[][] preferences) {
        this.preferences = preferences;
    }

    public ArrayPreferenceGraph(ArrayPreferenceGraph graph) {
        this(copyPrefs(graph.preferences));
    }

    private static PrefState[][] copyPrefs(PrefState[][] preferences) {
        int numberOfDifferentElements = preferences.length;
        PrefState[][] newPref = new PrefState[numberOfDifferentElements][];
        for (int i = 0; i < numberOfDifferentElements; i++) {
            newPref[i] = Arrays.copyOf(preferences[i], preferences[i].length);
        }

        return newPref;
    }

    @Override
    public void setLeq(Identifiable left, Identifiable right) {
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
        assert !left.equals(right);

        preferences[left.getId()][right.getId()] = PrefState.Leq;
        preferences[right.getId()][left.getId()] = PrefState.NotLeq;
    }

    @Override
    public PrefState leq(Identifiable left, Identifiable right) {
        return preferences[left.getId()][right.getId()];
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (PrefState[] preference : preferences) {
            for (PrefState b : preference) {
                res.append(b).append(" ");
            }
            res.append("\n");
        }

        return res.toString();
    }

}
