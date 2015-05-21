package com.xokker.datasets.cars;

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

import static com.xokker.graph.PreferenceGraph.initBucketOrder;

/**
 * @author Ernest Sadykov
 * @since 24.04.2015
 */
public class CarsWithBucketOrders extends AbstractCars {

    private static final Logger logger = LoggerFactory.getLogger(CarsWithBucketOrders.class);

    private Random random = new Random();

    /**
     * Cross-validation for single user
     */
    protected Stats crossValidation(Map<Identifiable, Set<CarAttribute>> objects,
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
            predictor.init();

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
                    for (Identifiable current : bucket) {
                        Set<Support> ret1 = predictor.predictPreference(objects.get(current), objects.get(removedElement));
                        int support1 = ret1.size();
                        Set<Support> ret2 = predictor.predictPreference(objects.get(removedElement), objects.get(current));
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

    public static void main(String[] args) throws IOException {
        CarsWithBucketOrders carsWithBucketOrders = new CarsWithBucketOrders();
        carsWithBucketOrders.perform(CeterisParibusPredictor::new);
    }
}
