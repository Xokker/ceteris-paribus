package com.xokker.predictor.impl;

import com.xokker.PreferenceContext;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.net.BayesNetGenerator;

/**
 * @author Ernest Sadykov
 * @since 17.05.2015
 */
public class BayesPredictor<A> extends WekaPredictor<A> {

    public BayesPredictor(PreferenceContext<A> context) {
        super(context);
    }

    @Override
    protected Classifier createClassifier() {
        return new BayesNetGenerator();
    }

    @Override
    protected String[] getOptions() {
        return new String[0];
    }
}
