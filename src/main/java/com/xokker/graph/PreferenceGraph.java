package com.xokker.graph;

/**
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public interface PreferenceGraph<I extends Number> {

    /**
     * Checks whether the left object is at least as good as right object
     */
    boolean leq(I left, I right);

    /**
     * Asserts that the right object is at least as good as left object
     */
    void setLeq(I left, I right);

}
