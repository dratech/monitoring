package com.aldisued.iot.monitoring.service;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeasurementCalculatorService {

    public List<Double> filterByAverageDeviation(List<Double> values, Double deviation) {
        if (deviation < 0 || deviation > 1.0) {
            throw new IllegalArgumentException();
        }

        var avg = values.stream().collect(Collectors.averagingDouble(Double::doubleValue));
        var min = avg * (1 - deviation);
        var max = avg * (1 + deviation);

        return values.stream()
                .filter(val -> val < max && val > min)
                .toList();
    }

  public List<Double> getMovingAverage(List<Double> data, int windowSize) {
    // TODO: Task 10
    return List.of();
  }

}
