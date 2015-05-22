package com.xokker.predictor.impl;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.xokker.PreferenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

/**
 * @author Ernest Sadykov
 * @since 17.05.2015
 */
public class J48Predictor<A extends com.xokker.datasets.Attribute> extends WekaPredictor<A> {

    private static final Logger logger = LoggerFactory.getLogger(J48Predictor.class);

    private final boolean usePairedNodes;
    private final boolean supportNumeric;

    public J48Predictor(PreferenceContext<A> context) {
        this(context, false, false);
    }

    public J48Predictor(PreferenceContext<A> context, boolean usePairedNodes, boolean supportNumeric) {
        super(context);
        this.usePairedNodes = usePairedNodes;
        this.supportNumeric = supportNumeric;
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
        if (!usePairedNodes) {
            return super.createData(context);
        }

        FastVector attributes = new FastVector();

        ListMultimap<String, A> grouped = groupedAttributes();

        for (String category : grouped.keySet()) {
            List<A> atts = grouped.get(category);
            if (supportNumeric && atts.get(0).isNumeric()) {
                attributes.addElement(new Attribute(category));
            } else {
                FastVector values = new FastVector(atts.size() * atts.size());
                for (A i : atts) {
                    for (A j : atts) {
                        values.addElement(createPair(i, j));
                    }
                }
                attributes.addElement(new Attribute(category, values));
            }
        }

        attributes.addElement(new Attribute("Class", createClassAttribute()));

        return new Instances("datasetName", attributes, 100);
    }

    private String createPair(A i, A j) {
        return i.toString() + "-" + j.toString();
    }

    private ImmutableListMultimap<String, A> groupedAttributes() {
        return Multimaps.index(getAllAttributes(), com.xokker.datasets.Attribute::getCategory);
    }

    @Override
    protected Instance makeInstance(Set<A> leftAttributes, Set<A> rightAttributes, Instances data) {
        if (!usePairedNodes) {
            return super.makeInstance(leftAttributes, rightAttributes, data);
        }

        Multimap<String, A> grouped = groupedAttributes();

        // Create instance of length two.
        Instance instance = new Instance(grouped.keySet().size() + 1);
        instance.setDataset(data);

        Map<String, A> leftIndex = leftAttributes.stream().collect(toMap(
                com.xokker.datasets.Attribute::getCategory,
                a -> a));

        Map<String, A> rightIndex = rightAttributes.stream().collect(toMap(
                com.xokker.datasets.Attribute::getCategory,
                a -> a));

        for (String category : leftIndex.keySet()) {
            Attribute att = data.attribute(category);
            A left = leftIndex.get(category);
            A right = rightIndex.get(category);
            if (supportNumeric && left.isNumeric()) {
                instance.setValue(att.index(), left.asDouble() - right.asDouble());
            } else {
                instance.setValue(att.index(), createPair(left, right)); //TODO: use data.attribute(att.index()).indexOfValue(_)??
            }
        }

        // Give instance access to attribute information from the dataset.
        instance.setDataset(data);

        return instance;
    }
}
