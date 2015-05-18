package com.xokker.predictor.impl;

import com.xokker.PreferenceContext;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * @author Ernest Sadykov
 * @since 17.05.2015
 */
public class J48Predictor<A extends com.xokker.datasets.Attribute> extends WekaPredictor<A> {

    private final boolean usePairedNodes;

    public J48Predictor(PreferenceContext<A> context) {
        this(context, false);
    }

    public J48Predictor(PreferenceContext<A> context, boolean usePairedNodes) {
        super(context);
        this.usePairedNodes = usePairedNodes;
    }

    @Override
    protected Classifier createClassifier() {
        return new J48();
    }

    @Override
    protected String[] getOptions() {
        return new String[]{"-U", "-M", "1"};
    }

    @Override
    protected Instances createData(PreferenceContext<A> context) {
        FastVector attributes = new FastVector();

        // Add attributes for left and right objects in pair
        for (A attribute : allAttributes) {
            FastVector values = new FastVector(2);
            values.addElement(YES);
            values.addElement(NO);
            attributes.addElement(new Attribute(attribute.toString() + "_l", values));
            attributes.addElement(new Attribute(attribute.toString() + "_r", values));
        }

        attributes.addElement(new Attribute("Class", createClassAttribute()));

        return new Instances("datasetName", attributes, 100);
    }
}
