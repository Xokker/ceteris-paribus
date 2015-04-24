package com.xokker.graph;

import com.xokker.Identifiable;

import java.util.List;

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

}
