package com.xokker.datasets;

import com.xokker.datasets.cars.CarsPreferenceReader;
import com.xokker.datasets.sushi.SushiPreferencesFileReader;

/**
 * @author Ernest Sadykov
 * @since 20.04.2015
 */
public enum Datasets {

    Cars1  ("/exp1-prefs/items1.csv", "/exp1-prefs/users1.csv", "/exp1-prefs/prefs1.csv", new CarsPreferenceReader()),
    Cars2  ("/exp2-prefs/items2.csv", "/exp2-prefs/users2.csv", "/exp2-prefs/prefs2.csv", new CarsPreferenceReader()),
    SushiA ("/sushi3/sushi3A.idata", "/sushi3/sushi3.udata", "/sushi3/sushi3a.5000.10.order", new SushiPreferencesFileReader());

    private final String itemsPath;
    private final String usersPath;
    private final String prefsPath;
    private final PreferenceReader reader;

    Datasets(String itemsPath, String usersPath, String prefsPath, PreferenceReader reader) {
        String resources = "src/main/resources";
        this.itemsPath = resources + itemsPath;
        this.usersPath = resources + usersPath;
        this.prefsPath = resources + prefsPath;
        this.reader = reader;
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

    public PreferenceReader getReader() {
        return reader;
    }
}
