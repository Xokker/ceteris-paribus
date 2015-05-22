package com.xokker.datasets.cars;

import com.google.common.base.Preconditions;
import com.xokker.datasets.Attribute;

/**
 * Attributes of cars from car preferences datasets
 * (http://users.cecs.anu.edu.au/~u4940058/CarPreferences.html)
 *
 *  - Body type: Sedan (1), SUV(2), Hatchback(3)
 *  - Transmission: Manual (1), Automatic (2)
 *  - Engine capacity: 2.5L, 3.5L, 4.5L, 5.5L, 6.2L
 *  - Fuel consumed: Hybrid (1), Non-Hybrid (2)
 *  - Engine/Transmission layout: All-wheel-drive (AWD) (1), Forward-wheel-drive (FWD) (2)
 *
 * @author Ernest Sadykov
 * @since 24.04.2015
 */
public enum CarAttribute implements Attribute {

    Sedan       ("BodyType", "1"),
    SUV         ("BodyType", "2"),
    Hatchback   ("BodyType", "3"),

    Manual      ("Transmission", "1"),
    Automatic   ("Transmission", "2"),

    EngineXS    ("Engin Capacity", "2.5", true),
    EngineS     ("Engin Capacity", "3.5", true),
    EngineM     ("Engin Capacity", "4.5", true),
    EngineL     ("Engin Capacity", "5.5", true),
    EngineXL    ("Engin Capacity", "6.2", true),

    Hybrid      ("Fuel Consumed", "1"),
    NonHybrid   ("Fuel Consumed", "2"),

    AWD         ("Engine/Transmission Layout", "1"),
    FWD         ("Engine/Transmission Layout", "2");

    private final String category;
    private final String id;
    private final boolean numeric;

    CarAttribute(String category, String id) {
        this(category, id, false);
    }

    CarAttribute(String category, String id, boolean isNumeric) {
        this.category = category;
        this.id = id;
        this.numeric = isNumeric;
    }

    public static CarAttribute get(String category, String id) {
        Preconditions.checkNotNull(category);
        Preconditions.checkNotNull(id);

        for (CarAttribute item : values()) {
            if (category.equals(item.getCategory()) && id.equals(item.getId())) {
                return item;
            }
        }

        return null;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public boolean isNumeric() {
        return numeric;
    }

    @Override
    public double asDouble() {
        if (isNumeric()) {
            return Double.parseDouble(id);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public String getId() {
        return id;
    }
}
