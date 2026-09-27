package com.aldisued.iot.monitoring.repository;

import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.entity.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SensorReadingRepository extends JpaRepository<SensorReading, String> {
    @Query("select avg(r.value) from SensorReading r where r.sensor.type = :type and r.timestamp between :from and :to")
    Optional<Double> calcAverage(SensorType type, LocalDateTime from, LocalDateTime to);
}
