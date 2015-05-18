package com.xokker.datasets.cars;

import com.google.common.collect.Multimap;
import com.xokker.Identifiable;
import com.xokker.PrefEntry;
import com.xokker.PreferenceContext;
import com.xokker.Stats;
import com.xokker.graph.PrefState;
import com.xokker.graph.PreferenceGraph;
import com.xokker.graph.impl.ArrayPreferenceGraph;
import com.xokker.predictor.PreferencePredictor;
import com.xokker.predictor.impl.J48Predictor;
import com.xokker.predictor.impl.Support;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static com.xokker.IntIdentifiable.ii;
import static com.xokker.datasets.Datasets.Cars1;
import static com.xokker.datasets.cars.CarPreferencesFileReader.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author Ernest Sadykov
 * @since 24.04.2015
 */
public class Cars2 {

    private static final Logger logger = LoggerFactory.getLogger(Cars2.class);

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
//        for (Integer user : singleton(54)) {
        for (Integer user : users) {
            logger.info("user {}:", user);
            Collection<PrefEntry> userPreferences = preferences.get(user);
            logger.info("{} preferences", userPreferences.size());
            Stats stats = crossValidation(objects, userPreferences, predictorCreator);
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

        Stats result = new Stats();
        for (int removedElementIndex = 0; removedElementIndex < objects.keySet().size(); removedElementIndex++) {
            Identifiable removedElement = ii(removedElementIndex);

            PreferenceGraph filteredPreferenceGraph = new ArrayPreferenceGraph(objects.size());
            List<PrefEntry> filteredPreferences = preferences.stream()
                    .filter(e -> !e.id1.equals(removedElement))
                    .filter(e -> !e.id2.equals(removedElement))
                    .collect(toList());
            PreferenceGraph.init(filteredPreferenceGraph, filteredPreferences);

            PreferenceGraph preferenceGraph = new ArrayPreferenceGraph(objects.size());
            PreferenceGraph.init(preferenceGraph, preferences);

            Set<CarAttribute> possibleAttributes = mergeSets(objects.values());

            PreferenceContext<CarAttribute> context = new PreferenceContext<>(possibleAttributes, filteredPreferenceGraph);

            context.addObjects(mapWithoutKey(objects, removedElement));
            PreferencePredictor<CarAttribute> predictor = predictorCreator.apply(context);
            predictor.init();

            double penalty = 0;

            for (Identifiable current : objects.keySet()) {
                Set<Support> ret1 = predictor.predictPreference(objects.get(current), objects.get(removedElement));
                int support1 = ret1.size();
                Set<Support> ret2 = predictor.predictPreference(objects.get(removedElement), objects.get(current));
                int support2 = ret2.size();
                if (preferenceGraph.leq(removedElement, current) == PrefState.Leq && support1 > support2) {
                    penalty += 1;
                }
                if (preferenceGraph.leq(removedElement, current) == PrefState.NotLeq && support1 < support2) {
                    penalty += 1;
                }
                logger.info("{} vs {} for elements {} and {}", support1, support2, removedElement, current);
            }

            result.addPenalty(penalty);
            logger.info("removedElementBucketIndex: {} penalty: {}", removedElementIndex, penalty);
        }

        return result;
    }

    private Map<Identifiable, Set<CarAttribute>> mapWithoutKey(Map<Identifiable, Set<CarAttribute>> objects, Identifiable removedElement) {
        Map<Identifiable, Set<CarAttribute>> objectsWithoutElement = new HashMap<>(objects);
        objectsWithoutElement.remove(removedElement);
        return objectsWithoutElement;
    }

    private <T> Set<T> mergeSets(Collection<Set<T>> values) {
        return values.stream()
                .flatMap(Collection::stream)
                .collect(toSet());
    }

    public static void main(String[] args) throws IOException {
        Cars2 cars2 = new Cars2();
        Collection<Stats> stats = cars2.crossValidation((context) -> new J48Predictor<>(context, true)).values();
        DoubleSummaryStatistics summary = stats.stream().mapToDouble(Stats::getAveragePenalty).summaryStatistics();
        logger.info("J48Predictor<>(context, true)");
        logger.info("max avg penalty: {}, min avg penalty: {}, avg avg penalty: {}",
                format(summary.getMax()), format(summary.getMin()), format(summary.getAverage()));
    }

    private static String format(double d) {
        return String.format("%.2f", d);
    }
}
