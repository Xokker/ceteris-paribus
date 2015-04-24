package com.xokker.datasets.cars;

import com.google.common.collect.Multimap;
import com.xokker.PrefEntry;
import com.xokker.datasets.Datasets;
import org.junit.Test;

import static org.junit.Assert.*;

public class CarPreferencesFileReaderTest {

    @Test
    public void testReadFromFile() throws Exception {
        Multimap<Integer, PrefEntry> prefs = CarPreferencesFileReader.readFromFile(Datasets.Cars1);

        assertNotNull(prefs);
        assertFalse(prefs.isEmpty());
        assertNotNull(prefs.get(1));
        assertFalse(prefs.get(1).isEmpty());
        assertEquals(60, prefs.keySet().size());
    }

}