package com.xokker.predictor.impl;

import com.xokker.Attributes;
import com.xokker.ContextUtils;
import com.xokker.PreferenceContext;
import org.junit.Test;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.Attributes.*;
import static org.junit.Assert.assertFalse;

public class J48PredictorTest {

    private Set<String> c6 = newHashSet(SUV, RedExterior, BrightInterior);
    private Set<String> c7 = newHashSet(Minivan, RedExterior, BrightInterior);

    @Test
    public void test1() throws Exception {
        PreferenceContext<String> prefContext = new PreferenceContext<>(Attributes.AllAttrs, ContextUtils.createPreferenceGraph());
        ContextUtils.addObjects(prefContext);

        J48Predictor<String> predictor = new J48Predictor<>(prefContext);
        Set<Support> res = predictor.predictPreference(c6, c7);
        assertFalse(res.isEmpty());
    }
}