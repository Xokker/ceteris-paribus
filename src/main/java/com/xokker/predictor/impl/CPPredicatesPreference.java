package com.xokker.predictor.impl;

import com.xokker.datasets.Attribute;
import com.xokker.predictor.PredicateList;

import java.util.Objects;
import java.util.Set;

/**
 * Represents the triple D, F and E.
 * E is at least as good as D with F being equal.
 *
 * @author Ernest Sadykov
 * @since 14.05.2015
 */
public class CPPredicatesPreference<A extends Attribute> {

    private final Set<A> d;
    private final PredicateList<A> f;
    private final Set<A> e;

    public CPPredicatesPreference(Set<A> d, PredicateList<A> f, Set<A> e) {
        Objects.requireNonNull(d);
        Objects.requireNonNull(f);
        Objects.requireNonNull(e);

        this.e = e;
        this.f = f;
        this.d = d;
    }

    public Set<A> getD() {
        return d;
    }

    public PredicateList<A> getF() {
        return f;
    }

    public Set<A> getE() {
        return e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CPPredicatesPreference that = (CPPredicatesPreference) o;

        return d.equals(that.d) && e.equals(that.e) && f.equals(that.f);

    }

    @Override
    public int hashCode() {
        int result = d.hashCode();
        result = 31 * result + f.hashCode();
        result = 31 * result + e.hashCode();

        return result;
    }
}
