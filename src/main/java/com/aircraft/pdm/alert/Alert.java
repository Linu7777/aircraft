package com.aircraft.pdm.alert;

public record Alert(AlertSeverity severity, String message, String recommendedAction) {
}
