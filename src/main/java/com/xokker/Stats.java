package com.xokker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ernest Sadykov
 * @since 25.04.2015
 */
public class Stats {

    private Integer userId;
    private List<Double> penalties;

    public Stats(Integer userId) {
        this.userId = userId;
        penalties = new ArrayList<>();
    }

    public Stats() {
        this(null);
    }

    public double getAveragePenalty() {
        return penalties.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .getAsDouble();
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
