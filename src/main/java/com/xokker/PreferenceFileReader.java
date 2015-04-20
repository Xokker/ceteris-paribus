package com.xokker;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static java.lang.Integer.parseInt;

/**
 * @author Ernest Sadykov
 * @since 20.04.2015
 */
public class PreferenceFileReader {

    /**
     * @return key - userId, value - their preferences
     */
    public static Multimap<Integer, PrefEntry> readFromFile(String pathToFile) {
        Path path = Paths.get(pathToFile);

        ImmutableMultimap.Builder<Integer, PrefEntry> builder = ImmutableMultimap.builder();

        try {
            Files.lines(path)
                    .skip(1)
                    .map(s -> s.split(","))
                    .filter(ar -> Objects.equals(ar[3], "0"))
                    .forEach(ar -> builder.put(parseInt(ar[0]), new PrefEntry(parseInt(ar[1]), parseInt(ar[2]))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.build();
    }

}
