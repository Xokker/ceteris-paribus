package com.xokker.predictor;

import com.xokker.datasets.cars.CarAttribute;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PredicateListTest {

    /*
        this:
        EQ -> {Body -> (Sedan, Sedan),
               Transmission -> (Manual, Manual)}
        LT -> {Engine -> (S, XL)}

        that:
        EQ -> {Fuel -> (Hybrid, Hybrid),
               Transmission -> (Automatic, Automatic),
               Body -> (Sedan, Sedan)}
        LT -> {Engine -> (XS, XL)}

        result:
        EQ -> {Body -> (Sedan, Sedan)}
        LT -> {Engine -> (S, XL)}
     */
    @Test
    public void testIntersect() throws Exception {
        PredicateList<CarAttribute> pl = new PredicateList<>();
        pl.put(AttributePredicate.Equality, "BodyType", Pair.of(CarAttribute.Sedan, CarAttribute.Sedan));
        pl.put(AttributePredicate.Equality, "Transmission", Pair.of(CarAttribute.Manual, CarAttribute.Manual));
        pl.put(AttributePredicate.LessThan, "Engin Capacity", Pair.of(CarAttribute.EngineS, CarAttribute.EngineXL));


        PredicateList<CarAttribute> pl2 = new PredicateList<>();
        pl2.put(AttributePredicate.Equality, "Fuel Consumed", Pair.of(CarAttribute.Hybrid, CarAttribute.Hybrid));
        pl2.put(AttributePredicate.Equality, "Transmission", Pair.of(CarAttribute.Automatic, CarAttribute.Automatic));
        pl2.put(AttributePredicate.Equality, "BodyType", Pair.of(CarAttribute.Sedan, CarAttribute.Sedan));
        pl2.put(AttributePredicate.LessThan, "Engin Capacity", Pair.of(CarAttribute.EngineXS, CarAttribute.EngineXL));

        PredicateList<CarAttribute> result = pl.intersect(pl2);
        assertTrue(result.getData().containsKey(AttributePredicate.LessThan));
        System.out.println(result);
    }


}