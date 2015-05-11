package com.xokker;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * @author Ernest Sadykov
 * @since 22.04.2015
 */
public class Attributes {
    public static final String Minivan = "minivan";
    public static final String SUV = "SUV";
    public static final String RedExterior = "red exterior";
    public static final String WhiteExterior = "white exterior";
    public static final String BrightInterior = "bright interior";
    public static final String DarkInterior = "dark interior";

    public static final Set<String> AllAttrs = newHashSet(Minivan, SUV, RedExterior, WhiteExterior, BrightInterior, DarkInterior);
}
