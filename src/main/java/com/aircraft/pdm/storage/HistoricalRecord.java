package com.aircraft.pdm.storage;

import com.aircraft.pdm.alert.Alert;
import com.aircraft.pdm.anomaly.Anomaly;
import com.aircraft.pdm.model.SensorSnapshot;
import com.aircraft.pdm.predict.FailurePrediction;

import java.util.List;

public record HistoricalRecord(
    SensorSnapshot snapshot,
    List<Anomaly> anomalies,
    FailurePrediction prediction,
    List<Alert> alerts
) {
}
