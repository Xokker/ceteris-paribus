package com.xokker;

import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Collection;
import java.util.stream.IntStream;

import static com.xokker.MatrixPrinter.println;

/**
 * An implementation of the ranking algorithm proposed in
 * "A randomized approximation algorithm for computing bucket orders" by Ukkonen et al.
 * http://research.ics.aalto.fi/publications/kaip/10.1016_j.ipl.2008.12.003.pdf
 *
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public class BucketOrders {

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
                double counterEntry = counter.getEntry(r, c);
                return counterEntry == 0
                        ? 0
                        : counterEntry / (counterEntry + counter.getEntry(c, r));
            }
        });

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
