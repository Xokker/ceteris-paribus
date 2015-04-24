package com.xokker.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

import java.util.Collection;
import java.util.Random;

/**
 * @author Ernest Sadykov
 * @since 24.04.2015
 */
public class CollectionUtils {

    private static final Random random = new Random();

    public static <T> T randomElement(Collection<T> items) {
        preconditions(items);

        int position = random.nextInt(items.size());
        return Iterators.get(items.iterator(), position);
    }

    public static <T> T removeRandom(Collection<T> items) {
        preconditions(items);

        T t = randomElement(items);
        items.remove(t);
        return t;
    }

    private static <T> void preconditions(Collection<T> items) {
        Preconditions.checkNotNull(items);
        Preconditions.checkArgument(!items.isEmpty(), "Set is empty");
    }
}
