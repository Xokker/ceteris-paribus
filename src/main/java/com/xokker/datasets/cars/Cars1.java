package com.xokker.datasets.cars;

import com.google.common.collect.Multimap;
import com.xokker.*;
import com.xokker.graph.PreferenceGraph;
import com.xokker.graph.impl.ArrayPreferenceGraph;
import com.xokker.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(Cars1.class);

    private Random random = new Random();

    /**
     * Cross-validation for single user
     */
    private void crossValidation(Map<Identifiable, Set<CarAttribute>> objects, Collection<PrefEntry> preferences) {
        BucketOrders bucketOrders = new BucketOrders(preferences);
        List<Set<Identifiable>> originalBuckets = bucketOrders.bucketPivot();
//        mergeRandomBuckets(originalBuckets, random.nextInt(2) + 2);
//        mergeRandomBuckets(originalBuckets, random.nextInt(2) + 2);
//        mergeRandomBuckets(originalBuckets, random.nextInt(2) + 2);

        for (int removedElementBucketIndex = 0; removedElementBucketIndex < originalBuckets.size(); removedElementBucketIndex++) {
            List<Set<Identifiable>> buckets = deepCopy(originalBuckets);

            Set<Identifiable> originalRandomBucket = buckets.get(removedElementBucketIndex);
            Set<Identifiable> randomBucket = new HashSet<>(originalRandomBucket);
            int randomBucketIndex = originalBuckets.indexOf(randomBucket);

            Identifiable removedElement = CollectionUtils.removeRandom(randomBucket);
            buckets.get(randomBucketIndex).remove(removedElement);

            PreferenceGraph preferenceGraph = new ArrayPreferenceGraph(objects.size());
            initBucketOrder(preferenceGraph, buckets);
            PreferenceContext<CarAttribute> context = new PreferenceContext<>(mergeSets(objects.values()), preferenceGraph);

            context.addObjects(mapWithoutKey(objects, removedElement));
            CeterisParibus<CarAttribute> ceterisParibus = new CeterisParibus<>(context);

            int penalty = 0;

            // if bucket index of the removed element is zero, it should be <= to everything else
            boolean after = removedElementBucketIndex == 0;

            boolean ret;
            for (Set<Identifiable> bucket : buckets) {
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

            logger.info("removedElementBucketIndex: {} penalty: {}", removedElementBucketIndex, penalty);
        }
    }

    private Map<Identifiable, Set<CarAttribute>> mapWithoutKey(Map<Identifiable, Set<CarAttribute>> objects, Identifiable removedElement) {
        Map<Identifiable, Set<CarAttribute>> objectsWithoutElement = new HashMap<>(objects);
        objectsWithoutElement.remove(removedElement);
        return objectsWithoutElement;
    }

    private List<Set<Identifiable>> deepCopy(List<Set<Identifiable>> originalBuckets) {
        List<Set<Identifiable>> res = new ArrayList<>();
        for (Set<Identifiable> originalBucket : originalBuckets) {
            res.add(new HashSet<>(originalBucket));
        }

        return res;
    }

    private int deepSize(List<Set<Identifiable>> buckets) {
        return buckets.stream().mapToInt(Set::size).sum();
    }

    private void mergeRandomBuckets(List<Set<Identifiable>> originalBuckets, int numberOfBucketsToMerge) {
        int startWith = originalBuckets.size() - numberOfBucketsToMerge;
        if (startWith < 0) {
            return;
        }
        int i = random.nextInt(startWith);
        Set<Identifiable> element = originalBuckets.get(i);
        for (int j = 1; j < numberOfBucketsToMerge; j++) {
            Set<Identifiable> set = originalBuckets.remove(i + j);
            element.addAll(set);
        }
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
        logger.info("users: {}", users);
        objects.entrySet().stream().forEach(e -> logger.info("{} -> {}", e.getKey(), e.getValue()));

        Cars1 cars1 = new Cars1();
        for (Integer user : users) {
            logger.info("user " + user);
            cars1.crossValidation(objects, preferences.get(user));
        }
    }
}
