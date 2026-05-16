package com.aircraft.pdm.monitor;

import com.aircraft.pdm.model.SensorSnapshot;

import java.util.ArrayDeque;
import java.util.Deque;

public class MonitoringEngine {
    private final Deque<SensorSnapshot> window = new ArrayDeque<>();
    private final int maxWindowSize;

    public MonitoringEngine(int maxWindowSize) {
        this.maxWindowSize = maxWindowSize;
    }

    public void accept(SensorSnapshot snapshot) {
        window.addLast(snapshot);
        if (window.size() > maxWindowSize) {
            window.removeFirst();
        }
    }

    public Deque<SensorSnapshot> window() {
        return window;
    }

    public SensorSnapshot previous() {
        if (window.size() < 2) {
            return null;
        }
        return window.stream().skip(window.size() - 2).findFirst().orElse(null);
    }

    public double movingAvgTemp(int n) {
        return window.stream().skip(Math.max(0, window.size() - n)).mapToDouble(SensorSnapshot::engineTempC).average().orElse(0);
    }

    public double movingAvgVibration(int n) {
        return window.stream().skip(Math.max(0, window.size() - n)).mapToDouble(SensorSnapshot::vibrationG).average().orElse(0);
    }
}
