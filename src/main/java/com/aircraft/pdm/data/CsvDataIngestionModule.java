package com.aircraft.pdm.data;

import com.aircraft.pdm.model.SensorSnapshot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CsvDataIngestionModule implements DataIngestionModule {
    private final List<SensorSnapshot> snapshots;
    private int index = 0;

    public CsvDataIngestionModule(String filePath) {
        this.snapshots = readSnapshots(filePath);
        if (snapshots.isEmpty()) {
            throw new IllegalArgumentException("No valid rows found in input file: " + filePath);
        }
    }

    @Override
    public SensorSnapshot nextSnapshot() {
        SensorSnapshot sample = snapshots.get(index % snapshots.size());
        index++;
        return new SensorSnapshot(
            Instant.now(),
            sample.engineTempC(),
            sample.fuelFlowKgPerSec(),
            sample.vibrationG(),
            sample.pressureKpa(),
            sample.rpm()
        );
    }

    private List<SensorSnapshot> readSnapshots(String filePath) {
        List<SensorSnapshot> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.toLowerCase().contains("temp")) {
                    continue;
                }
                String[] p = line.split(",");
                if (p.length < 5) {
                    continue;
                }
                try {
                    double temp = Double.parseDouble(p[0].trim());
                    double fuel = Double.parseDouble(p[1].trim());
                    double vib = Double.parseDouble(p[2].trim());
                    double pressure = Double.parseDouble(p[3].trim());
                    double rpm = Double.parseDouble(p[4].trim());
                    list.add(new SensorSnapshot(Instant.now(), temp, fuel, vib, pressure, rpm));
                } catch (NumberFormatException ignored) {
                    // Skip malformed lines.
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read dataset file: " + filePath, e);
        }
        return list;
    }
}
