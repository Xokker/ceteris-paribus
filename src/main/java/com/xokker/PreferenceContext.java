package com.xokker;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.xokker.graph.PreferenceGraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singleton;

/**
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public class PreferenceContext<I extends Number, A> {

    private final Set<A> possibleAttributes;
    private final SetMultimap<I, A> objects;
    private final SetMultimap<A, I> attributesToObjects;
    private final PreferenceGraph<I> preferenceGraph;

    public PreferenceContext(Set<A> possibleAttributes, PreferenceGraph<I> preferenceGraph) {
        this.possibleAttributes = possibleAttributes;
        this.preferenceGraph = preferenceGraph;
        this.objects = HashMultimap.create();
        this.attributesToObjects = HashMultimap.create();
    }

    public void addObject(I id, Set<A> attributes) {
        for (A attribute : attributes) {
            Preconditions.checkArgument(
                    possibleAttributes.contains(attribute),
                    "context cannot hold '" + attribute + "' attribute");
            objects.put(id, attribute);
            attributesToObjects.put(attribute, id);
        }
    }

    public Set<I> getAttributeExtent(A attribute) {
        return getAttributeExtent(singleton(attribute));
    }

    public Set<I> getAttributeExtent(Collection<A> attributes) {
        return attributes.stream()
                .map(attributesToObjects::get)
                .collect(
                        () -> (Set<I>)new HashSet<>(objects.keySet()),
                        Set::retainAll,
                        Set::retainAll);
    }

    public Set<A> getObjectIntent(I object) {
        return getObjectIntent(singleton(object));
    }

    public Set<A> getObjectIntent(Collection<I> objects0) {
        return objects0.stream()
                .map(objects::get)
                .collect(
                        () -> (Set<A>)new HashSet<>(attributesToObjects.keySet()),
                        Set::retainAll,
                        Set::retainAll);
    }

    /**
     * Checks whether the left object is at least as good as right object
     */
    public boolean leq(I left, I right) {
        return preferenceGraph.leq(left, right);
    }

    public Set<I> getAllObjects() {
        return new HashSet<>(objects.keySet());
    }

    public Set<A> getAllAttributes() {
        return new HashSet<>(attributesToObjects.keySet());
    }
}
