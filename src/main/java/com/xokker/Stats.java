package com.xokker;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Ernest Sadykov
 * @since 25.04.2015
 */
public class Stats {

    private Integer userId;
    private List<Double> penalties;
    private int truePositiveCount;
    private int falsePositiveCount;
    private int falseNegativeCount;

    public Stats(Integer userId) {
        this.userId = userId;
        penalties = new ArrayList<>();
    }

    public Stats() {
        this(null);
    }

    public void truePositive() {
        truePositiveCount++;
    }

    public void falsePositive() {
        falsePositiveCount++;
    }

    public void falseNegative() {
        falseNegativeCount++;
    }

    public double getPrecision() {
        return ((double) truePositiveCount) / (truePositiveCount + falsePositiveCount);
    }

    public double getRecall() {
        return ((double) truePositiveCount) / (truePositiveCount + falseNegativeCount);
    }

    public double getAveragePenalty() {
        return penalties.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .getAsDouble();
    }

    public double getMedianPenalty() {
        List<Double> values = penalties.stream()
                .map(Double::doubleValue)
                .sorted()
                .collect(toList());
        int size = values.size();
        if (size == 1) {
            return values.get(0);
        } else if (size % 2 == 1) {
            return values.get(size / 2);
        } else {
            return (values.get(size / 2) + values.get(size / 2 - 1)) / 2;
        }
    }

    public void addPenalty(double penalty) {
        penalties.add(penalty);
    }

    public Integer getUserId() {
        return userId;
    }

    public List<Double> getPenalties() {
        return penalties;
    }
}
