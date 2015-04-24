package com.xokker.datasets;

/**
 * @author Ernest Sadykov
 * @since 20.04.2015
 */
public enum Datasets {

    Cars1 ("/exp1-prefs/items1.csv", "/exp1-prefs/users1.csv", "/exp1-prefs/prefs1.csv"),
    Cars2 ("/exp2-prefs/items2.csv", "/exp2-prefs/users2.csv", "/exp2-prefs/prefs2.csv");

    private final String itemsPath;
    private final String usersPath;
    private final String prefsPath;

    Datasets(String itemsPath, String usersPath, String prefsPath) {
        String resources = "src/main/resources";
        this.itemsPath = resources + itemsPath;
        this.usersPath = resources + usersPath;
        this.prefsPath = resources + prefsPath;
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
}
