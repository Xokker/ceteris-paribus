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

/**
 * @author Ernest Sadykov
 * @since 20.04.2015
 */
public class CarPreferencesFileReader {

    /**
     * @return key - userId, value - their preferences
     */
    public static Multimap<Integer, PrefEntry> readPreferences(String pathToFile) throws IOException {
        Path path = Paths.get(pathToFile);

        ImmutableMultimap.Builder<Integer, PrefEntry> builder = ImmutableMultimap.builder();
        Files.lines(path)
                .skip(1)                                  // skip header
                .map(s -> s.split(","))
                .filter(ar -> Objects.equals(ar[3], "0")) // skip control questions
                .forEach(ar -> builder.put(parseInt(ar[0]), new PrefEntry(parseInt(ar[1]), parseInt(ar[2]))));

        return builder.build();
    }

    public static Map<Identifiable, Set<CarAttributes>> readItems(String pathToFile) throws IOException {
        Path path = Paths.get(pathToFile);

        List<String> lines = Files.readAllLines(path);
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

}
