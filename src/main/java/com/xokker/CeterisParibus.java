package com.xokker;

import java.util.Set;

import static com.google.common.collect.Sets.*;
import static java.util.Collections.singleton;

/**
 * An implementation of the Ceteris Paribus Preference Elicitation algorithm proposed in
 * "Ceteris Paribus: Preferences Prediction via Abduction" by Sergei Obiedkov
 * http://publications.hse.ru/en/chapters/98938030
 *
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public class CeterisParibus<A> {

    private final PreferenceContext<A> context;

    public CeterisParibus(PreferenceContext<A> context) {
        this.context = context;
    }

    /**
     * Implementation of the Algorithm 1
     */
    public boolean predictPreference(Set<A> a, Set<A> b) {
        Set<Identifiable> allObjects = context.getAllObjects();
        Set<A> allAttributes = context.getAllAttributes();
        for (Identifiable g : allObjects) {
            Set<A> d = intersection(a, context.getObjectIntent(g));
            for (Identifiable h : difference(allObjects, singleton(g))) {
                if (context.leq(g, h)) {
                    Set<A> hIntent = context.getObjectIntent(h);
                    Set<A> gIntent = context.getObjectIntent(g);
                    Set<A> e = intersection(b, hIntent);
                    Set<A> f = intersection(
                            difference(allAttributes, symmetricDifference(a, b)),
                            difference(allAttributes, symmetricDifference(gIntent, hIntent))
                    );
                    if (checkPreference(d, f, e)) {
                        return true;
                    }
                }
            }
        }

        return false;
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
                if (!context.leq(g, h) && intersection(gIntent, f).equals(intersection(hIntent, f))) {
                    return false;
                }
            }
        }

        return true;
    }

}
