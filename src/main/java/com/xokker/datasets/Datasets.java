package com.xokker.datasets;

import com.xokker.datasets.cars.CarsPreferenceReader;
import com.xokker.datasets.sushi.SushiPreferencesFileReader;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Ernest Sadykov
 * @since 20.04.2015
 */
public enum Datasets {

    Cars1  ("/exp1-prefs/items1.csv", "/exp1-prefs/users1.csv", "/exp1-prefs/prefs1.csv", new CarsPreferenceReader()),
    Cars2  ("/exp2-prefs/items2.csv", "/exp2-prefs/users2.csv", "/exp2-prefs/prefs2.csv", new CarsPreferenceReader()),
    SushiA ("/sushi3/sushi3A.idata", "/sushi3/sushi3.udata", "/sushi3/sushi3a.5000.10.order", "/sushi3/rand_users.txt", new SushiPreferencesFileReader());

    private final String itemsPath;
    private final String usersPath;
    private final String prefsPath;
    private final String randomUsers;
    private final PreferenceReader reader;

    Datasets(String itemsPath, String usersPath, String prefsPath, String randomUsers, PreferenceReader reader) {
        String resources = "src/main/resources";
        this.itemsPath = resources + itemsPath;
        this.usersPath = resources + usersPath;
        this.prefsPath = resources + prefsPath;
        this.randomUsers = resources + randomUsers;
        this.reader = reader;
    }

    Datasets(String itemsPath, String usersPath, String prefsPath, PreferenceReader reader) {
        this(itemsPath, usersPath, prefsPath, null, reader);
    }

    public String getItemsPath() {
        return itemsPath;
    }

    public String getUsersPath() {
        return usersPath;
    }

    public String getPrefsPath() {
        return prefsPath;
    }

    public String getRandomUsers() {
        return randomUsers;
    }

    public PreferenceReader getReader() {
        return reader;
    }

    public List<Integer> usersForIteration() throws IOException {
        if (randomUsers != null) {
            return PreferenceReader.readLines(randomUsers).stream()
                    .map(Integer::parseInt)
                    .collect(toList());
        }
        return null;
    }
}
