package com.aircraft.pdm.anomaly;

public record Anomaly(AnomalyType type, String description, double score) {
    @Override
    public String toString() {
        return type + ": " + description;
    }
}
