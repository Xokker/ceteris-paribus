package com.xokker.graph;

import com.xokker.Identifiable;
import com.xokker.IntIdentifiable;
import com.xokker.graph.impl.ArrayPreferenceGraph;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.IntIdentifiable.ii;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class PreferenceGraphTest {

    @Test
    public void testInitLinearOrder() throws Exception {
        PreferenceGraph graph = new ArrayPreferenceGraph(10);
        PreferenceGraph.initLinearOrder(
                graph,
                Stream.of(8, 6, 7, 10, 2, 9, 5, 1, 3, 4)
                        .map(i -> i - 1)
                        .map(IntIdentifiable::ii)
                        .collect(toList())
        );

        assertEquals(PrefState.Leq, graph.leq(ii(10 - 1), ii(3 - 1)));
        assertEquals(PrefState.Leq, graph.leq(ii(6 - 1), ii(7 - 1)));
        assertEquals(PrefState.Leq, graph.leq(ii(8 - 1), ii(4 - 1)));
        assertEquals(PrefState.Leq, graph.leq(ii(2 - 1), ii(1 - 1)));
    }

    @Test
    public void testInitBucketOrder() throws Exception {
        PreferenceGraph graph = new ArrayPreferenceGraph(10);

        List<Set<Identifiable>> buckets = newArrayList(
                newHashSet(ii(8 - 1)),
                newHashSet(ii(6 - 1)),
                newHashSet(ii(7 - 1), ii(10 - 1)),
                newHashSet(ii(2 - 1)),
                newHashSet(ii(9 - 1), ii(5 - 1), ii(1 - 1)),
                newHashSet(ii(3 - 1), ii(4 - 1))
        );
        PreferenceGraph.initBucketOrder(graph, buckets);

        assertEquals(PrefState.Leq, graph.leq(ii(10 - 1), ii(3 - 1)));
        assertEquals(PrefState.Leq, graph.leq(ii(6 - 1), ii(7 - 1)));
        assertEquals(PrefState.Leq, graph.leq(ii(8 - 1), ii(4 - 1)));
        assertEquals(PrefState.Leq, graph.leq(ii(2 - 1), ii(1 - 1)));
        assertEquals(PrefState.Leq, graph.leq(ii(5 - 1), ii(5 - 1)));

        // TODO: figure it out:
        assertEquals(PrefState.Unknown, graph.leq(ii(9 - 1), ii(1 - 1)));
        assertEquals(PrefState.Unknown, graph.leq(ii(5 - 1), ii(1 - 1)));
        assertEquals(PrefState.Unknown, graph.leq(ii(7 - 1), ii(10 - 1)));
    }
}