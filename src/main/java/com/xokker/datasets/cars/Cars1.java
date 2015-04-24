package com.xokker.datasets.cars;

import com.google.common.collect.Multimap;
import com.xokker.*;
import com.xokker.graph.PreferenceGraph;
import com.xokker.graph.impl.ArrayPreferenceGraph;
import com.xokker.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

import static com.xokker.datasets.Datasets.Cars1;
import static com.xokker.datasets.cars.CarPreferencesFileReader.*;
import static com.xokker.graph.PreferenceGraphUtils.initBucketOrder;
import static java.util.stream.Collectors.toSet;

/**
 * @author Ernest Sadykov
 * @since 24.04.2015
 */
public class Cars1 {

    private Random random = new Random();

    /**
     * Cross-validation for single user
     */
    private void crossValidation(Map<Identifiable, Set<CarAttribute>> objects, Collection<PrefEntry> preferences) {
        BucketOrders bucketOrders = new BucketOrders(preferences);
        List<Set<Identifiable>> originalBuckets = bucketOrders.bucketPivot();
        mergeTwoRandomElement(originalBuckets);
        mergeTwoRandomElement(originalBuckets);
        mergeTwoRandomElement(originalBuckets);

        for (int removedElementBucketIndex = 0; removedElementBucketIndex < originalBuckets.size(); removedElementBucketIndex++) {
            List<Set<Identifiable>> buckets = new ArrayList<>(originalBuckets);
            PreferenceGraph preferenceGraph = new ArrayPreferenceGraph(deepSize(buckets));
            initBucketOrder(preferenceGraph, buckets);

            Set<Identifiable> originalRandomBucket = buckets.get(removedElementBucketIndex);
            Set<Identifiable> randomBucket = new HashSet<>(originalRandomBucket);
            Identifiable removedElement = CollectionUtils.removeRandom(randomBucket);

            PreferenceContext<CarAttribute> context = new PreferenceContext<>(mergeSets(objects.values()), preferenceGraph);
            context.addObjects(objects);
            CeterisParibus<CarAttribute> ceterisParibus = new CeterisParibus<>(context);

            int penalty = 0;

            // if bucket index of the removed element is zero, it should be <= to everything else
            boolean after = removedElementBucketIndex == 0;

            boolean ret;
            for (Set<Identifiable> bucket : originalBuckets) {
                if (bucket.equals(originalRandomBucket)) {
                    // elements in the same bucket must be incomparable
                    for (Identifiable id : randomBucket) {
                        ret = ceterisParibus.predictPreference(objects.get(id), objects.get(removedElement));
                        if (ret) {
                            penalty++;
                        }
                        ret = ceterisParibus.predictPreference(objects.get(removedElement), objects.get(id));
                        if (ret) {
                            penalty++;
                        }
                    }
                    after = true;
                } else {
                    for (Identifiable id : bucket) {
                        ret = ceterisParibus.predictPreference(objects.get(id), objects.get(removedElement));
                        if (after && ret || !after && !ret) { // element should go before any element of the current bucket
                            penalty++;
                        }
                        ret = ceterisParibus.predictPreference(objects.get(removedElement), objects.get(id));
                        if (!after && ret || after && !ret) { // element should go after any element of the current bucket
                            penalty++;
                        }
                    }
                }
            }

            System.out.println("removedElementBucketIndex: " + removedElementBucketIndex + " penalty: " + penalty);
        }
    }

    private int deepSize(List<Set<Identifiable>> buckets) {
        return buckets.stream().mapToInt(Set::size).sum();
    }

    private void mergeTwoRandomElement(List<Set<Identifiable>> originalBuckets) {
        int i = random.nextInt(originalBuckets.size() - 1);
        Set<Identifiable> set = originalBuckets.remove(i + 1);
        Set<Identifiable> element = originalBuckets.get(i);
        element.addAll(set);
    }

    private <T> Set<T> mergeSets(Collection<Set<T>> values) {
        return values.stream()
                .flatMap(Collection::stream)
                .collect(toSet());
    }

    public static void main(String[] args) throws IOException {
        Multimap<Integer, PrefEntry> preferences = readPreferences(Cars1.getPrefsPath());
        Map<Identifiable, Set<CarAttribute>> objects = readItems(Cars1.getItemsPath());
        Set<Integer> users = readUsers(Cars1.getUsersPath());
        System.out.println("users: " + users);

        Cars1 cars1 = new Cars1();
        for (Integer user : users) {
            System.out.println("user " + user);
            cars1.crossValidation(objects, preferences.get(user));
        }
    }
}
