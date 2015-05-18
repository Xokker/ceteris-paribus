package com.xokker;

import com.xokker.datasets.Attribute;
import com.xokker.graph.PreferenceGraph;
import com.xokker.graph.impl.ArrayPreferenceGraph;

import java.util.Objects;

import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.Attributes.*;
import static com.xokker.IntIdentifiable.ii;

/**
 * @author Ernest Sadykov
 * @since 22.04.2015
 */
public class ContextUtils {
    public static void addObjects(PreferenceContext<Attribute> context) {
        context.addObject(ii(1 - 1), newHashSet(toAttribute(Minivan), toAttribute(WhiteExterior), toAttribute(DarkInterior)));
        context.addObject(ii(2 - 1), newHashSet(toAttribute(SUV), toAttribute(WhiteExterior), toAttribute(DarkInterior)));
        context.addObject(ii(3 - 1), newHashSet(toAttribute(Minivan), toAttribute(WhiteExterior), toAttribute(BrightInterior)));
        context.addObject(ii(4 - 1), newHashSet(toAttribute(SUV), toAttribute(RedExterior), toAttribute(DarkInterior)));
        context.addObject(ii(5 - 1), newHashSet(toAttribute(Minivan), toAttribute(RedExterior), toAttribute(DarkInterior)));
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

    public static Attribute toAttribute(String s) {
        Objects.requireNonNull(s);

        return new Attribute() {
            @Override
            public String getCategory() {
                return null;
            }

            @Override
            public String toString() {
                return s;
            }

            @Override
            public int hashCode() {
                return s.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null) return false;
                if (!(obj instanceof Attribute)) return false;

                return obj.toString().equals(s);
            }
        };
    }
}
