package com.xokker;

import com.google.common.collect.Multimap;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BucketOrdersTest {

    Multimap<Integer, PrefEntry> preferences;

    @Before
    public void setUp() throws Exception {
        preferences = PreferenceFileReader.readFromFile(Datasets.Cars1);
    }

    @Test
    public void testPairOrderMatrix() throws Exception {
        BucketOrders bucketOrders = new BucketOrders(preferences.get(3));
        RealMatrix pairOrder = bucketOrders.pairOrderMatrix();

        assertNotNull(pairOrder);
        assertEquals(10, pairOrder.getRowDimension());
        assertEquals(10, pairOrder.getColumnDimension());
    }

}