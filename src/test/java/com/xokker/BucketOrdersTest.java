package com.xokker;

import com.google.common.collect.Multimap;
import com.xokker.datasets.Datasets;
import com.xokker.datasets.cars.CarPreferencesFileReader;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class BucketOrdersTest {

    private Multimap<Integer, PrefEntry> preferences;

    @Before
    public void setUp() throws Exception {
        preferences = CarPreferencesFileReader.readPreferences(Datasets.Cars1.getPrefsPath());
    }

    @Test
    public void testPairOrderMatrix() throws Exception {
        BucketOrders bucketOrders = new BucketOrders(preferences.get(3));
        RealMatrix pairOrder = bucketOrders.pairOrderMatrix();

        assertNotNull(pairOrder);
        assertEquals(10, pairOrder.getRowDimension());
        assertEquals(10, pairOrder.getColumnDimension());
    }

    @Test
    public void testBucketPivot1() throws Exception {
        BucketOrders bucketOrders = new BucketOrders(preferences.get(4));
        List<Set<Identifiable>> buckets = bucketOrders.bucketPivot();

        assertNotNull(buckets);
        assertFalse(buckets.isEmpty());
    }
}