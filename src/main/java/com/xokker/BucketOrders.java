package com.xokker;

import com.google.common.collect.Sets;
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;
import java.util.stream.IntStream;

import static com.xokker.util.MatrixPrinter.println;

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

    /**
     * @param preferences preferences of the individual user
     * @param numberOfItems number of different items
     */
    public BucketOrders(Collection<PrefEntry> preferences, int numberOfItems) {
        this.preferences = preferences;
        this.numberOfItems = numberOfItems;
    }

    public BucketOrders(Collection<PrefEntry> preferences) {
        this(preferences, computeNumberOfItems(preferences));
    }

    RealMatrix pairOrderMatrix() {
        RealMatrix counter = zeros(numberOfItems);
        preferences.stream()
                .forEach(e -> counter.addToEntry(e.id1 - 1, e.id2 - 1, 1));
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

    public List<Set<Integer>> bucketPivot() {
        RealMatrix pairOrderMatrix = pairOrderMatrix();
        return bucketPivot0(createRange(numberOfItems), pairOrderMatrix);
    }

    private Set<Integer> createRange(int numberOfItems) {
        Set<Integer> res = new HashSet<>();
        IntStream.range(1, numberOfItems + 1).forEach(res::add);

        return res;
    }

    private List<Set<Integer>> bucketPivot0(Set<Integer> items, RealMatrix pairOrderMatrix) {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        Integer pivot = items.iterator().next(); // TODO: select truly random element
        Set<Integer> left = Sets.newHashSet();
        Set<Integer> center = Sets.newHashSet(pivot);
        Set<Integer> right = Sets.newHashSet();
        for (Integer item : items) {
            double entry = pairOrderMatrix.getEntry(pivot - 1, item - 1);
            if (entry < Half - Beta) {
                left.add(item);
            } else if (entry > Half + Beta) {
                right.add(item);
            } else {
                center.add(item);
            }
        }

        List<Set<Integer>> result = bucketPivot0(left, pairOrderMatrix);
        result.add(center);
        result.addAll(bucketPivot0(right, pairOrderMatrix));

        return result;
    }

    private RealMatrix zeros(int size) {
        return MatrixUtils.createRealMatrix(size, size);
    }

    private static int computeNumberOfItems(Collection<PrefEntry> preferences) {
        return (int) preferences.stream()
                .flatMapToInt(e -> IntStream.of(e.id1, e.id2))
                .distinct()
                .count();
    }
}
