package com.aircraft.pdm.storage;

import com.aircraft.pdm.alert.Alert;
import com.aircraft.pdm.anomaly.Anomaly;
import com.aircraft.pdm.model.SensorSnapshot;
import com.aircraft.pdm.predict.FailurePrediction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoricalDataStore {
    private final List<HistoricalRecord> records = new ArrayList<>();

    public void log(SensorSnapshot snapshot, List<Anomaly> anomalies, FailurePrediction prediction, List<Alert> alerts) {
        records.add(new HistoricalRecord(snapshot, anomalies, prediction, alerts));
    }

    public List<HistoricalRecord> records() {
        return Collections.unmodifiableList(records);
    }
}
