package com.xokker.datasets.cars;

import com.xokker.Identifiable;
import com.xokker.PrefEntry;
import com.xokker.PreferenceContext;
import com.xokker.Stats;
import com.xokker.graph.PrefState;
import com.xokker.graph.PreferenceGraph;
import com.xokker.graph.impl.ArrayPreferenceGraph;
import com.xokker.predictor.PreferencePredictor;
import com.xokker.predictor.impl.CeterisParibusPredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.xokker.IntIdentifiable.ii;
import static java.util.stream.Collectors.toList;

/**
 * @author Ernest Sadykov
 * @since 24.04.2015
 */
public class Cars3 extends AbstractCars {

    private static final Logger logger = LoggerFactory.getLogger(Cars3.class);

    /**
     * Cross-validation for single user
     */
    @Override
    protected Stats crossValidation(Map<Identifiable, Set<CarAttribute>> objects,
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
                if (current.equals(removedElement)) {
                    continue;
                }
                int comparison = predictor.predict(objects.get(current), objects.get(removedElement));

                if (preferenceGraph.leq(removedElement, current) == PrefState.Leq && comparison < 0) {
                    penalty += 1;
                }
                if (preferenceGraph.leq(removedElement, current) == PrefState.NotLeq && comparison > 0) {
                    penalty += 1;
                }
//                if (preferenceGraph.leq(removedElement, current) != PrefState.Unknown && comparison == 0) {
//                    penalty += 1;
//                }
                logger.info("comp: {} for elements {} and {}", comparison, removedElement, current);
            }

            result.addPenalty(penalty);
            logger.info("removedElementBucketIndex: {} penalty: {}", removedElementIndex, penalty);
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        Cars3 cars3 = new Cars3();
        cars3.perform(CeterisParibusPredictor::new);
    }
}
