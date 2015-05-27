package com.xokker.datasets;

import com.google.common.collect.Multimap;
import com.xokker.Identifiable;
import com.xokker.PrefEntry;
import com.xokker.datasets.cars.CarAttribute;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ernest Sadykov
 * @since 27.05.2015
 */
public interface PreferenceReader {

    Multimap<Integer, PrefEntry> readPreferences(String pathToFile, List<Integer> users) throws IOException;

    List<Integer> readUsers(String pathToFile) throws IOException;

    Map<Identifiable, Set<CarAttribute>> readItems(String pathToFile) throws IOException;

}
