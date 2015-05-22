package com.xokker.datasets.cars;

import com.google.common.collect.Multimap;
import com.google.common.primitives.Doubles;
import com.xokker.Identifiable;
import com.xokker.PrefEntry;
import com.xokker.PreferenceContext;
import com.xokker.Stats;
import com.xokker.predictor.PreferencePredictor;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static com.xokker.datasets.Datasets.Cars1;
import static com.xokker.datasets.cars.CarPreferencesFileReader.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author Ernest Sadykov
 * @since 21.05.2015
 */
public abstract class AbstractCars {

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
//        for (Integer user : newArrayList(11, 13, 14, 15, 17)) {
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
    protected abstract Stats crossValidation(Map<Identifiable, Set<CarAttribute>> objects,
                                  Collection<PrefEntry> preferences,
                                  Function<PreferenceContext<CarAttribute>, PreferencePredictor<CarAttribute>> predictorCreator);


    protected Map<Identifiable, Set<CarAttribute>> mapWithoutKeys(Map<Identifiable, Set<CarAttribute>> objects, Collection<Identifiable> remove) {
        Map<Identifiable, Set<CarAttribute>> objectsWithoutElement = new HashMap<>(objects);
        for (Identifiable identifiable : remove) {
            objectsWithoutElement.remove(identifiable);
        }
        return objectsWithoutElement;
    }

    protected Map<Identifiable, Set<CarAttribute>> mapWithoutKey(Map<Identifiable, Set<CarAttribute>> objects, Identifiable removedElement) {
        return mapWithoutKeys(objects, Collections.singleton(removedElement));
    }

    protected <T> Set<T> mergeSets(Collection<Set<T>> values) {
        return values.stream()
                .flatMap(Collection::stream)
                .collect(toSet());
    }

    protected void perform(Function<PreferenceContext<CarAttribute>, PreferencePredictor<CarAttribute>> predictorCreator) {
        Collection<Stats> stats;
        try {
            stats = crossValidation(predictorCreator).values();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<Double> values = stats.stream().map(Stats::getAveragePenalty).collect(toList());

        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(Doubles.toArray(values));
        logger.info("{}", descriptiveStatistics.toString());
    }

    private static String format(double d) {
        return String.format("%.2f", d);
    }
}

