package com.aircraft.pdm.ui;

import com.aircraft.pdm.alert.Alert;
import com.aircraft.pdm.anomaly.Anomaly;
import com.aircraft.pdm.app.EngineCycleResult;
import com.aircraft.pdm.app.PredictiveMaintenanceEngine;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

public class AppFrame extends JFrame {
    private final PredictiveMaintenanceEngine engine;
    private final JLabel cycleValue = new JLabel("-", SwingConstants.CENTER);
    private final JLabel tempValue = new JLabel("-", SwingConstants.CENTER);
    private final JLabel vibValue = new JLabel("-", SwingConstants.CENTER);
    private final JLabel pressureValue = new JLabel("-", SwingConstants.CENTER);
    private final JLabel rpmValue = new JLabel("-", SwingConstants.CENTER);
    private final JLabel predictionValue = new JLabel("No data", SwingConstants.LEFT);
    private final JProgressBar riskBar = new JProgressBar(0, 100);
    private final DefaultTableModel anomalyTableModel = new DefaultTableModel(new String[]{"Type", "Description", "Score"}, 0);
    private final DefaultTableModel alertTableModel = new DefaultTableModel(new String[]{"Severity", "Message", "Recommended Action"}, 0);
    private final TrendChartPanel trendChart = new TrendChartPanel();
    private final JTextArea reportArea = new JTextArea();
    private final Timer timer;

    public AppFrame(String[] args) {
        this.engine = new PredictiveMaintenanceEngine(args);
        setTitle("Aircraft Predictive Maintenance Control Center");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        timer = new Timer(800, e -> updateCycle());
        timer.start();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new GridLayout(1, 6, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        panel.add(metricCard("Cycle", cycleValue));
        panel.add(metricCard("Engine Temp (C)", tempValue));
        panel.add(metricCard("Vibration (g)", vibValue));
        panel.add(metricCard("Pressure (kPa)", pressureValue));
        panel.add(metricCard("RPM", rpmValue));
        panel.add(metricCard("Failure Risk", riskBar));
        return panel;
    }

    private JPanel buildMainContent() {
        JPanel container = new JPanel(new BorderLayout(12, 12));
        container.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        JTable anomalyTable = new JTable(anomalyTableModel);
        JTable alertTable = new JTable(alertTableModel);
        JScrollPane anomaliesPane = new JScrollPane(anomalyTable);
        anomaliesPane.setBorder(BorderFactory.createTitledBorder("Detected Anomalies"));
        JScrollPane alertsPane = new JScrollPane(alertTable);
        alertsPane.setBorder(BorderFactory.createTitledBorder("Active Alerts"));

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, anomaliesPane, alertsPane);
        topSplit.setResizeWeight(0.45);

        JPanel livePanel = new JPanel(new BorderLayout(8, 8));
        livePanel.setBorder(BorderFactory.createTitledBorder("Live Monitoring"));
        predictionValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        livePanel.add(predictionValue, BorderLayout.NORTH);
        livePanel.add(trendChart, BorderLayout.CENTER);

        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane reportPane = new JScrollPane(reportArea);
        reportPane.setBorder(BorderFactory.createTitledBorder("Analytical Report Snapshot"));

        JSplitPane lowerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, livePanel, reportPane);
        lowerSplit.setResizeWeight(0.55);

        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, lowerSplit);
        verticalSplit.setResizeWeight(0.42);
        container.add(verticalSplit, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        JButton refreshReport = new JButton("Refresh Full Report");
        refreshReport.addActionListener(e -> reportArea.setText(engine.generateReport()));
        panel.add(refreshReport, BorderLayout.EAST);
        return panel;
    }

    private JPanel metricCard(String label, JLabel value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(214, 220, 230)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JLabel title = new JLabel(label, SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        value.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        card.add(title, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel metricCard(String label, JProgressBar progressBar) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(214, 220, 230)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JLabel title = new JLabel(label, SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        progressBar.setStringPainted(true);
        card.add(title, BorderLayout.NORTH);
        card.add(progressBar, BorderLayout.CENTER);
        return card;
    }

    private void updateCycle() {
        EngineCycleResult result = engine.nextCycle();
        cycleValue.setText(String.valueOf(result.cycle()));
        tempValue.setText(String.format("%.2f", result.snapshot().engineTempC()));
        vibValue.setText(String.format("%.2f", result.snapshot().vibrationG()));
        pressureValue.setText(String.format("%.2f", result.snapshot().pressureKpa()));
        rpmValue.setText(String.format("%.0f", result.snapshot().rpm()));

        int riskPercent = (int) Math.round(result.prediction().failureLikelihood() * 100);
        riskBar.setValue(riskPercent);
        riskBar.setString(riskPercent + "%");
        riskBar.setForeground(riskPercent >= 75 ? new Color(198, 40, 40) : riskPercent >= 45 ? new Color(245, 124, 0) : new Color(46, 125, 50));
        predictionValue.setText("Prediction: " + result.prediction().predictedFailureType() + " | " + result.prediction().timeframeHint());

        trendChart.append(result.snapshot().engineTempC(), result.snapshot().vibrationG());
        loadAnomalies(result);
        loadAlerts(result);
        if (result.cycle() % 5 == 0) {
            reportArea.setText(engine.generateReport());
        }
    }

    private void loadAnomalies(EngineCycleResult result) {
        anomalyTableModel.setRowCount(0);
        for (Anomaly anomaly : result.anomalies()) {
            anomalyTableModel.addRow(new Object[]{anomaly.type(), anomaly.description(), String.format("%.2f", anomaly.score())});
        }
    }

    private void loadAlerts(EngineCycleResult result) {
        alertTableModel.setRowCount(0);
        for (Alert alert : result.alerts()) {
            alertTableModel.addRow(new Object[]{alert.severity(), alert.message(), alert.recommendedAction()});
        }
    }
}
