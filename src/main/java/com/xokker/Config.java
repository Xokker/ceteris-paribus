package com.xokker;

import org.apache.commons.lang3.BooleanUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Ernest Sadykov
 * @since 24.04.2015
 */
public class Config {

    private static boolean quiteMode;

    static {
        Properties config = new Properties();
        try {
            config.load(Config.class.getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        quiteMode = BooleanUtils.toBoolean(config.getProperty("quite_mode", "false"));
    }

    @Deprecated
    public static boolean isQuiteMode() {
        return quiteMode;
    }

}
