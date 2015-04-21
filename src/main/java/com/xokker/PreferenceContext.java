package com.xokker;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public class PreferenceContext<I, A> {

    private final Set<A> possibleAttributes;
    private final SetMultimap<I, A> objects;
    private final SetMultimap<A, I> attributesToObjects;

    public PreferenceContext(Set<A> possibleAttributes) {
        this.possibleAttributes = possibleAttributes;
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

    public Set<I> getAttributeExtent(Set<A> attributes) {
        return attributes.stream()
                .map(attributesToObjects::get)
                .collect(
                        () -> (Set<I>)new HashSet<>(objects.keySet()),
                        Set::retainAll,
                        Set::retainAll);
    }

    public Set<A> getObjectIntent(Set<I> objects0) {
        return objects0.stream()
                .map(objects::get)
                .collect(
                        () -> (Set<A>)new HashSet<>(attributesToObjects.keySet()),
                        Set::retainAll,
                        Set::retainAll);
    }

}
