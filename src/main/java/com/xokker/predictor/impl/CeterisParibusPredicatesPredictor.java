package com.xokker.predictor.impl;

import com.xokker.Identifiable;
import com.xokker.PreferenceContext;
import com.xokker.datasets.Attribute;
import com.xokker.graph.PrefState;
import com.xokker.predictor.AttributePredicate;
import com.xokker.predictor.PredicateList;
import com.xokker.predictor.PreferencePredictor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.intersection;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toMap;

/**
* An implementation of the Ceteris Paribus Preference Elicitation algorithm proposed in
* "Ceteris Paribus: Preferences Prediction via Abduction" by Sergei Obiedkov
* http://publications.hse.ru/en/chapters/98938030
*
* @param <A> type of objects' attributes
*
* @author Ernest Sadykov
* @since 21.04.2015
*/
public class CeterisParibusPredicatesPredictor<A extends Attribute> implements PreferencePredictor<A> {

    private static final Logger logger = LoggerFactory.getLogger(CeterisParibusPredicatesPredictor.class);

    private final PreferenceContext<A> context;
    private final boolean countSupport;

    public CeterisParibusPredicatesPredictor(PreferenceContext<A> context) {
        this(context, false);
    }

    public CeterisParibusPredicatesPredictor(PreferenceContext<A> context, boolean countSupport) {
        this.context = context;
        this.countSupport = countSupport;
    }

    /**
     * Implementation of the Algorithm 1
     */
    @Override
    public Set<Support> predictPreference(Set<A> a, Set<A> b) {
        Set<Identifiable> allObjects = context.getAllObjects();
        Set<Support> result = new HashSet<>();
        for (Identifiable g : allObjects) {
            Set<A> d = intersection(a, context.getObjectIntent(g));
            for (Identifiable h : difference(allObjects, singleton(g))) {
                if (context.leq(g, h) == PrefState.Leq) {
                    Set<A> hIntent = context.getObjectIntent(h);
                    Set<A> gIntent = context.getObjectIntent(g);
                    Set<A> e = intersection(b, hIntent);
                    PredicateList<A> ab = predicateList(a, b);
                    PredicateList<A> gh = predicateList(gIntent, hIntent);
                    PredicateList<A> f = ab.intersect(gh);
                    if (checkPreference(d, f, e)) {
                        logger.trace("{} <{}= {}    for {} and {}", d, f, e, a, b);
                        if (countSupport) {
                            result.add(new Support(g, h));
                        } else {
                            return singleton(new Support(g, h));
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Implementation of the Algorithm 2
     */
    boolean checkPreference(Set<A> d, PredicateList<A> f, Set<A> e) {
        Set<Identifiable> x = context.getAttributeExtent(d);
        Set<Identifiable> y = context.getAttributeExtent(e);
        for (Identifiable g : x) {
            for (Identifiable h : y) {
                Set<A> gIntent = context.getObjectIntent(g);
                Set<A> hIntent = context.getObjectIntent(h);
                if (context.leq(g, h) != PrefState.Leq && check(f, gIntent, hIntent)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Positive number if the first is better than the second.
     */
    @Override
    public int predict(Set<A> first, Set<A> second) {
        Map<Integer, CPPredicatesPreference<A>> lr = predictPreferenceCounted(first, second);
        Map<Integer, CPPredicatesPreference<A>> rl = predictPreferenceCounted(second, first);

        return rl.keySet().stream().max(Comparator.<Integer>naturalOrder()).orElseGet(() -> 0) -
                lr.keySet().stream().max(Comparator.<Integer>naturalOrder()).orElseGet(() -> 0);
    }

    public Map<Integer, CPPredicatesPreference<A>> predictPreferenceCounted(Set<A> a, Set<A> b) {
        Set<Identifiable> allObjects = context.getAllObjects();

        Map<Integer, CPPredicatesPreference<A>> result = new HashMap<>();
        for (Identifiable g : allObjects) {
            Set<A> d = intersection(a, context.getObjectIntent(g));
            for (Identifiable h : difference(allObjects, singleton(g))) {
                if (context.leq(g, h) == PrefState.Leq) {
                    Set<A> hIntent = context.getObjectIntent(h);
                    Set<A> gIntent = context.getObjectIntent(g);
                    Set<A> e = intersection(b, hIntent);
                    PredicateList<A> ab = predicateList(a, b);
                    PredicateList<A> gh = predicateList(gIntent, hIntent);
                    PredicateList<A> f = ab.intersect(gh);
                    int c = checkPreferenceCounted(d, f, e);
                    if (c > 0) {
                        logger.trace("{} <{}= {}    for {} and {}", d, f, e, a, b);
                        result.put(c, new CPPredicatesPreference<A>(d, f, e));
                    }
                }
            }
        }

        return result;
    }

    private PredicateList<A> predicateList(Set<A> first, Set<A> second) {
        Map<String, A> fGroupped = first .stream().collect(toMap(Attribute::getCategory, a -> a));
        Map<String, A> sGroupped = second.stream().collect(toMap(Attribute::getCategory, a -> a));

        PredicateList<A> result = new PredicateList<>();
        for (AttributePredicate predicate : AttributePredicate.values()) {
            for (Map.Entry<String, A> fEntry : fGroupped.entrySet()) {
                String category = fEntry.getKey();
                A sAtt = sGroupped.get(category);
                if (!sAtt.isNumeric() && !predicate.canWorkWithNominal()) {
                    continue;
                }
                if (predicate.check(fEntry.getValue(), sAtt)) {
                    result.put(predicate, category, Pair.of(fEntry.getValue(), sAtt));
                }
            }
        }

        return result;
    }


    int checkPreferenceCounted(Set<A> d, PredicateList<A> f, Set<A> e) {
        int c = 0;
        Set<Identifiable> x = context.getAttributeExtent(d);
        Set<Identifiable> y = context.getAttributeExtent(e);
        for (Identifiable g : x) {
            for (Identifiable h : y) {
                Set<A> gIntent = context.getObjectIntent(g);
                Set<A> hIntent = context.getObjectIntent(h);
                if (check(f, gIntent, hIntent)) {
                    if (context.leq(g, h) != PrefState.Leq) {
                        return 0;
                    } else {
                        c++;
                    }
                }
            }
        }

        return c;
    }

    private boolean check(PredicateList<A> f, Set<A> gIntent, Set<A> hIntent) {
        // category -> (l, r)
        Map<String, Pair<A, A>> groupped = gIntent.stream().collect(toMap(
                        Attribute::getCategory,
                        a -> Pair.of(a, find(hIntent, a.getCategory())))
        );

        Map<AttributePredicate, Map<String, Pair<A, A>>> data = f.getData();
        for (AttributePredicate p : data.keySet()) {
            Map<String, Pair<A, A>> pairs = data.get(p);
            for (Map.Entry<String, Pair<A, A>> entry : pairs.entrySet()) {
                Pair<A, A> pair = entry.getValue();
                String category = entry.getKey();
                if (pair.getLeft().isNumeric()) {
                    if (groupped.containsKey(category)) {
                        Pair<A, A> ghPair = groupped.get(category);
                        if (!p.check(ghPair.getLeft(), ghPair.getRight())) {
                            return false;
                        }
                    }
                } else {
                    if (groupped.values().contains(pair)) {
                        if (!p.check(pair.getLeft(), pair.getRight())) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private A find(Set<A> hIntent, String category) {
        for (A a : hIntent) {
            if (Objects.equals(a.getCategory(), category)) {
                return a;
            }
        }
        throw new IllegalStateException("no category in left object's intent");
    }
}

