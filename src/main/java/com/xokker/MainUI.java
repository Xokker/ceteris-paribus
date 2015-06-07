package com.xokker;

import com.google.common.util.concurrent.*;
import com.xokker.datasets.*;
import com.xokker.predictor.impl.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;

/**
 * @author Ernest Sadykov
 * @since 08.06.2015
 */
public class MainUI {

    private static final Logger logger = LoggerFactory.getLogger(MainUI.class);

    private JPanel panel1;
    private JComboBox datasetCombobox;
    private JComboBox algorithmComboBox;
    private JButton calculateButton;
    private JSpinner spinner1;
    private JTextArea resultsTextArea;
    private JCheckBox twoOut;
    private JButton cancelButton;

    private String selectedDataset;
    private String selectedAlgorithm;

    private ListeningExecutorService executor;
    private ListenableFuture<Map<String, DescriptiveStatistics>> future = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainUI");
        frame.setContentPane(new MainUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void init() {
        spinner1.setModel(new SpinnerNumberModel(10, 10, 5000, 1));
        carsSelected();
        this.selectedDataset = datasetCombobox.getSelectedItem().toString();
        this.selectedAlgorithm = algorithmComboBox.getSelectedItem().toString();
        resultsTextArea.setEditable(false);
        cancelButton.setEnabled(false);

        this.executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    }

    public MainUI() {
        init();
        calculateButton.addActionListener(e -> buttonClicked());
        datasetCombobox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.selectedDataset = datasetCombobox.getSelectedItem().toString();
                String newItem = e.getItem().toString();
                switch (newItem) {
                    case "Cars":
                        carsSelected();
                        break;
                    case "Sushi":
                        sushiSelected();
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
        });
        algorithmComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.selectedAlgorithm = algorithmComboBox.getSelectedItem().toString();
                this.selectedAlgorithm = e.getItem().toString();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (future != null && !future.isDone()) {
                    future.cancel(true);
                }
            }
        });
    }

    private boolean isTwoOut() {
        return twoOut.isSelected();
    }

    private void sushiSelected() {
        spinner1.setEnabled(true);
        String[] sushiAlgorithms = new String[] {
                "CP with support, num",
                "C4.5 paired, num",
                "Naive Bayes",
                "Bayes Net"};
        algorithmComboBox.setModel(new DefaultComboBoxModel<>(sushiAlgorithms));
    }

    private void carsSelected() {
        String[] carsAlgorithms = new String[] {
                "ceteris paribus (CP)",
                "CP with support",
                "CP with fixed DFE",
                "CP with support, num",
                "C4.5 unpaired",
                "C4.5 paired",
                "C4.5 paired, num",
                "Naive Bayes",
                "Bayes Net"};
        algorithmComboBox.setModel(new DefaultComboBoxModel<>(carsAlgorithms));
        spinner1.setEnabled(false);
        spinner1.setValue(10);
    }

    private void buttonClicked() {
        calculateButton.setEnabled(false);
        printResult("выполняются вычисления. \nэто может занять несколько минут...");

        switch (selectedAlgorithm) {
            case "ceteris paribus (CP)": {
                AbstractExperiment<? extends Attribute> exp = exp2();
                future = executor.submit(() -> exp.perform(getDataset(), CeterisParibusPredictor::new));
                break;
            }
            case "CP with support": {
                AbstractExperiment<? extends Attribute> exp = exp2();
                future = executor.submit(() -> exp.perform(getDataset(), (context) -> new CeterisParibusPredictor<>(context, true)));
                break;
            }
            case "CP with fixed DFE": {
                AbstractExperiment<? extends Attribute> exp3 = exp3();
                future = executor.submit(() -> exp3.perform(getDataset(), CeterisParibusPredictor::new));
                break;
            }
            case "CP with support, num": {
                AbstractExperiment<? extends Attribute> exp2 = exp2();
                future = executor.submit(() -> exp2.perform(getDataset(), (context) -> new CeterisParibusPredicatesPredictor<>(context, true)));
                break;
            }
            case "C4.5 unpaired":{
                AbstractExperiment<? extends Attribute> exp2 = exp2();
                future = executor.submit(() -> exp2.perform(getDataset(), J48Predictor::new));
                break;
            }
            case "C4.5 paired":   {
                AbstractExperiment<? extends Attribute> exp2 = exp2();
                future = executor.submit(() -> exp2.perform(getDataset(), (context) -> new J48Predictor<>(context, true, false)));
                break;
            }
            case "C4.5 paired, num":  {
                AbstractExperiment<? extends Attribute> exp2 = exp2();
                future = executor.submit(() -> exp2.perform(getDataset(), (context) -> new J48Predictor<>(context, true, true)));
                break;
            }
            case "Naive Bayes": {
                AbstractExperiment<? extends Attribute> exp2 = exp2();
                future = executor.submit(() -> exp2.perform(getDataset(), NaiveBayesPredictor::new));
                break;
            }
            case "Bayes Net": {
                AbstractExperiment<? extends Attribute> exp2 = exp2();
                future = executor.submit(() -> exp2.perform(getDataset(), BayesNetPredictor::new));
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }

        Futures.addCallback(future, new FutureCallback<Map<String, DescriptiveStatistics>>() {
            @Override
            public void onSuccess(Map<String, DescriptiveStatistics> result) {
                StringBuilder sb = new StringBuilder("Results:\n\n");
                sb.append("\nправильность:\n").append(result.get("accuracy").toString());
                sb.append("\nточность:\n").append(result.get("precision").toString());
                sb.append("\nполнота:\n").append(result.get("recall").toString());

                printResult(sb.toString());
                cancelButton.setEnabled(false);
                calculateButton.setEnabled(true);
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof CancellationException) {
                    printResult("выполнение прервано");
                } else {
                    printResult("Что-то пошло не так\nОшибка: " + t.getMessage());
                }
                cancelButton.setEnabled(false);
                calculateButton.setEnabled(true);
            }
        });
    }

    private AbstractExperiment<? extends Attribute> exp2() {
        Experiment2<? extends Attribute> exp2 = new Experiment2<>();
        if (isTwoOut()) {
            exp2.remove2Elements();
        }
        logClick();
        cancelButton.setEnabled(true);

        return exp2;
    }

    private AbstractExperiment<? extends Attribute> exp3() {
        Experiment3<? extends Attribute> exp3 = new Experiment3<>();
        if (isTwoOut()) {
            exp3.remove2Elements();
        }
        logClick();
        cancelButton.setEnabled(true);

        return exp3;
    }

    private void logClick() {
        logger.info("{}, {}-out", selectedAlgorithm, isTwoOut() ? "2" : "1");
    }

    private Datasets getDataset() {
        switch (selectedDataset) {
            case "Cars":
                return Datasets.Cars1;
            case "Sushi":
                return Datasets.SushiA;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void printResult(String result) {
        SwingUtilities.invokeLater(() -> resultsTextArea.setText(result));
    }
}
