package com.xokker.predictor;

import com.xokker.datasets.Attribute;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Ernest Sadykov
 * @since 26.05.2015
 */
public enum AttributePredicate {
    Equality(Object::equals),
    LessThan((left, right) -> {
        if (Objects.equals(left.getCategory(), right.getCategory())) {
            return left.comparator().compare(left, right) < 0;
        }
        throw new IllegalArgumentException("'left' and 'right' must be the same category");
    }),
    GreaterThan((left, right) -> {
        if (Objects.equals(left.getCategory(), right.getCategory())) {
            return left.comparator().compare(left, right) > 0;
        }
        throw new IllegalArgumentException("'left' and 'right' must be the same category");
    });

    private final BiFunction<Attribute, Attribute, Boolean> checker;

    AttributePredicate(BiFunction<Attribute, Attribute, Boolean> checker) {
        this.checker = checker;
    }

    public <T extends Attribute<T>> boolean check(T left, T right) {
        return checker.apply(left, right);
    }

    public boolean canWorkWithNominal() {
        return this == Equality;
    }
}
