package com.xokker;

import static com.google.common.collect.Sets.newHashSet;
import static com.xokker.Attributes.*;

/**
 * @author Ernest Sadykov
 * @since 22.04.2015
 */
public class ContextUrils {
    public static void addObjects(PreferenceContext<Integer, String> context) {
        context.addObject(1 - 1, newHashSet(Minivan, WhiteExterior, DarkInterior));
        context.addObject(2 - 1, newHashSet(SUV, WhiteExterior, DarkInterior));
        context.addObject(3 - 1, newHashSet(Minivan, WhiteExterior, BrightInterior));
        context.addObject(4 - 1, newHashSet(SUV, RedExterior, DarkInterior));
        context.addObject(5 - 1, newHashSet(Minivan, RedExterior, DarkInterior));
    }
}
