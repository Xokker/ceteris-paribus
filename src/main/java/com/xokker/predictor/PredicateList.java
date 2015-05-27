package com.xokker.predictor;

import com.xokker.datasets.Attribute;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

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

    /*

    this:
    EQ -> {Body -> (Sedan, Sedan),
           Transmission -> (Manual, Manual)}
    LT -> {Engine -> (S, XL)}

    that:
    EQ -> {Fuel -> (Hybrid, Hybrid),
           Transmission -> (Automatic, Automatic),
           Body -> (Sedan, Sedan)}
    LT -> {Engine -> (XS, XL)}

    result:
    EQ -> {Body -> (Sedan, Sedan)}
    LT -> {Engine -> (S, XL)}

     */
    public PredicateList<A> intersect(PredicateList<A> that) {
        PredicateList<A> result = new PredicateList<>();
        for (AttributePredicate pred : data.keySet()) {
            Map<String, Pair<A, A>> first = this.data.get(pred);
            Map<String, Pair<A, A>> second = that.data.get(pred);
            for (Map.Entry<String, Pair<A, A>> firstEntry : first.entrySet()) {
                String key = firstEntry.getKey();
                Pair<A, A> value = firstEntry.getValue();
                if (second != null && second.containsKey(key)) {
                    // numeric attributes must be treated differently
                    if (second.get(key).equals(value) || value.getLeft().isNumeric() && second.containsKey(key)) {
                        result.put(pred, key, value);
                    }
                }
            }
        }

        return result;
    }

    public Map<AttributePredicate, Map<String, Pair<A, A>>> getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PL: ");
        for (AttributePredicate predicate : data.keySet()) {
            sb.append(predicate.name())
                    .append(data.get(predicate).keySet().stream().collect(joining(", ", "(", ")")));
        }
        return sb.toString();
    }
}
