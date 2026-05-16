package com.aircraft.pdm.app;

import com.aircraft.pdm.alert.Alert;
import com.aircraft.pdm.alert.AlertManager;
import com.aircraft.pdm.anomaly.Anomaly;
import com.aircraft.pdm.anomaly.AnomalyDetector;
import com.aircraft.pdm.data.CsvDataIngestionModule;
import com.aircraft.pdm.data.DataIngestionModule;
import com.aircraft.pdm.data.RealTimeSensorSimulator;
import com.aircraft.pdm.model.SensorSnapshot;
import com.aircraft.pdm.monitor.MonitoringEngine;
import com.aircraft.pdm.predict.FailurePrediction;
import com.aircraft.pdm.predict.FailurePredictionEngine;
import com.aircraft.pdm.preprocess.PreprocessingUnit;
import com.aircraft.pdm.report.ReportGenerator;
import com.aircraft.pdm.storage.HistoricalDataStore;

import java.util.List;

public class PredictiveMaintenanceEngine {
    private final DataIngestionModule ingestion;
    private final PreprocessingUnit preprocessing = new PreprocessingUnit();
    private final MonitoringEngine monitoring = new MonitoringEngine(120);
    private final AnomalyDetector anomalyDetector = new AnomalyDetector();
    private final FailurePredictionEngine predictionEngine = new FailurePredictionEngine();
    private final AlertManager alertManager = new AlertManager();
    private final HistoricalDataStore dataStore = new HistoricalDataStore();
    private final ReportGenerator reportGenerator = new ReportGenerator();
    private int cycle;

    public PredictiveMaintenanceEngine(String[] args) {
        this.ingestion = createIngestion(args);
    }

    public EngineCycleResult nextCycle() {
        cycle++;
        SensorSnapshot raw = ingestion.nextSnapshot();
        SensorSnapshot clean = preprocessing.cleanAndNormalize(raw);
        monitoring.accept(clean);
        List<Anomaly> anomalies = anomalyDetector.detect(clean, monitoring);
        FailurePrediction prediction = predictionEngine.predict(clean, anomalies, monitoring);
        List<Alert> alerts = alertManager.generateAlerts(clean, anomalies, prediction);
        dataStore.log(clean, anomalies, prediction, alerts);
        return new EngineCycleResult(cycle, clean, anomalies, prediction, alerts);
    }

    public String generateReport() {
        return reportGenerator.generate(dataStore);
    }

    private DataIngestionModule createIngestion(String[] args) {
        if (args.length >= 2 && "--file".equals(args[0])) {
            return new CsvDataIngestionModule(args[1]);
        }
        return new RealTimeSensorSimulator();
    }
}
