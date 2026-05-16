package com.aircraft.pdm.app;

import com.aircraft.pdm.alert.Alert;
import com.aircraft.pdm.anomaly.Anomaly;
import com.aircraft.pdm.model.SensorSnapshot;
import com.aircraft.pdm.predict.FailurePrediction;

import java.util.List;

public record EngineCycleResult(
    int cycle,
    SensorSnapshot snapshot,
    List<Anomaly> anomalies,
    FailurePrediction prediction,
    List<Alert> alerts
) {
}
