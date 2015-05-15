package com.xokker.predictor.impl;

import com.xokker.Identifiable;
import com.xokker.PreferenceContext;
import com.xokker.graph.PrefState;
import com.xokker.predictor.PreferencePredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
public class CeterisParibusPredictor<A> implements PreferencePredictor<A> {

    private static final Logger logger = LoggerFactory.getLogger(CeterisParibusPredictor.class);

    private final PreferenceContext<A> context;

    public CeterisParibusPredictor(PreferenceContext<A> context) {
        this.context = context;
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
                        result.add(new Support(g, h));
                    }
                }
            }
        }

        return result;
    }

    /**
     * TODO: optimize
     */
    public Set<Support> fixed(Set<A> a, Set<A> b, CeterisParibusPreference<A> fixed) {
        Set<Identifiable> allObjects = context.getAllObjects();
        Set<A> allAttributes = context.getAllAttributes();
        Set<Support> result = new HashSet<>();
        for (Identifiable g : allObjects) {
            Set<A> d = intersection(a, context.getObjectIntent(g));

//            if (d.equals(fixed.getD())) {
            if (intersection(d, fixed.getD()).size() == d.size()) {
                for (Identifiable h : difference(allObjects, singleton(g))) {
                    if (context.leq(g, h) == PrefState.Leq) {
                        Set<A> hIntent = context.getObjectIntent(h);
                        Set<A> gIntent = context.getObjectIntent(g);
                        Set<A> e = intersection(b, hIntent);
                        Set<A> f = intersection(
                                difference(allAttributes, symmetricDifference(a, b)),
                                difference(allAttributes, symmetricDifference(gIntent, hIntent))
                        );
//                        if (f.equals(fixed.getF()) && e.equals(fixed.getE())) {
                        if (intersection(f, fixed.getF()).size() == f.size() && intersection(e, fixed.getE()).size() == e.size()) {
                            if (checkPreference(d, f, e)) {
                                logger.trace("{} <{}= {}    for {} and {}", d, f, e, a, b);
                                result.add(new Support(g, h));
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private Set<CeterisParibusPreference<A>> findAll(Set<A> a, Set<A> b) {
        Set<Identifiable> allObjects = context.getAllObjects();
        Set<A> allAttributes = context.getAllAttributes();
        Set<CeterisParibusPreference<A>> result = new HashSet<>();
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
                        result.add(new CeterisParibusPreference<>(d, f, e));
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
        Set<CeterisParibusPreference<A>> allLeftRight = findAll(first, second);
        Set<CeterisParibusPreference<A>> allRightLeft = findAll(second, first);

        int maxLeftRight = -1;
        int maxRightLeft = -1;

        Iterator<CeterisParibusPreference<A>> iterator = allRightLeft.iterator();
        for (CeterisParibusPreference<A> fixed : allLeftRight) {
            int leftRight = fixed(first, second, fixed).size();
            if (leftRight > maxLeftRight) {
                maxLeftRight = leftRight;
                while (maxLeftRight >= maxRightLeft && iterator.hasNext()) {
                    int rightLeft = fixed(second, first, iterator.next()).size();
                    if (rightLeft > maxRightLeft) {
                        maxRightLeft = rightLeft;
                    }
                }
            }
        }

        return maxRightLeft - maxLeftRight;
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

}
