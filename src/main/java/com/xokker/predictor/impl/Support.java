package com.xokker.predictor.impl;

import com.xokker.Identifiable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Ernest Sadykov
 * @since 04.05.2015
 */
public class Support {

    public static final Support OK = new Support(null, null);
    public static final Support EMPTY = new Support(null, null);

    public final Identifiable g;
    public final Identifiable h;

    public Support(Identifiable g, Identifiable h) {
        this.g = g;
        this.h = h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Support support = (Support) o;

        return g.equals(support.g) && h.equals(support.h);
    }

    @Override
    public int hashCode() {
        int result = g.hashCode();
        result = 31 * result + h.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("g", g)
                .append("h", h)
                .toString();
    }
}
