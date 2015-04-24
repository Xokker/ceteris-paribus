package com.xokker;

import com.xokker.graph.PreferenceGraph;
import com.xokker.graph.impl.ArrayPreferenceGraph;

import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.Attributes.*;
import static com.xokker.IntIdentifiable.ii;

/**
 * @author Ernest Sadykov
 * @since 22.04.2015
 */
public class ContextUtils {
    public static void addObjects(PreferenceContext<String> context) {
        context.addObject(ii(1 - 1), newHashSet(Minivan, WhiteExterior, DarkInterior));
        context.addObject(ii(2 - 1), newHashSet(SUV, WhiteExterior, DarkInterior));
        context.addObject(ii(3 - 1), newHashSet(Minivan, WhiteExterior, BrightInterior));
        context.addObject(ii(4 - 1), newHashSet(SUV, RedExterior, DarkInterior));
        context.addObject(ii(5 - 1), newHashSet(Minivan, RedExterior, DarkInterior));
    }

    public static PreferenceGraph createPreferenceGraph() {
        PreferenceGraph preferenceGraph = new ArrayPreferenceGraph(5);
        preferenceGraph.setLeq(ii(1 - 1), ii(5 - 1));
        preferenceGraph.setLeq(ii(2 - 1), ii(5 - 1));
        preferenceGraph.setLeq(ii(3 - 1), ii(5 - 1));
        preferenceGraph.setLeq(ii(4 - 1), ii(5 - 1));
        preferenceGraph.setLeq(ii(2 - 1), ii(1 - 1));
        preferenceGraph.setLeq(ii(3 - 1), ii(1 - 1));
        preferenceGraph.setLeq(ii(4 - 1), ii(1 - 1));
        preferenceGraph.setLeq(ii(4 - 1), ii(2 - 1));
        preferenceGraph.setLeq(ii(4 - 1), ii(3 - 1));

        return preferenceGraph;
    }
}
