package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SensorService {

  private final SensorRepository sensorRepository;

  public SensorService(SensorRepository sensorRepository) {
    this.sensorRepository = sensorRepository;
  }

  public Sensor saveSensor(SensorDto sensor) {

    //its race condition, but the with the added unique constraint it should protect data integrity
    //could check constraint validation, but this is a bit cleaner
    if (sensorRepository.existsByName(sensor.name())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "sensor name must be unique");
    }

    return sensorRepository.save(new Sensor(
        sensor.name(),
        sensor.type()
    ));
  }
}
