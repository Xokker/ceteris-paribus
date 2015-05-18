package com.xokker.predictor.impl;

import com.xokker.ContextUtils;
import com.xokker.PreferenceContext;
import com.xokker.datasets.Attribute;
import com.xokker.graph.PreferenceGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.xokker.Attributes.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CeterisParibusPredictorTest {

    private PreferenceContext<Attribute> preferenceContext;
    private Set<Attribute> c6 = Stream.of(SUV, RedExterior, BrightInterior).map(ContextUtils::toAttribute).collect(Collectors.toSet());
    private Set<Attribute> c7 = Stream.of(Minivan, RedExterior, BrightInterior).map(ContextUtils::toAttribute).collect(Collectors.toSet());

    @Before
    public void setUp() throws Exception {
        PreferenceGraph preferenceGraph = ContextUtils.createPreferenceGraph();
        preferenceContext = new PreferenceContext<>(AllAttrs, preferenceGraph);
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