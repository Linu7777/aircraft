package com.aircraft.pdm.preprocess;

import com.aircraft.pdm.model.SensorSnapshot;

public class PreprocessingUnit {
    public SensorSnapshot cleanAndNormalize(SensorSnapshot raw) {
        double temp = clamp(raw.engineTempC(), 300, 1200);
        double fuel = clamp(raw.fuelFlowKgPerSec(), 0.5, 10);
        double vibration = clamp(raw.vibrationG(), 0, 8);
        double pressure = clamp(raw.pressureKpa(), 700, 1300);
        double rpm = clamp(raw.rpm(), 1000, 18000);

        return new SensorSnapshot(raw.timestamp(), temp, fuel, vibration, pressure, rpm);
    }

    private double clamp(double value, double min, double max) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }
}
