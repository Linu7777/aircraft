package com.aircraft.pdm.predict;

public record FailurePrediction(
    double failureLikelihood,
    String predictedFailureType,
    String timeframeHint
) {
}
