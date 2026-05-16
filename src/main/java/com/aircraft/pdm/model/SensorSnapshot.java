package com.aircraft.pdm.model;

import java.time.Instant;

public record SensorSnapshot(
    Instant timestamp,
    double engineTempC,
    double fuelFlowKgPerSec,
    double vibrationG,
    double pressureKpa,
    double rpm
) {
}
