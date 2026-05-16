package com.aircraft.pdm.anomaly;

import com.aircraft.pdm.model.SensorSnapshot;
import com.aircraft.pdm.monitor.MonitoringEngine;

import java.util.ArrayList;
import java.util.List;

public class AnomalyDetector {
    private static final double MAX_TEMP = 735;
    private static final double MAX_VIBRATION = 2.4;
    private static final double MIN_PRESSURE = 900;

    public List<Anomaly> detect(SensorSnapshot current, MonitoringEngine monitoring) {
        List<Anomaly> anomalies = new ArrayList<>();
        thresholdBased(current, anomalies);
        trendBased(current, monitoring, anomalies);
        suddenSpike(current, monitoring, anomalies);
        correlatedRisk(current, anomalies);
        return anomalies;
    }

    private void thresholdBased(SensorSnapshot s, List<Anomaly> anomalies) {
        if (s.engineTempC() > MAX_TEMP) {
            anomalies.add(new Anomaly(AnomalyType.THRESHOLD_BREACH, "Engine temperature above safe threshold", 0.85));
        }
        if (s.vibrationG() > MAX_VIBRATION) {
            anomalies.add(new Anomaly(AnomalyType.THRESHOLD_BREACH, "Vibration level above safe threshold", 0.80));
        }
        if (s.pressureKpa() < MIN_PRESSURE) {
            anomalies.add(new Anomaly(AnomalyType.THRESHOLD_BREACH, "Pressure below safe minimum", 0.82));
        }
    }

    private void trendBased(SensorSnapshot s, MonitoringEngine monitoring, List<Anomaly> anomalies) {
        if (monitoring.window().size() < 10) {
            return;
        }
        double avgTemp = monitoring.movingAvgTemp(10);
        double avgVib = monitoring.movingAvgVibration(10);
        if (s.engineTempC() - avgTemp > 18) {
            anomalies.add(new Anomaly(AnomalyType.TREND_ANOMALY, "Temperature trend rising compared with historical average", 0.72));
        }
        if (s.vibrationG() - avgVib > 0.45) {
            anomalies.add(new Anomaly(AnomalyType.TREND_ANOMALY, "Vibration trend increase detected", 0.68));
        }
    }

    private void suddenSpike(SensorSnapshot s, MonitoringEngine monitoring, List<Anomaly> anomalies) {
        SensorSnapshot prev = monitoring.previous();
        if (prev == null) {
            return;
        }
        if (s.engineTempC() - prev.engineTempC() > 20) {
            anomalies.add(new Anomaly(AnomalyType.SUDDEN_SPIKE, "Sudden temperature spike", 0.78));
        }
        if (s.vibrationG() - prev.vibrationG() > 0.6) {
            anomalies.add(new Anomaly(AnomalyType.SUDDEN_SPIKE, "Sudden vibration spike", 0.76));
        }
    }

    private void correlatedRisk(SensorSnapshot s, List<Anomaly> anomalies) {
        if (s.engineTempC() > 710 && s.vibrationG() > 2.1) {
            anomalies.add(new Anomaly(AnomalyType.CORRELATED_RISK, "High temperature with high vibration indicates potential mechanical wear", 0.92));
        }
        if (s.pressureKpa() < 920 && s.fuelFlowKgPerSec() > 3.0) {
            anomalies.add(new Anomaly(AnomalyType.CORRELATED_RISK, "Low pressure with high fuel flow indicates fuel inefficiency risk", 0.86));
        }
    }
}
