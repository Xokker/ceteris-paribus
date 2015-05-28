package com.xokker.datasets;

import com.google.common.collect.Multimap;
import com.google.common.primitives.Doubles;
import com.xokker.Identifiable;
import com.xokker.PrefEntry;
import com.xokker.PreferenceContext;
import com.xokker.Stats;
import com.xokker.predictor.PreferencePredictor;
import com.xokker.util.CollectionUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author Ernest Sadykov
 * @since 21.05.2015
 */
public abstract class AbstractExperiment<A extends Attribute<A>> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractExperiment.class);

    private boolean remove2Elements = false;

    /**
     * @return key - user
     *         value - stats for this user
     */
    public Map<Integer, Stats> crossValidation(Datasets dataset,
            Function<PreferenceContext<A>, PreferencePredictor<A>> predictorCreator)
            throws IOException {

        @SuppressWarnings("unchecked")
        PreferenceReader<A> preferenceReader = dataset.getReader();
        List<Integer> users = preferenceReader.readUsers(dataset.getUsersPath());
        Multimap<Integer, PrefEntry> preferences = preferenceReader.readPreferences(dataset.getPrefsPath(), users);
        Map<Identifiable, Set<A>> objects = preferenceReader.readItems(dataset.getItemsPath());
        logger.info("users: {}", users);
        objects.entrySet().stream().forEach(e -> logger.info("{} -> {}", e.getKey(), e.getValue()));

        Map<Integer, Stats> result = new HashMap<>(users.size());
//        for (Integer user : newArrayList(17)) {
        for (Integer user : CollectionUtils.randomElements(users, 300)) {
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
    protected abstract Stats crossValidation(Map<Identifiable, Set<A>> objects,
                                  Collection<PrefEntry> preferences,
                                  Function<PreferenceContext<A>, PreferencePredictor<A>> predictorCreator);


    protected Map<Identifiable, Set<A>> mapWithoutKeys(Map<Identifiable, Set<A>> objects, Collection<Identifiable> remove) {
        Map<Identifiable, Set<A>> objectsWithoutElement = new HashMap<>(objects);
        for (Identifiable identifiable : remove) {
            objectsWithoutElement.remove(identifiable);
        }
        return objectsWithoutElement;
    }

    protected Map<Identifiable, Set<A>> mapWithoutKey(Map<Identifiable, Set<A>> objects, Identifiable removedElement) {
        return mapWithoutKeys(objects, Collections.singleton(removedElement));
    }

    protected <T> Set<T> mergeSets(Collection<Set<T>> values) {
        return values.stream()
                .flatMap(Collection::stream)
                .collect(toSet());
    }

    protected void perform(Datasets datasets, Function<PreferenceContext<A>, PreferencePredictor<A>> predictorCreator) {
        Collection<Stats> stats;
        try {
            stats = crossValidation(datasets, predictorCreator).values();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<Double> values = stats.stream().map(Stats::getAveragePenalty).collect(toList());

        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(Doubles.toArray(values));
        logger.info("{}", descriptiveStatistics.toString());

        DescriptiveStatistics dsPrecision = new DescriptiveStatistics(Doubles.toArray(stats.stream().map(Stats::getPrecision).collect(toList())));
        DescriptiveStatistics dsRecall = new DescriptiveStatistics(Doubles.toArray(stats.stream().map(Stats::getRecall).collect(toList())));
        logger.info("precision: {} \n recall: {}", dsPrecision.toString(), dsRecall.toString());
    }

    public boolean isRemove2Elements() {
        return remove2Elements;
    }

    public void remove2Elements() {
        this.remove2Elements = true;
    }
}

