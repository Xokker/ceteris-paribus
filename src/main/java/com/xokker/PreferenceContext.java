package com.xokker;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.xokker.graph.PreferenceGraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singleton;

/**
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public class PreferenceContext<A> {

    private final Set<A> possibleAttributes;
    private final SetMultimap<Identifiable, A> objects;
    private final SetMultimap<A, Identifiable> attributesToObjects;
    private final PreferenceGraph preferenceGraph;

    public PreferenceContext(Set<A> possibleAttributes, PreferenceGraph preferenceGraph) {
        this.possibleAttributes = possibleAttributes;
        this.preferenceGraph = preferenceGraph;
        this.objects = HashMultimap.create();
        this.attributesToObjects = HashMultimap.create();
    }

    public void addObject(Identifiable id, Set<A> attributes) {
        for (A attribute : attributes) {
            Preconditions.checkArgument(
                    possibleAttributes.contains(attribute),
                    "context cannot hold '" + attribute + "' attribute");
            objects.put(id, attribute);
            attributesToObjects.put(attribute, id);
        }
    }

    public void addObjects(Map<Identifiable, Set<A>> objects) {
        for (Map.Entry<Identifiable, Set<A>> entry : objects.entrySet()) {
            addObject(entry.getKey(), entry.getValue());
        }
    }

    public Set<Identifiable> getAttributeExtent(A attribute) {
        return getAttributeExtent(singleton(attribute));
    }

    public Set<Identifiable> getAttributeExtent(Collection<A> attributes) {
        return attributes.stream()
                .map(attributesToObjects::get)
                .collect(
                        () -> (Set<Identifiable>)new HashSet<>(objects.keySet()),
                        Set::retainAll,
                        Set::retainAll);
    }

    public Set<A> getObjectIntent(Identifiable object) {
        return getObjectIntent(singleton(object));
    }

    public Set<A> getObjectIntent(Collection<Identifiable> objects0) {
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
    public boolean leq(Identifiable left, Identifiable right) {
        return preferenceGraph.leq(left, right);
    }

    public Set<Identifiable> getAllObjects() {
        return new HashSet<>(objects.keySet());
    }

    public Set<A> getAllAttributes() {
        return new HashSet<>(attributesToObjects.keySet());
    }
}
