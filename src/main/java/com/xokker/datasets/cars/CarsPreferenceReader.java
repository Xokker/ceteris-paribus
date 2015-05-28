package com.xokker.datasets.cars;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.xokker.Identifiable;
import com.xokker.PrefEntry;
import com.xokker.datasets.PreferenceReader;

import java.io.IOException;
import java.util.*;

import static com.xokker.IntIdentifiable.ii;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author Ernest Sadykov
 * @since 20.04.2015
 */
public class CarsPreferenceReader implements PreferenceReader<CarAttribute> {

    /**
     * @return key - userId, value - their preferences
     */
    public Multimap<Integer, PrefEntry> readPreferences(String pathToFile, List<Integer> users) throws IOException {
        ImmutableMultimap.Builder<Integer, PrefEntry> builder = ImmutableMultimap.builder();
        PreferenceReader.readLines(pathToFile).stream()
                .skip(1)                                  // skip header
                .map(s -> s.split(","))
                .filter(ar -> Objects.equals(ar[3], "0")) // skip control questions
                .forEach(ar -> builder.put(parseInt(ar[0]), createPrefEntry(ar[1], ar[2])));

        return builder.build();
    }

    private PrefEntry createPrefEntry(String left, String right) {
        Identifiable l = ii(parseInt(left) - 1);
        Identifiable r = ii(parseInt(right) - 1);

        return new PrefEntry(l, r);
    }

    public Map<Identifiable, Set<CarAttribute>> readItems(String pathToFile) throws IOException {
        List<String> lines = PreferenceReader.readLines(pathToFile);
        String[] headers = getHeaders(lines.get(0));

        return lines.stream()
                .skip(1)
                .map(s -> s.split(","))
                .collect(toMap(
                        ar -> ii(parseInt(ar[0]) - 1),
                        ar -> {
                            Set<CarAttribute> attrs = EnumSet.noneOf(CarAttribute.class);
                            for (int i = 1; i < ar.length; i++) {
                                attrs.add(CarAttribute.get(headers[i], ar[i]));
                            }
                            return attrs;
                        }));
    }

    private String[] getHeaders(String headerLine) {
        String[] splittedHeaders = headerLine.split(",");
        int noOfHeaders = splittedHeaders.length;
        String[] headers = new String[noOfHeaders];
        for (int i = 0; i < noOfHeaders; i++) {
            headers[i] = splittedHeaders[i].trim();
        }

        return headers;
    }

    public List<Integer> readUsers(String pathToFile) throws IOException {
        return PreferenceReader.readLines(pathToFile).stream()
                .skip(1)
                .map(s -> s.split(","))
                .filter(ar -> "5".equals(ar[5])) // exclude liars
                .map(ar -> ar[0])
                .map(Integer::parseInt)
                .collect(toList());
    }
}
