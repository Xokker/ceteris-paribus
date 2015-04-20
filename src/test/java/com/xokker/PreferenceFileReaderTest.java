package com.xokker;

import com.google.common.collect.Multimap;
import org.junit.Test;

import static org.junit.Assert.*;

public class PreferenceFileReaderTest {

    @Test
    public void testReadFromFile() throws Exception {
        Multimap<Integer, PrefEntry> prefs = PreferenceFileReader.readFromFile(Datasets.PathCars1);

        assertNotNull(prefs);
        assertFalse(prefs.isEmpty());
        assertNotNull(prefs.get(1));
        assertFalse(prefs.get(1).isEmpty());
        assertEquals(60, prefs.keySet().size());
    }

}