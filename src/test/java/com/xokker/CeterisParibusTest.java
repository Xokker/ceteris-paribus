package com.xokker;

import com.xokker.graph.PreferenceGraph;
import com.xokker.graph.impl.ArrayPreferenceGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.Attributes.*;
import static com.xokker.IntIdentifiable.ii;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CeterisParibusTest {

    private HashSet<String> allAttrs = newHashSet(Minivan, SUV, RedExterior, WhiteExterior, BrightInterior, DarkInterior);
    private PreferenceContext<String> preferenceContext;
    private HashSet<String> c6 = newHashSet(SUV, RedExterior, BrightInterior);
    private HashSet<String> c7 = newHashSet(Minivan, RedExterior, BrightInterior);

    @Before
    public void setUp() throws Exception {
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
        preferenceContext = new PreferenceContext<>(allAttrs, preferenceGraph);
        ContextUrils.addObjects(preferenceContext);
    }

    @Test
    public void testPredictPreference1() throws Exception {
        boolean res = new CeterisParibus<>(preferenceContext).predictPreference(c6, c7);
        assertTrue(res);
    }

    @Test
    public void testPredictPreference2() throws Exception {
        boolean res = new CeterisParibus<>(preferenceContext).predictPreference(c7, c6);
        assertFalse(res);
    }
}