package com.coldchain.tracker.service.impl;

import com.coldchain.tracker.dto.request.TemperatureLogRequest;
import com.coldchain.tracker.dto.response.TemperatureLogResponse;
import com.coldchain.tracker.entity.Shipment;
import com.coldchain.tracker.entity.TemperatureLog;
import com.coldchain.tracker.entity.enums.AlertSeverity;
import com.coldchain.tracker.exception.InvalidTemperatureException;
import com.coldchain.tracker.exception.ResourceNotFoundException;
import com.coldchain.tracker.repository.ShipmentRepository;
import com.coldchain.tracker.repository.TemperatureLogRepository;
import com.coldchain.tracker.service.AlertService;
import com.coldchain.tracker.service.TemperatureMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TemperatureMonitoringService evaluating compliance against 2°C - 8°C cold-chain standards.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TemperatureMonitoringServiceImpl implements TemperatureMonitoringService {

    private final TemperatureLogRepository temperatureLogRepository;
    private final ShipmentRepository shipmentRepository;
    private final AlertService alertService;

    @Value("${coldchain.temperature.min-sensor-limit:-50.0}")
    private double minSensorLimit;

    @Value("${coldchain.temperature.max-sensor-limit:60.0}")
    private double maxSensorLimit;

    @Override
    @Transactional
    public TemperatureLogResponse recordTemperatureLog(Long shipmentId, TemperatureLogRequest request) {
        log.info("Recording temperature log for shipment ID {}: {}°C", shipmentId, request.getTemperature());

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", shipmentId));

        Double temp = request.getTemperature();

        // 1. Sanity check: physical sensor limits (-50°C to +60°C)
        if (temp < minSensorLimit || temp > maxSensorLimit) {
            log.error("Corrupted telemetry reading: {}°C for shipment ID {}", temp, shipmentId);
            throw new InvalidTemperatureException(temp, minSensorLimit, maxSensorLimit);
        }

        // 2. Safe cold-chain threshold check (Default safe range: 2.0°C to 8.0°C)
        double minSafe = shipment.getMinTempThreshold() != null ? shipment.getMinTempThreshold() : 2.0;
        double maxSafe = shipment.getMaxTempThreshold() != null ? shipment.getMaxTempThreshold() : 8.0;

        boolean isViolation = (temp < minSafe || temp > maxSafe);

        LocalDateTime logTime = request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now();

        TemperatureLog logEntity = TemperatureLog.builder()
                .shipment(shipment)
                .temperature(temp)
                .humidity(request.getHumidity())
                .locationCoordinates(request.getLocationCoordinates())
                .timestamp(logTime)
                .isViolation(isViolation)
                .build();

        TemperatureLog savedLog = temperatureLogRepository.save(logEntity);

        // 3. Automated Alert Triggering if Violation Occurred
        if (isViolation) {
            String breachType;
            AlertSeverity severity;
            String message;

            if (temp > maxSafe) {
                breachType = "UPPER_LIMIT_EXCEEDED";
                double delta = temp - maxSafe;
                severity = (delta > 5.0) ? AlertSeverity.CRITICAL : AlertSeverity.HIGH;
                message = String.format(
                        "AUTOMATED ALERT: High Temperature Breach! Recorded %.2f°C exceeds maximum safe threshold (%.2f°C) by %.2f°C for shipment '%s' (Tracking: %s).",
                        temp, maxSafe, delta, shipment.getProductName(), shipment.getTrackingNumber()
                );
            } else {
                breachType = "LOWER_LIMIT_EXCEEDED";
                double delta = minSafe - temp;
                severity = (delta > 5.0) ? AlertSeverity.CRITICAL : AlertSeverity.HIGH;
                message = String.format(
                        "AUTOMATED ALERT: Sub-Zero/Freezing Temperature Breach! Recorded %.2f°C dropped below minimum safe threshold (%.2f°C) by %.2f°C for shipment '%s' (Tracking: %s). Product may suffer freeze damage.",
                        temp, minSafe, delta, shipment.getProductName(), shipment.getTrackingNumber()
                );
            }

            log.warn("Triggering automated alert for shipment {}: {}", shipment.getTrackingNumber(), message);
            alertService.createAlertForViolation(shipment, savedLog, temp, breachType, severity, message);
        } else {
            log.info("Temperature reading compliant: {}°C within safe window [{}°C - {}°C]", temp, minSafe, maxSafe);
        }

        return mapToTemperatureLogResponse(savedLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemperatureLogResponse> getLogsByShipmentId(Long shipmentId) {
        if (!shipmentRepository.existsById(shipmentId)) {
            throw new ResourceNotFoundException("Shipment", "id", shipmentId);
        }
        return temperatureLogRepository.findByShipmentIdOrderByTimestampDesc(shipmentId).stream()
                .map(this::mapToTemperatureLogResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TemperatureLogResponse getLatestLog(Long shipmentId) {
        if (!shipmentRepository.existsById(shipmentId)) {
            throw new ResourceNotFoundException("Shipment", "id", shipmentId);
        }
        TemperatureLog latestLog = temperatureLogRepository.findTopByShipmentIdOrderByTimestampDesc(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No temperature logs found for shipment id: " + shipmentId));

        return mapToTemperatureLogResponse(latestLog);
    }

    public TemperatureLogResponse mapToTemperatureLogResponse(TemperatureLog log) {
        if (log == null) return null;
        return TemperatureLogResponse.builder()
                .id(log.getId())
                .shipmentId(log.getShipment().getId())
                .trackingNumber(log.getShipment().getTrackingNumber())
                .temperature(log.getTemperature())
                .humidity(log.getHumidity())
                .locationCoordinates(log.getLocationCoordinates())
                .timestamp(log.getTimestamp())
                .isViolation(log.isViolation())
                .complianceStatus(log.isViolation() ? "BREACH_DETECTED" : "COMPLIANT")
                .build();
    }
}
