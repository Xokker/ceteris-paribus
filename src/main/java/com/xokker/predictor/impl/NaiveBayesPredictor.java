package com.xokker.predictor.impl;

import com.xokker.PreferenceContext;
import com.xokker.datasets.Attribute;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;

/**
 * @author Ernest Sadykov
 * @since 17.05.2015
 */
public class NaiveBayesPredictor<A extends Attribute> extends WekaPredictor<A> {

    public NaiveBayesPredictor(PreferenceContext<A> context) {
        super(context);
    }

    @Override
    protected Classifier createClassifier() {
        return new NaiveBayes();
    }

    @Override
    protected String[] getOptions() {
        return new String[0];
    }
}
