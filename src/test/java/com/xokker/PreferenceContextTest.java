package com.xokker;

import com.xokker.graph.PreferenceGraph;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.Attributes.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PreferenceContextTest {

    private HashSet<String> allAttrs = newHashSet(Minivan, SUV, RedExterior, WhiteExterior, BrightInterior, DarkInterior);
    private PreferenceGraph<Integer> preferenceGraph = null;

    @Test
    public void testGetAttributeExtent1() throws Exception {
        PreferenceContext<Integer, String> preferenceContext = new PreferenceContext<>(allAttrs, preferenceGraph);
        ContextUrils.addObjects(preferenceContext);
        Set<Integer> extent = preferenceContext.getAttributeExtent(newHashSet(SUV));

        assertNotNull(extent);
        assertEquals(newHashSet(2, 4), extent);
    }

    @Test
    public void testGetAttributeExtent2() throws Exception {
        PreferenceContext<Integer, String> preferenceContext = new PreferenceContext<>(allAttrs, preferenceGraph);
        ContextUrils.addObjects(preferenceContext);
        Set<Integer> extent = preferenceContext.getAttributeExtent(newHashSet(WhiteExterior, Minivan));

        assertNotNull(extent);
        assertEquals(newHashSet(1, 3), extent);
    }

    @Test
    public void testGetObjectIntent1() throws Exception {
        PreferenceContext<Integer, String> preferenceContext = new PreferenceContext<>(allAttrs, preferenceGraph);
        ContextUrils.addObjects(preferenceContext);
        Set<String> intent = preferenceContext.getObjectIntent(newHashSet(3));

        assertNotNull(intent);
        assertEquals(newHashSet(Minivan, WhiteExterior, BrightInterior), intent);
    }

    @Test
    public void testGetObjectIntent2() throws Exception {
        PreferenceContext<Integer, String> preferenceContext = new PreferenceContext<>(allAttrs, preferenceGraph);
        ContextUrils.addObjects(preferenceContext);
        Set<String> intent = preferenceContext.getObjectIntent(newHashSet(4, 5));

        assertNotNull(intent);
        assertEquals(newHashSet(RedExterior, DarkInterior), intent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddObjectException() throws Exception {
        PreferenceContext<Integer, String> preferenceContext = new PreferenceContext<>(allAttrs, preferenceGraph);
        ContextUrils.addObjects(preferenceContext);
        preferenceContext.addObject(6, newHashSet("wrong attribute"));
        preferenceContext.getAttributeExtent(newHashSet(WhiteExterior, "wrong attribute"));
    }

}