package com.xokker;

import com.xokker.graph.PreferenceGraph;
import com.xokker.predictor.impl.CeterisParibusPredictor;
import com.xokker.predictor.impl.Support;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.Attributes.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CeterisParibusPredictorTest {

    private HashSet<String> allAttrs = newHashSet(Minivan, SUV, RedExterior, WhiteExterior, BrightInterior, DarkInterior);
    private PreferenceContext<String> preferenceContext;
    private HashSet<String> c6 = newHashSet(SUV, RedExterior, BrightInterior);
    private HashSet<String> c7 = newHashSet(Minivan, RedExterior, BrightInterior);

    @Before
    public void setUp() throws Exception {
        PreferenceGraph preferenceGraph = ContextUtils.createPreferenceGraph();
        preferenceContext = new PreferenceContext<>(allAttrs, preferenceGraph);
        ContextUtils.addObjects(preferenceContext);
    }

    @Test
    public void testPredictPreference1() throws Exception {
        Set<Support> res = new CeterisParibusPredictor<>(preferenceContext).predictPreference(c6, c7);
        assertFalse(res.isEmpty());
    }

    @Test
    public void testPredictPreference2() throws Exception {
        Set<Support> res = new CeterisParibusPredictor<>(preferenceContext).predictPreference(c7, c6);
        assertTrue(res.isEmpty());
    }
}