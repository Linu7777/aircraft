package com.aircraft.pdm.report;

import com.aircraft.pdm.alert.AlertSeverity;
import com.aircraft.pdm.storage.HistoricalDataStore;
import com.aircraft.pdm.storage.HistoricalRecord;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {
    public String generate(HistoricalDataStore dataStore) {
        var records = dataStore.records();
        if (records.isEmpty()) {
            return "No records captured.";
        }

        DoubleSummaryStatistics tempStats = records.stream().mapToDouble(r -> r.snapshot().engineTempC()).summaryStatistics();
        DoubleSummaryStatistics vibStats = records.stream().mapToDouble(r -> r.snapshot().vibrationG()).summaryStatistics();

        long anomalyCount = records.stream().mapToLong(r -> r.anomalies().size()).sum();
        long warningCount = records.stream()
            .flatMap(r -> r.alerts().stream())
            .filter(a -> a.severity() == AlertSeverity.WARNING)
            .count();
        long criticalCount = records.stream()
            .flatMap(r -> r.alerts().stream())
            .filter(a -> a.severity() == AlertSeverity.CRITICAL)
            .count();

        Map<String, Long> predictionTypes = records.stream()
            .collect(Collectors.groupingBy(r -> r.prediction().predictedFailureType(), Collectors.counting()));

        HistoricalRecord maxRisk = records.stream()
            .max(Comparator.comparingDouble(r -> r.prediction().failureLikelihood()))
            .orElse(records.get(0));

        return String.format("""
            Total samples: %d
            Temperature: avg=%.2fC min=%.2fC max=%.2fC
            Vibration: avg=%.2fg min=%.2fg max=%.2fg
            Total anomalies detected: %d
            Alerts: warnings=%d critical=%d
            Dominant predictions: %s
            Peak risk event: %.0f%% (%s)
            Maintenance recommendation: %s
            """,
            records.size(),
            tempStats.getAverage(), tempStats.getMin(), tempStats.getMax(),
            vibStats.getAverage(), vibStats.getMin(), vibStats.getMax(),
            anomalyCount,
            warningCount, criticalCount,
            predictionTypes,
            maxRisk.prediction().failureLikelihood() * 100,
            maxRisk.prediction().predictedFailureType(),
            recommendationForRisk(maxRisk.prediction().failureLikelihood())
        );
    }

    private String recommendationForRisk(double risk) {
        if (risk > 0.85) {
            return "Immediate engine subsystem inspection before next operation window.";
        }
        if (risk > 0.60) {
            return "Schedule maintenance within next service interval and increase monitoring frequency.";
        }
        return "Continue operations with standard preventive checks.";
    }
}
