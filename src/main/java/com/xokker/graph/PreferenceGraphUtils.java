package com.xokker.graph;

import com.xokker.Identifiable;

import java.util.List;
import java.util.Set;

/**
 * @author Ernest Sadykov
 * @since 24.04.2015
 */
public class PreferenceGraphUtils {

    public static void initLinearOrder(PreferenceGraph graph, List<Identifiable> order) {
        for (int i = 0; i < order.size(); i++) {
            for (int j = i + 1; j < order.size(); j++) {
                graph.setLeq(order.get(i), order.get(j));
            }
        }
    }

    /**
     * O(n^2) where n – number of Identifiables
     */
    public static void initBucketOrder(PreferenceGraph graph, List<Set<Identifiable>> order) {
        for (int i = 0; i < order.size(); i++) {
            Set<Identifiable> bucket = order.get(i);
            for (Identifiable left : bucket) {
                for (int j = i + 1; j < order.size(); j++) {
                    for (Identifiable right : order.get(j)) {
                        graph.setLeq(left, right);
                    }
                }
            }
        }
    }

}
