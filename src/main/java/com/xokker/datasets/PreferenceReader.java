package com.xokker.datasets;

import com.google.common.collect.Multimap;
import com.xokker.Identifiable;
import com.xokker.PrefEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ernest Sadykov
 * @since 27.05.2015
 */
public interface PreferenceReader<A extends Attribute<A>> {

    Multimap<Integer, PrefEntry> readPreferences(String pathToFile, List<Integer> users) throws IOException;

    List<Integer> readUsers(String pathToFile) throws IOException;

    Map<Identifiable, Set<A>> readItems(String pathToFile) throws IOException;

    static List<String> readLines(String pathToFile) throws IOException {
        Path path = Paths.get(pathToFile);
        return Files.readAllLines(path);
    }

}
