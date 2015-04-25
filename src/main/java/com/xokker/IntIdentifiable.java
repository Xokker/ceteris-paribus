package com.xokker;

import com.google.common.base.Preconditions;

/**
 * @author Ernest Sadykov
 * @since 24.04.2015
 */
public class IntIdentifiable implements Identifiable {

    private final Integer value;

    public IntIdentifiable(Integer value) {
        Preconditions.checkNotNull(value);
        this.value = value;
    }

    @Override
    public int getId() {
        return value;
    }

    public static Identifiable ii(int i) {
        return new IntIdentifiable(i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IntIdentifiable that = (IntIdentifiable) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
