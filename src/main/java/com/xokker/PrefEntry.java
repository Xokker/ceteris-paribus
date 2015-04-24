package com.xokker;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Ernest Sadykov
 * @since 20.04.2015
 */
public class PrefEntry {

    public PrefEntry(Identifiable id1, Identifiable id2) {
        Preconditions.checkNotNull(id1);
        Preconditions.checkNotNull(id2);
        this.id1 = id1;
        this.id2 = id2;
    }

    public final Identifiable id1;
    public final Identifiable id2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrefEntry prefEntry = (PrefEntry) o;

        return id1.equals(prefEntry.id1) && id2.equals(prefEntry.id2);
    }

    @Override
    public int hashCode() {
        int result = id1.hashCode();
        result = 31 * result + id2.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id1", id1)
                .append("id2", id2)
                .toString();
    }
}
