# Aircraft Predictive Maintenance and Fault Detection (Java)

Professional desktop app that ingests aircraft sensor data, detects anomalies, predicts failures, generates alerts, and produces analytical reports in real time.

## Features
- Desktop GUI control center (live metrics, trend chart, anomalies table, alerts table, report pane)
- Continuous streaming monitoring
- Data preprocessing (cleaning + normalization)
- Threshold-based, trend-based, sudden-spike, and correlated anomaly detection
- Failure likelihood prediction and fault classification
- Warning/Critical alerts with maintenance recommendations
- Historical logging and final analytical report
- Two ingestion modes:
  - Simulated real-time stream
  - CSV dataset input

## Project Structure
- `src/main/java/com/aircraft/pdm/data` - ingestion modules
- `src/main/java/com/aircraft/pdm/preprocess` - preprocessing
- `src/main/java/com/aircraft/pdm/monitor` - monitoring window and trends
- `src/main/java/com/aircraft/pdm/anomaly` - anomaly detection
- `src/main/java/com/aircraft/pdm/predict` - failure prediction
- `src/main/java/com/aircraft/pdm/alert` - alert management
- `src/main/java/com/aircraft/pdm/storage` - historical logging
- `src/main/java/com/aircraft/pdm/report` - analytical reporting
- `src/main/resources/sample-sensor-data.csv` - sample dataset

## Run
If Maven is available:

```bash
mvn compile exec:java
```

Run with file input:

```bash
mvn compile exec:java -Dexec.args="--file src/main/resources/sample-sensor-data.csv"
```

If Maven is not installed, compile with JDK:

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.aircraft.pdm.Main
```

On startup, the app opens a window titled `Aircraft Predictive Maintenance Control Center`.
