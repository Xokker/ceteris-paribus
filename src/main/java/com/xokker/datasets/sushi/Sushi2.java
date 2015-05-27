package com.xokker.datasets.sushi;

import com.xokker.Identifiable;
import com.xokker.PrefEntry;
import com.xokker.PreferenceContext;
import com.xokker.Stats;
import com.xokker.datasets.Datasets;
import com.xokker.datasets.cars.AbstractCars;
import com.xokker.graph.PrefState;
import com.xokker.graph.PreferenceGraph;
import com.xokker.graph.impl.ArrayPreferenceGraph;
import com.xokker.predictor.PreferencePredictor;
import com.xokker.predictor.impl.CeterisParibusPredicatesPredictor;
import com.xokker.predictor.impl.Support;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.IntIdentifiable.ii;
import static java.util.stream.Collectors.toList;

/**
 * @author Ernest Sadykov
 * @since 24.04.2015
 */
public class Sushi2 extends AbstractCars<SushiAttribute> {

    private static final Logger logger = LoggerFactory.getLogger(Sushi2.class);

    /**
     * Cross-validation for single user
     */
    @Override
    protected Stats crossValidation(Map<Identifiable, Set<SushiAttribute>> objects,
                                    Collection<PrefEntry> preferences,
                                    Function<PreferenceContext<SushiAttribute>, PreferencePredictor<SushiAttribute>> predictorCreator) {

        Stats result = new Stats();
        for (int removedElementIndex = 0; removedElementIndex < objects.keySet().size(); removedElementIndex++) {
            Identifiable removedElement = ii(removedElementIndex);

            double penalty = 0;

            for (Identifiable current : objects.keySet()) {
                if (current.equals(removedElement)) {
                    continue;
                }
                Set<Identifiable> removedElements = newHashSet(removedElement);
                if (isRemove2Elements()) {
                    removedElements.add(current);
                }

                PreferenceGraph filteredPreferenceGraph = new ArrayPreferenceGraph(objects.size());
                List<PrefEntry> filteredPreferences = preferences.stream()
                        .filter(e -> !removedElements.contains(e.id1))
                        .filter(e -> !removedElements.contains(e.id2))
                        .collect(toList());
                PreferenceGraph.init(filteredPreferenceGraph, filteredPreferences);

                PreferenceGraph preferenceGraph = new ArrayPreferenceGraph(objects.size());
                PreferenceGraph.init(preferenceGraph, preferences);

                Set<SushiAttribute> possibleAttributes = mergeSets(objects.values());

                PreferenceContext<SushiAttribute> context = new PreferenceContext<>(possibleAttributes, filteredPreferenceGraph);

                context.addObjects(mapWithoutKeys(objects, removedElements));
                PreferencePredictor<SushiAttribute> predictor = predictorCreator.apply(context);
                predictor.init();

                Set<Support> ret1 = predictor.predictPreference(objects.get(current), objects.get(removedElement));
                int support1 = ret1.size();
                Set<Support> ret2 = predictor.predictPreference(objects.get(removedElement), objects.get(current));
                int support2 = ret2.size();
                double currentPenalty = 0.0;
                if (preferenceGraph.leq(removedElement, current) == PrefState.Leq && support1 > support2) {
                    currentPenalty = 1;
                    result.falsePositive();
                }
                if (preferenceGraph.leq(removedElement, current) == PrefState.NotLeq && support1 < support2) {
                    currentPenalty = 1;
                    result.falsePositive();
                }
                if (preferenceGraph.leq(removedElement, current) != PrefState.Unknown && support1 == support2) {
                    currentPenalty = 0.5;
                    result.falseNegative();
                }
                if (currentPenalty == 0) {
                    result.truePositive();
                }
                penalty += currentPenalty;
                logger.info("{} vs {} for elements {} and {}. pen: {}", support1, support2, removedElement, current, currentPenalty);
            }

            result.addPenalty(1 - penalty / 9);
            logger.info("removedElementBucketIndex: {} penalty: {}", removedElementIndex, penalty);
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        Sushi2 cars2 = new Sushi2();
//        cars2.remove2Elements();
//        cars2.perform((context) -> new J48Predictor<CarAttribute>(context, true, true));
        cars2.perform(Datasets.SushiA, (context) -> new CeterisParibusPredicatesPredictor<SushiAttribute>(context));
    }
}
