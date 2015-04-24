package com.xokker.datasets.cars;

import com.google.common.collect.Multimap;
import com.xokker.Identifiable;
import com.xokker.PrefEntry;
import com.xokker.datasets.Datasets;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class CarPreferencesFileReaderTest {

    @Test
    public void testReadFromFile() throws Exception {
        Multimap<Integer, PrefEntry> prefs = CarPreferencesFileReader.readPreferences(Datasets.Cars1.getPrefsPath());

        assertNotNull(prefs);
        assertFalse(prefs.isEmpty());
        assertNotNull(prefs.get(1));
        assertFalse(prefs.get(1).isEmpty());
        assertEquals(60, prefs.keySet().size());
    }

    @Test
    public void testReadItems1() throws Exception {
        Map<Identifiable, Set<CarAttributes>> cars = CarPreferencesFileReader.readItems(Datasets.Cars1.getItemsPath());

        assertNotNull(cars);
        assertEquals(10, cars.keySet().size());
        for (Set<CarAttributes> carAttributeses : cars.values()) {
            assertEquals(4, carAttributeses.size());
        }
    }

    @Test
    public void testReadItems2() throws Exception {
        Map<Identifiable, Set<CarAttributes>> cars = CarPreferencesFileReader.readItems(Datasets.Cars2.getItemsPath());

        assertNotNull(cars);
        assertEquals(20, cars.keySet().size());
        for (Set<CarAttributes> carAttributeses : cars.values()) {
            assertEquals(5, carAttributeses.size());
        }
    }

    @Test
    public void testReadUsers() throws Exception {
        Set<Integer> users = CarPreferencesFileReader.readUsers(Datasets.Cars1.getUsersPath());

        assertNotNull(users);
        assertEquals(31, users.size());
    }
}