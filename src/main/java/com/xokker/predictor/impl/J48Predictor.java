package com.xokker.predictor.impl;

import com.google.common.collect.Sets;
import com.xokker.Identifiable;
import com.xokker.PreferenceContext;
import com.xokker.graph.PrefState;
import com.xokker.predictor.PreferencePredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

/**
 * @author Ernest Sadykov
 * @since 11.05.2015
 */
public class J48Predictor<A> implements PreferencePredictor<A> {

    private static final Logger logger = LoggerFactory.getLogger(J48Predictor.class);

    private static final String NO = "no";
    private static final String YES = "yes";

    /* The training data gathered so far. */
    private Instances data = null;

    /* The actual classifier. */
    private Classifier classifier = new J48();

    private Set<A> allAttributes;

    /**
     * Constructs empty training dataset.
     */
    public J48Predictor(PreferenceContext<A> context) {
        // Create vector of attributes.
        FastVector attributes = new FastVector();

        allAttributes = context.getPossibleAttributes();

        // Add attributes for left and right objects in pair
        for (A attribute : allAttributes) {
            FastVector values = new FastVector(2);
            values.addElement(YES);
            values.addElement(NO);
            attributes.addElement(new Attribute(attribute.toString() + "_l", values));
            attributes.addElement(new Attribute(attribute.toString() + "_r", values));
        }

        // Add class attribute.
        FastVector classValues = new FastVector(2);
        classValues.addElement(PrefState.Leq.name());
        classValues.addElement(PrefState.NotLeq.name());
        attributes.addElement(new Attribute("Class", classValues));

        // Create dataset with initial capacity of 100, and set index of class.
        data = new Instances("datasetName", attributes, 100);
        data.setClassIndex(data.numAttributes() - 1);

        for (Identifiable left : context.getAllObjects()) {
            for (Identifiable right : Sets.difference(context.getAllObjects(), singleton(left))) {
                PrefState state = context.leq(left, right);
                if (state != PrefState.Unknown) {
                    updateData(context.getObjectIntent(left),
                            context.getObjectIntent(right),
                            state.name());
                }
            }
        }

        // unpruned tree, min number of instances per leaf = 1:
        try {
            classifier.setOptions(new String[]{"-U", "-M", "1"});
            classifier.buildClassifier(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        logger.debug("classifier: {}", classifier);
        System.out.println(classifier);
    }

    /**
     * Updates model using the given training message.
     */
    public void updateData(Set<A> a, Set<A> b, String classValue) {

        // Make message into instance.
        Instance instance = makeInstance(a, b, data);

        // Set class value for instance.
        instance.setClassValue(classValue);

        // Add instance to training data.
        data.add(instance);
    }

    /**
     * Classifies a given message.
     */
    public String classifyMessage(Set<A> a, Set<A> b) {

        // Check whether classifier has been built.
        if (data.numInstances() == 0) {
            throw new IllegalStateException("No classifier available.");
        }

        // Make separate little test set so that message
        // does not get added to string attribute in data.
        Instances testset = new Instances(data);

        // Make message into test instance.
        Instance instance = makeInstance(a, b, testset);

        // Get index of predicted class value.
        double predicted;
        try {
            predicted = classifier.classifyInstance(instance);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return data.classAttribute().value((int)predicted);
    }

    /**
     * Method that converts a text message into an instance.
     */
    private Instance makeInstance(Set<A> leftAttributes, Set<A> rightAttributes, Instances data) {

        // Create instance of length two.
        Instance instance = new Instance(allAttributes.size() * 2 + 1);
        instance.setDataset(data);

        for (A attribute : leftAttributes) {
            Attribute att = data.attribute(attribute.toString() + "_l");
            instance.setValue(att.index(), YES);
        }
        for (A attribute : rightAttributes) {
            Attribute att = data.attribute(attribute.toString() + "_r");
            instance.setValue(att.index(), YES);
        }
        for (A attribute : allAttributes) {
            if (!leftAttributes.contains(attribute)) {
                Attribute att = data.attribute(attribute.toString() + "_l");
                instance.setValue(att.index(), NO);
            }
            if (!rightAttributes.contains(attribute)) {
                Attribute att = data.attribute(attribute.toString() + "_r");
                instance.setValue(att.index(), NO);
            }
        }

        // Give instance access to attribute information from the dataset.
        instance.setDataset(data);

        return instance;
    }

    @Override
    public Set<Support> predictPreference(Set<A> a, Set<A> b) {
        return PrefState.Leq.name().equals(classifyMessage(a, b)) ? singleton(Support.OK) : emptySet();
    }
}