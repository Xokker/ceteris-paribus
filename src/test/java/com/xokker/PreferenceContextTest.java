package com.xokker;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.Attributes.*;
import static com.xokker.IntIdentifiable.ii;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PreferenceContextTest {

    private static final HashSet<String> AllAttrs = newHashSet(Minivan, SUV, RedExterior, WhiteExterior, BrightInterior, DarkInterior);

    private PreferenceContext<String> preferenceContext;

    @Before
    public void setUp() throws Exception {
        preferenceContext = new PreferenceContext<>(AllAttrs, null);
        ContextUtils.addObjects(preferenceContext);
    }

    @Test
    public void testGetAttributeExtent1() throws Exception {
        Set<Identifiable> extent = preferenceContext.getAttributeExtent(newHashSet(SUV));

        assertNotNull(extent);
        assertEquals(newHashSet(ii(2 - 1), ii(4 - 1)), extent);
    }

    @Test
    public void testGetAttributeExtent2() throws Exception {
        Set<Identifiable> extent = preferenceContext.getAttributeExtent(newHashSet(WhiteExterior, Minivan));

        assertNotNull(extent);
        assertEquals(newHashSet(ii(1 - 1), ii(3 - 1)), extent);
    }

    @Test
    public void testGetObjectIntent1() throws Exception {
        Set<String> intent = preferenceContext.getObjectIntent(newHashSet(ii(3 - 1)));

        assertNotNull(intent);
        assertEquals(newHashSet(Minivan, WhiteExterior, BrightInterior), intent);
    }

    @Test
    public void testGetObjectIntent2() throws Exception {
        Set<String> intent = preferenceContext.getObjectIntent(newHashSet(ii(4 - 1), ii(5 - 1)));

        assertNotNull(intent);
        assertEquals(newHashSet(RedExterior, DarkInterior), intent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddObjectException() throws Exception {
        preferenceContext.addObject(ii(6), newHashSet("wrong attribute"));
        preferenceContext.getAttributeExtent(newHashSet(WhiteExterior, "wrong attribute"));
    }

}