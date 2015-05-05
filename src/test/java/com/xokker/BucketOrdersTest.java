package com.xokker;

import com.google.common.collect.Multimap;
import com.xokker.datasets.Datasets;
import com.xokker.datasets.cars.CarPreferencesFileReader;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
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

    @Ignore
    @Test
    public void testBucketPivot2() throws Exception {
        BucketOrders bucketOrders = new BucketOrders(preferences.get(20));
        List<Set<Identifiable>> buckets = bucketOrders.bucketPivot();

        assertNotNull(buckets);

        List<Identifiable> flat = buckets.stream().flatMap(Collection::stream).collect(toList());
        List<Identifiable> expected = IntStream.of(0, 4, 7, 2, 5, 6, 9, 3, 1, 8)
                .boxed()
                .map(IntIdentifiable::ii)
                .collect(toList());

        assertEquals(expected, flat);
        assertFalse(buckets.isEmpty());
    }
}