package com.xokker.graph;

import com.xokker.Identifiable;

/**
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public interface PreferenceGraph {

    /**
     * Checks whether the left object is at least as good as right object
     */
    boolean leq(Identifiable left, Identifiable right);

    /**
     * Asserts that the right object is at least as good as left object
     */
    void setLeq(Identifiable left, Identifiable right);

}
