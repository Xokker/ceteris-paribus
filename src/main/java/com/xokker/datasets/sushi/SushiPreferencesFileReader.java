package com.xokker.datasets.sushi;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.xokker.Identifiable;
import com.xokker.IntIdentifiable;
import com.xokker.PrefEntry;
import com.xokker.datasets.PreferenceReader;
import com.xokker.graph.PreferenceGraph;
import com.xokker.graph.impl.ArrayPreferenceGraph;

import java.io.IOException;
import java.util.*;

import static com.xokker.IntIdentifiable.ii;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

/**
 * @author Ernest Sadykov
 * @since 20.04.2015
 */
public class SushiPreferencesFileReader implements PreferenceReader<SushiAttribute> {

    /**
     * @return key - userId, value - their preferences
     */
    @Override
    public Multimap<Integer, PrefEntry> readPreferences(String pathToFile, List<Integer> users) throws IOException {
        ImmutableMultimap.Builder<Integer, PrefEntry> builder = ImmutableMultimap.builder();
        List<List<Identifiable>> rows = PreferenceReader.readLines(pathToFile).stream()
                .skip(1)                    // skip header
                .map(s -> s.split(" "))
                .map(ar -> Arrays.copyOfRange(ar, 2, ar.length))
                .map(ar -> Arrays.stream(ar).map(Integer::parseInt).map(IntIdentifiable::ii).collect(toList()))
                .collect(toList());

        for (int i = 0; i < users.size(); i++) {
            Integer user = users.get(i);
            ArrayPreferenceGraph preferenceGraph = new ArrayPreferenceGraph(10);
            PreferenceGraph.initLinearOrder(preferenceGraph, rows.get(i));
            builder.putAll(user, preferenceGraph.toEntries());
        }

        return builder.build();
    }

    private static PrefEntry createPrefEntry(String left, String right) {
        Identifiable l = ii(parseInt(left) - 1);
        Identifiable r = ii(parseInt(right) - 1);

        return new PrefEntry(l, r);
    }

    @Override
    public Map<Identifiable, Set<SushiAttribute>> readItems(String pathToFile) throws IOException {
        List<String> lines = PreferenceReader.readLines(pathToFile);
        String[] headers = getHeaders(lines.get(0));
        Map<Identifiable, Set<SushiAttribute>> result = new HashMap<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] splitted = line.split("\t");
            Set<SushiAttribute> atts = new HashSet<>();
            for (int columnNo = 2; columnNo < splitted.length; ++columnNo) { // pass id and name atts
                String category = headers[columnNo];
                SushiAttribute attribute = SushiAttribute.get(category, splitted[columnNo]);
                atts.add(attribute);
            }
            result.put(ii(i - 1), atts);
        }

        return result;
    }

    private String[] getHeaders(String headerLine) {
        String[] splittedHeaders = headerLine.split("\t");
        int noOfHeaders = splittedHeaders.length;
        String[] headers = new String[noOfHeaders];
        for (int i = 0; i < noOfHeaders; i++) {
            headers[i] = splittedHeaders[i].trim();
        }

        return headers;
    }

    @Override
    public List<Integer> readUsers(String pathToFile) throws IOException {
        return PreferenceReader.readLines(pathToFile).stream()
                .map(s -> s.split("\t"))
                .map(ar -> ar[0])
                .map(Integer::parseInt)
                .collect(toList());
    }
}
