package com.xokker.predictor;

import com.xokker.datasets.Attribute;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ernest Sadykov
 * @since 27.05.2015
 */
public class PredicateList<A extends Attribute> {

    private Map<AttributePredicate, Map<String, Pair<A, A>>> data;

    public PredicateList() {
        data = new HashMap<>();
    }

    public void put(AttributePredicate predicate, String category, Pair<A, A> elements) {
        Map<String, Pair<A, A>> map = data.get(predicate);
        if (map == null) {
            map = new HashMap<>();
            map.put(category, elements);
            data.put(predicate, map);
        } else {
            map.put(category, elements);
        }
    }

    public PredicateList<A> intersect(PredicateList<A> gh) {
        PredicateList<A> result = new PredicateList<>();
        for (AttributePredicate pred : data.keySet()) {
            Map<String, Pair<A, A>> first = data.get(pred);
            Map<String, Pair<A, A>> second = gh.data.get(pred);
            for (Map.Entry<String, Pair<A, A>> firstEntry : first.entrySet()) {
                String key = firstEntry.getKey();
                Pair<A, A> value = firstEntry.getValue();
                if (second != null && second.containsKey(key) && second.get(key).equals(value)) {
                    result.put(pred, key, value);
                }
            }
        }

        return result;
    }

    public Map<AttributePredicate, Map<String, Pair<A, A>>> getData() {
        return data;
    }
}
