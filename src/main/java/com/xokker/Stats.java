package com.xokker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ernest Sadykov
 * @since 25.04.2015
 */
public class Stats {

    private Integer userId;
    private List<Integer> penalties;

    public Stats(Integer userId) {
        this.userId = userId;
        penalties = new ArrayList<>();
    }

    public Stats() {
        this(null);
    }

    public double getAveragePenalty() {
        return penalties.stream()
                .mapToInt(Integer::intValue)
                .average()
                .getAsDouble();
    }

    public void addPenalty(int penalty) {
        penalties.add(penalty);
    }

    public Integer getUserId() {
        return userId;
    }

    public List<Integer> getPenalties() {
        return penalties;
    }
}
