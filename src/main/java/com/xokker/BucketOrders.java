package com.xokker;

import com.google.common.collect.Sets;
import com.xokker.util.CollectionUtils;
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;
import java.util.stream.IntStream;

import static com.xokker.util.MatrixPrinter.println;
import static java.util.stream.Collectors.toSet;

/**
 * An implementation of the ranking algorithm proposed in
 * "A randomized approximation algorithm for computing bucket orders" by Ukkonen et al.
 * http://research.ics.aalto.fi/publications/kaip/10.1016_j.ipl.2008.12.003.pdf
 *
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public class BucketOrders {

    private static final double Beta = 1.0 / 16;
    private static final double Half = 1.0 / 2;

    /**
     * Preferences of an individual user
     */
    private final Collection<PrefEntry> preferences;

    /**
     * Number of different items
     */
    private final int numberOfItems;

    private final Random random;

    /**
     * @param preferences preferences of the individual user
     * @param numberOfItems number of different items
     */
    public BucketOrders(Collection<PrefEntry> preferences, int numberOfItems) {
        this.preferences = preferences;
        this.numberOfItems = numberOfItems;
        this.random = new Random();
    }

    public BucketOrders(Collection<PrefEntry> preferences) {
        this(preferences, computeNumberOfItems(preferences));
    }

    RealMatrix pairOrderMatrix() {
        RealMatrix counter = zeros(numberOfItems);
        preferences.stream()
                .forEach(e -> counter.addToEntry(e.id1.getId(), e.id2.getId(), 1));
        println(counter);

        RealMatrix result = zeros(numberOfItems);
        result.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(int r, int c, double value) {
                if (c == r) {
                    return Half;
                }
                double counterEntry = counter.getEntry(r, c);
                return counterEntry == 0
                        ? 0
                        : counterEntry / (counterEntry + counter.getEntry(c, r));
            }
        });

        return result;
    }

    public List<Set<Identifiable>> bucketPivot() {
        RealMatrix pairOrderMatrix = pairOrderMatrix();
        System.out.println("pair order matrix: ");
        println(pairOrderMatrix);
        return bucketPivot0(createRange(numberOfItems), pairOrderMatrix);
    }

    private Set<Identifiable> createRange(int numberOfItems) {
        return IntStream
                .range(0, numberOfItems)
                .boxed()
                .map(IntIdentifiable::ii)
                .collect(toSet());
    }

    private List<Set<Identifiable>> bucketPivot0(Set<Identifiable> items, RealMatrix pairOrderMatrix) {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        Identifiable pivot = CollectionUtils.randomElement(items);
        Set<Identifiable> left = Sets.newHashSet();
        Set<Identifiable> center = Sets.newHashSet(pivot);
        Set<Identifiable> right = Sets.newHashSet();
        for (Identifiable item : items) {
            double entry = pairOrderMatrix.getEntry(pivot.getId(), item.getId());
            if (entry < Half - Beta) {
                left.add(item);
            } else if (entry > Half + Beta) {
                right.add(item);
            } else {
                center.add(item);
            }
        }

        List<Set<Identifiable>> result = bucketPivot0(left, pairOrderMatrix);
        result.add(center);
        result.addAll(bucketPivot0(right, pairOrderMatrix));

        return result;
    }

    private RealMatrix zeros(int size) {
        return MatrixUtils.createRealMatrix(size, size);
    }

    private static int computeNumberOfItems(Collection<PrefEntry> preferences) {
        return (int) preferences.stream()
                .flatMapToInt(e -> IntStream.of(e.id1.getId(), e.id2.getId()))
                .distinct()
                .count();
    }
}
