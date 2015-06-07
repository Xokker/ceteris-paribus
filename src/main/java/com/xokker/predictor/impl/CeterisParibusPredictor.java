package com.xokker.predictor.impl;

import com.xokker.Identifiable;
import com.xokker.PreferenceContext;
import com.xokker.datasets.Attribute;
import com.xokker.graph.PrefState;
import com.xokker.predictor.PreferencePredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.collect.Sets.*;
import static java.util.Collections.singleton;

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
public class CeterisParibusPredictor<A extends Attribute> implements PreferencePredictor<A> {

    private static final Logger logger = LoggerFactory.getLogger(CeterisParibusPredictor.class);

    private final PreferenceContext<A> context;
    private final boolean countSupport;

    public CeterisParibusPredictor(PreferenceContext<A> context) {
        this(context, false);
    }

    public CeterisParibusPredictor(PreferenceContext<A> context, boolean countSupport) {
        this.context = context;
        this.countSupport = countSupport;
    }

    /**
     * Implementation of the Algorithm 1
     */
    @Override
    public Set<Support> predictPreference(Set<A> a, Set<A> b) {
        Set<Identifiable> allObjects = context.getAllObjects();
        Set<A> allAttributes = context.getAllAttributes();
        Set<Support> result = new HashSet<>();
        for (Identifiable g : allObjects) {
            Set<A> d = intersection(a, context.getObjectIntent(g));
            for (Identifiable h : difference(allObjects, singleton(g))) {
                if (context.leq(g, h) == PrefState.Leq) {
                    Set<A> hIntent = context.getObjectIntent(h);
                    Set<A> gIntent = context.getObjectIntent(g);
                    Set<A> e = intersection(b, hIntent);
                    Set<A> f = intersection(
                            difference(allAttributes, symmetricDifference(a, b)),
                            difference(allAttributes, symmetricDifference(gIntent, hIntent))
                    );
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
     * Positive number if the first is better than the second.
     */
    @Override
    public int predict(Set<A> first, Set<A> second) {
        Map<Integer, CPPreference<A>> lr = predictPreferenceCounted(first, second);
        Map<Integer, CPPreference<A>> rl = predictPreferenceCounted(second, first);

        return rl.keySet().stream().max(Comparator.<Integer>naturalOrder()).orElseGet(() -> 0) -
                lr.keySet().stream().max(Comparator.<Integer>naturalOrder()).orElseGet(() -> 0);
    }

    /**
     * Implementation of the Algorithm 2
     */
    boolean checkPreference(Set<A> d, Set<A> f, Set<A> e) {
        Set<Identifiable> x = context.getAttributeExtent(d);
        Set<Identifiable> y = context.getAttributeExtent(e);
        for (Identifiable g : x) {
            for (Identifiable h : y) {
                Set<A> gIntent = context.getObjectIntent(g);
                Set<A> hIntent = context.getObjectIntent(h);
                if (context.leq(g, h) != PrefState.Leq && intersection(gIntent, f).equals(intersection(hIntent, f))) {
                    return false;
                }
            }
        }

        return true;
    }

    public Map<Integer, CPPreference<A>> predictPreferenceCounted(Set<A> a, Set<A> b) {
        Set<Identifiable> allObjects = context.getAllObjects();
        Set<A> allAttributes = context.getAllAttributes();

        Map<Integer, CPPreference<A>> result = new HashMap<>();
        for (Identifiable g : allObjects) {
            Set<A> d = intersection(a, context.getObjectIntent(g));
            for (Identifiable h : difference(allObjects, singleton(g))) {
                if (context.leq(g, h) == PrefState.Leq) {
                    Set<A> hIntent = context.getObjectIntent(h);
                    Set<A> gIntent = context.getObjectIntent(g);
                    Set<A> e = intersection(b, hIntent);
                    Set<A> f = intersection(
                            difference(allAttributes, symmetricDifference(a, b)),
                            difference(allAttributes, symmetricDifference(gIntent, hIntent))
                    );
                    int c = checkPreferenceCounted(d, f, e);
                    if (c > 0) {
                        logger.trace("{} <{}= {}    for {} and {}", d, f, e, a, b);
                        result.put(c, new CPPreference<A>(d, f, e));
                    }
                }
            }
        }

        return result;
    }


    int checkPreferenceCounted(Set<A> d, Set<A> f, Set<A> e) {
        int c = 0;
        Set<Identifiable> x = context.getAttributeExtent(d);
        Set<Identifiable> y = context.getAttributeExtent(e);
        for (Identifiable g : x) {
            for (Identifiable h : y) {
                Set<A> gIntent = context.getObjectIntent(g);
                Set<A> hIntent = context.getObjectIntent(h);
                if (Objects.equals(intersection(gIntent, f), (intersection(hIntent, f)))) {
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
}
