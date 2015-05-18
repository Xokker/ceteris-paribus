package com.xokker.predictor.impl;

import com.xokker.Identifiable;
import com.xokker.PreferenceContext;
import com.xokker.datasets.Attribute;
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
public class CeterisParibusPredictor<A extends Attribute> implements PreferencePredictor<A> {

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
            Set<A> d = context.getObjectIntent(g);

//            if (d.equals(fixed.getD())) {
            if (intersection(d, fixed.getD()).size() == fixed.getD().size()) {
                for (Identifiable h : difference(allObjects, singleton(g))) {
                    if (context.leq(g, h) == PrefState.Leq) {
                        Set<A> hIntent = context.getObjectIntent(h);
                        Set<A> gIntent = context.getObjectIntent(g);
                        Set<A> e = hIntent;
                        Set<A> f = intersection(
                                difference(allAttributes, symmetricDifference(a, b)),
                                difference(allAttributes, symmetricDifference(gIntent, hIntent))
                        );
//                        if (f.equals(fixed.getF()) && e.equals(fixed.getE())) {
                        if (f.equals(fixed.getF()) && intersection(e, fixed.getE()).size() == fixed.getE().size()) {
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

        int maxLeftRight = 0;
        int maxRightLeft = 0;

        Set<Support> fixedlr_max = null;
        Set<Support> fixedrl_max = null;

        Iterator<CeterisParibusPreference<A>> iterator = allRightLeft.iterator();
        for (CeterisParibusPreference<A> fixed : allLeftRight) {
            Set<Support> fixedlr = fixed(first, second, fixed);
            int leftRight = fixedlr.size();
            if (leftRight > maxLeftRight) {
                maxLeftRight = leftRight;
                fixedlr_max = fixedlr;
                while (maxLeftRight >= maxRightLeft && iterator.hasNext()) {
                    Set<Support> fixedrl = fixed(second, first, iterator.next());
                    int rightLeft = fixedrl.size();
                    if (rightLeft > maxRightLeft) {
                        maxRightLeft = rightLeft;
                        fixedrl_max = fixedrl;
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
