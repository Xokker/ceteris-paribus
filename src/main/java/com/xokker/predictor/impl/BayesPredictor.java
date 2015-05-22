package com.xokker.predictor.impl;

import com.xokker.PreferenceContext;
import com.xokker.datasets.Attribute;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.net.BayesNetGenerator;

/**
 * @author Ernest Sadykov
 * @since 17.05.2015
 */
public class BayesPredictor<A extends Attribute> extends WekaPredictor<A> {

    public BayesPredictor(PreferenceContext<A> context) {
        super(context);
    }

    @Override
    protected Classifier createClassifier() {
        return new BayesNetGenerator();
//        return new NaiveBayes();
    }

    @Override
    protected String[] getOptions() {
        return new String[0];
    }
}
