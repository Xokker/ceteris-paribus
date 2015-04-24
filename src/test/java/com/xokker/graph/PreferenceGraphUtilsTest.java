package com.xokker.graph;

import com.xokker.IntIdentifiable;
import com.xokker.graph.impl.ArrayPreferenceGraph;
import org.junit.Test;

import java.util.stream.Stream;

import static com.xokker.IntIdentifiable.ii;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;

public class PreferenceGraphUtilsTest {

    @Test
    public void testInitLinearOrder() throws Exception {
        PreferenceGraph graph = new ArrayPreferenceGraph(10);
        PreferenceGraphUtils.initLinearOrder(
                graph,
                Stream.of(8, 6, 7, 10, 2, 9, 5, 1, 3, 4)
                        .map(i -> i - 1)
                        .map(IntIdentifiable::ii)
                        .collect(toList())
        );

        assertTrue(graph.leq(ii(10 - 1), ii(3 - 1)));
        assertTrue(graph.leq(ii(6 - 1), ii(7 - 1)));
        assertTrue(graph.leq(ii(8 - 1), ii(4 - 1)));
        assertTrue(graph.leq(ii(2 - 1), ii(1 - 1)));
    }

}