package com.xokker;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PreferenceContextTest {

    private static final String Minivan = "minivan";
    private static final String SUV = "SUV";
    private static final String RedExterior = "red exterior";
    private static final String WhiteExterior = "white exterior";
    private static final String BrightInterior = "bright interior";
    private static final String DarkInterior = "dark interior";

    private HashSet<String> allAttrs = newHashSet(Minivan, SUV, RedExterior, WhiteExterior, BrightInterior, DarkInterior);

    @Test
    public void testGetAttributeExtent1() throws Exception {
        PreferenceContext<Integer, String> preferenceContext = new PreferenceContext<>(allAttrs);
        addObjects(preferenceContext);
        Set<Integer> extent = preferenceContext.getAttributeExtent(newHashSet(SUV));

        assertNotNull(extent);
        assertEquals(newHashSet(2, 4), extent);
    }

    @Test
    public void testGetAttributeExtent2() throws Exception {
        PreferenceContext<Integer, String> preferenceContext = new PreferenceContext<>(allAttrs);
        addObjects(preferenceContext);
        Set<Integer> extent = preferenceContext.getAttributeExtent(newHashSet(WhiteExterior, Minivan));

        assertNotNull(extent);
        assertEquals(newHashSet(1, 3), extent);
    }

    @Test
    public void testGetObjectIntent1() throws Exception {
        PreferenceContext<Integer, String> preferenceContext = new PreferenceContext<>(allAttrs);
        addObjects(preferenceContext);
        Set<String> intent = preferenceContext.getObjectIntent(newHashSet(3));

        assertNotNull(intent);
        assertEquals(newHashSet(Minivan, WhiteExterior, BrightInterior), intent);
    }

    @Test
    public void testGetObjectIntent2() throws Exception {
        PreferenceContext<Integer, String> preferenceContext = new PreferenceContext<>(allAttrs);
        addObjects(preferenceContext);
        Set<String> intent = preferenceContext.getObjectIntent(newHashSet(4, 5));

        assertNotNull(intent);
        assertEquals(newHashSet(RedExterior, DarkInterior), intent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddObjectException() throws Exception {
        PreferenceContext<Integer, String> preferenceContext = new PreferenceContext<>(allAttrs);
        addObjects(preferenceContext);
        preferenceContext.addObject(6, newHashSet("wrong attribute"));
        preferenceContext.getAttributeExtent(newHashSet(WhiteExterior, "wrong attribute"));
    }

    private void addObjects(PreferenceContext<Integer, String> context) {
        context.addObject(1, newHashSet(Minivan, WhiteExterior, DarkInterior));
        context.addObject(2, newHashSet(SUV, WhiteExterior, DarkInterior));
        context.addObject(3, newHashSet(Minivan, WhiteExterior, BrightInterior));
        context.addObject(4, newHashSet(SUV, RedExterior, DarkInterior));
        context.addObject(5, newHashSet(Minivan, RedExterior, DarkInterior));
    }
}