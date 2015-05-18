package com.xokker;

import com.xokker.datasets.Attribute;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.Attributes.*;
import static com.xokker.ContextUtils.toAttribute;
import static com.xokker.IntIdentifiable.ii;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PreferenceContextTest {

    private static final Set<Attribute> AllAttrs = Stream.of(Minivan, SUV, RedExterior, WhiteExterior, BrightInterior, DarkInterior)
            .map(ContextUtils::toAttribute)
            .collect(toSet());

    private PreferenceContext<Attribute> preferenceContext;

    @Before
    public void setUp() throws Exception {
        preferenceContext = new PreferenceContext<>(AllAttrs, null);
        ContextUtils.addObjects(preferenceContext);
    }

    @Test
    public void testGetAttributeExtent1() throws Exception {
        Set<Identifiable> extent = preferenceContext.getAttributeExtent(toAttribute(SUV));

        assertNotNull(extent);
        assertEquals(newHashSet(ii(2 - 1), ii(4 - 1)), extent);
    }

    @Test
    public void testGetAttributeExtent2() throws Exception {
        Set<Identifiable> extent = preferenceContext.getAttributeExtent(newHashSet(toAttribute(WhiteExterior), toAttribute(Minivan)));

        assertNotNull(extent);
        assertEquals(newHashSet(ii(1 - 1), ii(3 - 1)), extent);
    }

    @Test
    public void testGetObjectIntent1() throws Exception {
        Set<Attribute> intent = preferenceContext.getObjectIntent(newHashSet(ii(3 - 1)));

        assertNotNull(intent);
        assertEquals(Stream.of(Minivan, WhiteExterior, BrightInterior).map(ContextUtils::toAttribute).collect(toSet()), intent);
    }

    @Test
    public void testGetObjectIntent2() throws Exception {
        Set<Attribute> intent = preferenceContext.getObjectIntent(newHashSet(ii(4 - 1), ii(5 - 1)));

        assertNotNull(intent);
        assertEquals(Stream.of(RedExterior, DarkInterior).map(ContextUtils::toAttribute).collect(toSet()), intent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddObjectException() throws Exception {
        preferenceContext.addObject(ii(6), newHashSet(toAttribute("wrong attribute")));
        preferenceContext.getAttributeExtent(newHashSet(toAttribute(WhiteExterior), toAttribute("wrong attribute")));
    }

}