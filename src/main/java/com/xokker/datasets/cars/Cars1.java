package com.xokker.datasets.cars;

import com.google.common.collect.Multimap;
import com.xokker.*;
import com.xokker.graph.PreferenceGraph;
import com.xokker.graph.impl.ArrayPreferenceGraph;
import com.xokker.predictor.PreferencePredictor;
import com.xokker.predictor.impl.CeterisParibusPredictor;
import com.xokker.predictor.impl.Support;
import com.xokker.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

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
     * @return key - user
     *         value - stats for this user
     */
    public Map<Integer, Stats> crossValidation(
            Function<PreferenceContext<CarAttribute>, PreferencePredictor<CarAttribute>> predictorCreator)
            throws IOException {

        Multimap<Integer, PrefEntry> preferences = readPreferences(Cars1.getPrefsPath());
        Map<Identifiable, Set<CarAttribute>> objects = readItems(Cars1.getItemsPath());
        Set<Integer> users = readUsers(Cars1.getUsersPath());
        logger.info("users: {}", users);
        objects.entrySet().stream().forEach(e -> logger.info("{} -> {}", e.getKey(), e.getValue()));

        Map<Integer, Stats> result = new HashMap<>(users.size());
        for (Integer user : users) {
            logger.info("user {}:", user);
            Stats stats = crossValidation(objects, preferences.get(user), predictorCreator);
            logger.info("avg penalty for user #{} is {}", user, stats.getAveragePenalty());
            result.put(user, stats);
        }

        return result;
    }

    /**
     * Cross-validation for single user
     */
    private Stats crossValidation(Map<Identifiable, Set<CarAttribute>> objects,
                                  Collection<PrefEntry> preferences,
                                  Function<PreferenceContext<CarAttribute>, PreferencePredictor<CarAttribute>> predictorCreator) {
        BucketOrders bucketOrders = new BucketOrders(preferences);
        List<Set<Identifiable>> originalBuckets = bucketOrders.bucketPivot();
        logger.info("bucket order: {}", originalBuckets);
//        mergeRandomBuckets(originalBuckets, random.nextInt(2) + 2);
//        mergeRandomBuckets(originalBuckets, random.nextInt(2) + 2);
//        mergeRandomBuckets(originalBuckets, random.nextInt(2) + 2);

        Stats result = new Stats();
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
            PreferencePredictor<CarAttribute> predictor = predictorCreator.apply(context);

            double penalty = 0;

            // if bucket index of the removed element is zero, it should be <= to everything else
            boolean after = removedElementBucketIndex == 0;

            for (Set<Identifiable> bucket : buckets) {
                if (bucket.equals(originalRandomBucket)) {
                    // elements in the same bucket must be incomparable
                    for (Identifiable id : randomBucket) {
                        Set<Support> ret = predictor.predictPreference(objects.get(id), objects.get(removedElement));
                        if (!ret.isEmpty()) {
                            penalty += 0.5;
                        }
                        ret = predictor.predictPreference(objects.get(removedElement), objects.get(id));
                        if (!ret.isEmpty()) {
                            penalty += 0.5;
                        }
                    }
                    after = true;
                } else {
                    for (Identifiable id : bucket) {
                        Set<Support> ret1 = predictor.predictPreference(objects.get(id), objects.get(removedElement));
                        int support1 = ret1.size();
                        Set<Support> ret2 = predictor.predictPreference(objects.get(removedElement), objects.get(id));
                        int support2 = ret2.size();
                        if (after && support1 > support2) {
                            penalty += 1;
                        }
                        if (!after && support1 < support2) {
                            penalty += 1;
                        }
                        logger.debug(support1 + " vs " + support2);
                    }
                }
            }

            result.addPenalty(penalty);
            logger.info("removedElementBucketIndex: {} penalty: {}", removedElementBucketIndex, penalty);
        }

        return result;
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
        Cars1 cars1 = new Cars1();
        Collection<Stats> stats = cars1.crossValidation(CeterisParibusPredictor::new).values();
        DoubleSummaryStatistics summary = stats.stream().mapToDouble(Stats::getAveragePenalty).summaryStatistics();
        logger.info("max avg penalty: {}, min avg penalty: {}, avg avg penalty: {}",
                format(summary.getMax()), format(summary.getMin()), format(summary.getAverage()));
    }

    private static String format(double d) {
        return String.format("%.2f", d);
    }
}
