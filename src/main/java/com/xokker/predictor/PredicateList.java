package com.xokker.predictor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.xokker.datasets.Attribute;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

/**
 * @author Ernest Sadykov
 * @since 27.05.2015
 */
public class PredicateList<A extends Attribute> {

    private Multimap<AttributePredicate, Pair<A, A>> data;

    public PredicateList() {
        data = HashMultimap.create();
    }

    public boolean put(AttributePredicate predicate, Pair<A, A> elements) {
        return data.put(predicate, elements);
    }

    public PredicateList<A> intersect(PredicateList<A> gh) {
        PredicateList<A> result = new PredicateList<>();
        for (AttributePredicate key : data.keySet()) {
            Collection<Pair<A, A>> first = data.get(key);
            Collection<Pair<A, A>> second = gh.data.get(key);
            for (Pair<A, A> pair : first) {
                if (second.contains(pair)) {
                    result.put(key, pair);
                }
            }
        }

        return result;
    }

    public Multimap<AttributePredicate, Pair<A, A>> getData() {
        return data;
    }
}
