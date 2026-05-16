package com.aircraft.pdm.alert;

import com.aircraft.pdm.anomaly.Anomaly;
import com.aircraft.pdm.model.SensorSnapshot;
import com.aircraft.pdm.predict.FailurePrediction;

import java.util.ArrayList;
import java.util.List;

public class AlertManager {
    public List<Alert> generateAlerts(SensorSnapshot snapshot, List<Anomaly> anomalies, FailurePrediction prediction) {
        List<Alert> alerts = new ArrayList<>();

        if (!anomalies.isEmpty()) {
            alerts.add(new Alert(
                AlertSeverity.WARNING,
                "Anomalous behavior detected in monitored parameters",
                "Run focused inspection on engine thermal and vibration subsystems"
            ));
        }

        if (prediction.failureLikelihood() >= 0.75) {
            alerts.add(new Alert(
                AlertSeverity.CRITICAL,
                "High probability of near-term failure: " + prediction.predictedFailureType(),
                "Schedule immediate maintenance; limit engine load until inspected"
            ));
        }

        if (snapshot.pressureKpa() < 920 && snapshot.fuelFlowKgPerSec() > 3.0) {
            alerts.add(new Alert(
                AlertSeverity.WARNING,
                "Potential fuel inefficiency condition detected",
                "Inspect fuel injectors and pressure regulation path"
            ));
        }

        return alerts;
    }
}
