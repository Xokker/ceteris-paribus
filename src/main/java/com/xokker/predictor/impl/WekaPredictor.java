package com.xokker.predictor.impl;

import com.google.common.collect.Sets;
import com.xokker.Identifiable;
import com.xokker.PreferenceContext;
import com.xokker.graph.PrefState;
import com.xokker.predictor.PreferencePredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Collections;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

/**
 * @author Ernest Sadykov
 * @since 11.05.2015
 */
public abstract class WekaPredictor<A extends com.xokker.datasets.Attribute> implements PreferencePredictor<A> {

    private static final Logger logger = LoggerFactory.getLogger(WekaPredictor.class);

    public static final String NO = "no";
    public static final String YES = "yes";

    /* The training data gathered so far. */
    private Instances data = null;

    /* The actual classifier. */
    private Classifier classifier = createClassifier();

    private PreferenceContext<A> context;

    protected abstract Classifier createClassifier();

    protected abstract String[] getOptions();

    /**
     * Constructs empty training dataset.
     */
    public WekaPredictor(PreferenceContext<A> context) {
        this.context = context;
    }

    public void init() {
        // Create vector of attributes.
        data = createData(context);
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
            classifier.setOptions(getOptions());
            classifier.buildClassifier(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        logger.info("classifier: {}", classifier);
    }

    protected Instances createData(PreferenceContext<A> context) {
        FastVector attributes = new FastVector();

        // Add attributes for left and right objects in pair
        for (A attribute : getAllAttributes()) {
            FastVector values = new FastVector(2);
            values.addElement(YES);
            values.addElement(NO);
            attributes.addElement(new Attribute(attribute.toString() + "_l", values));
            attributes.addElement(new Attribute(attribute.toString() + "_r", values));
        }

        attributes.addElement(new Attribute("Class", createClassAttribute()));

        return new Instances("datasetName", attributes, 100);
    }

    protected FastVector createClassAttribute() {
        FastVector classValues = new FastVector(2);
        classValues.addElement(PrefState.Leq.name());
        classValues.addElement(PrefState.NotLeq.name());

        return classValues;
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
    protected Instance makeInstance(Set<A> leftAttributes, Set<A> rightAttributes, Instances data) {

        // Create instance of length two.
        Instance instance = new Instance(getAllAttributes().size() * 2 + 1);
        instance.setDataset(data);

        for (A attribute : leftAttributes) {
            Attribute att = data.attribute(attribute.toString() + "_l");
            instance.setValue(att.index(), YES);
        }
        for (A attribute : rightAttributes) {
            Attribute att = data.attribute(attribute.toString() + "_r");
            instance.setValue(att.index(), YES);
        }
        for (A attribute : getAllAttributes()) {
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

    protected Set<A> getAllAttributes() {
        return Collections.unmodifiableSet(context.getAllAttributes());
    }
}