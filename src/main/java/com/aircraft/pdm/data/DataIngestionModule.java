package com.aircraft.pdm.data;

import com.aircraft.pdm.model.SensorSnapshot;

public interface DataIngestionModule {
    SensorSnapshot nextSnapshot();
}
