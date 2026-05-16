package com.aircraft.pdm.predict;

import com.aircraft.pdm.anomaly.Anomaly;
import com.aircraft.pdm.anomaly.AnomalyType;
import com.aircraft.pdm.model.SensorSnapshot;
import com.aircraft.pdm.monitor.MonitoringEngine;

import java.util.List;

public class FailurePredictionEngine {
    public FailurePrediction predict(SensorSnapshot snapshot, List<Anomaly> anomalies, MonitoringEngine monitoring) {
        double baseRisk = anomalies.stream().mapToDouble(Anomaly::score).average().orElse(0.08);
        double trendBoost = 0.0;

        if (monitoring.window().size() > 15) {
            double shortTemp = monitoring.movingAvgTemp(5);
            double longTemp = monitoring.movingAvgTemp(15);
            if (shortTemp > longTemp + 8) {
                trendBoost += 0.10;
            }
        }

        double risk = Math.min(0.99, baseRisk + trendBoost);
        String failureType = classify(snapshot, anomalies);
        String timeframe = timeframe(risk);
        return new FailurePrediction(risk, failureType, timeframe);
    }

    private String classify(SensorSnapshot s, List<Anomaly> anomalies) {
        boolean correlated = anomalies.stream().anyMatch(a -> a.type() == AnomalyType.CORRELATED_RISK);
        if (correlated && s.engineTempC() > 720) {
            return "Potential engine overheating";
        }
        if (s.vibrationG() > 2.2) {
            return "Mechanical wear or imbalance";
        }
        if (s.pressureKpa() < 920 && s.fuelFlowKgPerSec() > 3.0) {
            return "Fuel inefficiency";
        }
        return "No imminent failure classified";
    }

    private String timeframe(double risk) {
        if (risk > 0.85) {
            return "Failure likely within 30-60 minutes of similar conditions";
        }
        if (risk > 0.60) {
            return "Failure risk elevated in the next few flight cycles";
        }
        if (risk > 0.35) {
            return "Monitor closely; potential degradation trend";
        }
        return "Low near-term failure likelihood";
    }
}
