package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AlertService {

  private final AlertRepository alertRepository;
  private final SensorRepository sensorRepository;
  private final KafkaTemplate<String, AlertDto> kafkaTemplate;
  private static final String ALERT_TOPIC = "alerts";

  public AlertService(AlertRepository alertRepository, SensorRepository sensorRepository,
      KafkaTemplate<String, AlertDto> kafkaTemplate) {
    this.alertRepository = alertRepository;
    this.sensorRepository = sensorRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  public Alert saveAlert(AlertDto alertDto) {
    var sensor = sensorRepository.findById(alertDto.sensorId())
            .orElseThrow(); //should throw a custom exception and handle differently for HTTP and kafka

    var alert = alertRepository.save(new Alert(alertDto.message(), alertDto.timestamp(), sensor));

    //Depending on what consumes this topic and how critical these alerts are, it might be wise to add an
    //outbox pattern here, so alerts are not lost even if the app crashes or connection to the kafka cluster is lost
    kafkaTemplate.send(ALERT_TOPIC, alertDto);

    return alert;
  }

  public AlertDto findLastAlertBySensorId(UUID sensorId) {
    //should create and use a custom exception, if this service is later not called by an HTTP endpoint
    var sensor = sensorRepository.findById(sensorId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    var alert = alertRepository.findTopBySensorOrderByTimestampDesc(sensor)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    return new AlertDto(sensor.getId(), alert.getMessage(), alert.getTimestamp());
  }
}
