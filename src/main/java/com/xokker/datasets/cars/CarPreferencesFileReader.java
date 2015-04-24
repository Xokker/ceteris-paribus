package com.xokker.datasets.cars;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.xokker.Identifiable;
import com.xokker.PrefEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.xokker.IntIdentifiable.ii;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toSet;

/**
 * @author Ernest Sadykov
 * @since 20.04.2015
 */
public class CarPreferencesFileReader {

    /**
     * @return key - userId, value - their preferences
     */
    public static Multimap<Integer, PrefEntry> readPreferences(String pathToFile) throws IOException {
        ImmutableMultimap.Builder<Integer, PrefEntry> builder = ImmutableMultimap.builder();
        readLines(pathToFile).stream()
                .skip(1)                                  // skip header
                .map(s -> s.split(","))
                .filter(ar -> Objects.equals(ar[3], "0")) // skip control questions
                .forEach(ar -> builder.put(parseInt(ar[0]), createPrefEntry(ar[1], ar[2])));

        return builder.build();
    }

    private static PrefEntry createPrefEntry(String left, String right) {
        Identifiable l = ii(parseInt(left) - 1);
        Identifiable r = ii(parseInt(right) - 1);

        return new PrefEntry(l, r);
    }

    public static Map<Identifiable, Set<CarAttributes>> readItems(String pathToFile) throws IOException {
        List<String> lines = readLines(pathToFile);
        Map<Integer, String> headers = new HashMap<>();
        String[] splittedHeaders = lines.get(0).split(",");
        for (int i = 0; i < splittedHeaders.length; i++) {
            headers.put(i, splittedHeaders[i].trim());
        }

        Map<Identifiable, Set<CarAttributes>> result = new HashMap<>();
        lines.stream()
                .skip(1)
                .map(s -> s.split(","))
                .forEach(ar -> {
                    int id = Integer.parseInt(ar[0]);
                    Set<CarAttributes> attrs = EnumSet.noneOf(CarAttributes.class);
                    for (int i = 1; i < ar.length; i++) {
                        attrs.add(CarAttributes.get(headers.get(i), ar[i]));
                    }
                    result.put(ii(id - 1), attrs);
                });

        return result;
    }

    public static Set<Integer> readUsers(String pathToFile) throws IOException {
        return readLines(pathToFile).stream()
                .map(s -> s.split(","))
                .filter(ar -> "5".equals(ar[5])) // exclude liars
                .map(ar -> ar[0])
                .map(Integer::parseInt)
                .collect(toSet());
    }

    private static List<String> readLines(String pathToFile) throws IOException {
        Path path = Paths.get(pathToFile);
        return Files.readAllLines(path);
    }

}
