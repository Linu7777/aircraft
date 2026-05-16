package com.aircraft.pdm.data;

import com.aircraft.pdm.model.SensorSnapshot;

import java.time.Instant;
import java.util.Random;

public class RealTimeSensorSimulator implements DataIngestionModule {
    private final Random random = new Random();
    private int tick = 0;

    @Override
    public SensorSnapshot nextSnapshot() {
        tick++;
        double drift = tick * 0.05;

        double temp = 650 + randomNoise(4) + drift;
        double fuel = 2.8 + randomNoise(0.08);
        double vibration = 1.2 + randomNoise(0.1) + drift * 0.01;
        double pressure = 980 + randomNoise(8) - drift * 0.2;
        double rpm = 8600 + randomNoise(70);

        if (tick % 18 == 0) {
            temp += 35;
            vibration += 0.9;
        }
        if (tick % 27 == 0) {
            pressure -= 55;
            fuel += 0.25;
        }

        return new SensorSnapshot(Instant.now(), temp, fuel, vibration, pressure, rpm);
    }

    private double randomNoise(double magnitude) {
        return (random.nextDouble() - 0.5) * 2 * magnitude;
    }
}
