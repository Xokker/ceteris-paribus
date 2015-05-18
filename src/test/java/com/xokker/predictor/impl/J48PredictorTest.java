package com.xokker.predictor.impl;

import com.xokker.Attributes;
import com.xokker.ContextUtils;
import com.xokker.PreferenceContext;
import com.xokker.datasets.Attribute;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.xokker.Attributes.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class J48PredictorTest {

    private Set<Attribute> c6 = Stream.of(SUV, RedExterior, BrightInterior).map(ContextUtils::toAttribute).collect(Collectors.toSet());
    private Set<Attribute> c7 = Stream.of(Minivan, RedExterior, BrightInterior).map(ContextUtils::toAttribute).collect(Collectors.toSet());

    @Test
    public void testPredictPreference1() throws Exception {
        PreferenceContext<Attribute> prefContext = new PreferenceContext<>(Attributes.AllAttrs, ContextUtils.createPreferenceGraph());
        ContextUtils.addObjects(prefContext);

        WekaPredictor<Attribute> predictor = new J48Predictor<>(prefContext);
        Set<Support> res = predictor.predictPreference(c6, c7);
        assertFalse(res.isEmpty());
    }

    @Test
    public void testPredictPreference2() throws Exception {
        PreferenceContext<Attribute> prefContext = new PreferenceContext<>(Attributes.AllAttrs, ContextUtils.createPreferenceGraph());
        ContextUtils.addObjects(prefContext);

        WekaPredictor<Attribute> predictor = new J48Predictor<>(prefContext);
        Set<Support> res = predictor.predictPreference(c7, c6);
        assertTrue(res.isEmpty());
    }
}