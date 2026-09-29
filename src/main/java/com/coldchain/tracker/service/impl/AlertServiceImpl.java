package com.coldchain.tracker.service.impl;

import com.coldchain.tracker.dto.response.AlertResponse;
import com.coldchain.tracker.entity.Alert;
import com.coldchain.tracker.entity.Shipment;
import com.coldchain.tracker.entity.TemperatureLog;
import com.coldchain.tracker.entity.enums.AlertSeverity;
import com.coldchain.tracker.exception.ResourceNotFoundException;
import com.coldchain.tracker.repository.AlertRepository;
import com.coldchain.tracker.repository.ShipmentRepository;
import com.coldchain.tracker.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AlertService managing life cycle and resolution of compliance breaches.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final ShipmentRepository shipmentRepository;

    @Override
    @Transactional
    public Alert createAlertForViolation(
            Shipment shipment,
            TemperatureLog logEntity,
            Double temperature,
            String breachType,
            AlertSeverity severity,
            String message) {

        log.warn("Persisting violation alert for shipment: {}", shipment.getTrackingNumber());

        Alert alert = Alert.builder()
                .shipment(shipment)
                .temperatureLog(logEntity)
                .recordedTemperature(temperature)
                .breachType(breachType)
                .severity(severity)
                .message(message)
                .isResolved(false)
                .build();

        return alertRepository.save(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByShipmentId(Long shipmentId) {
        if (!shipmentRepository.existsById(shipmentId)) {
            throw new ResourceNotFoundException("Shipment", "id", shipmentId);
        }
        return alertRepository.findByShipmentIdOrderByCreatedAtDesc(shipmentId).stream()
                .map(this::mapToAlertResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToAlertResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlertResponse resolveAlert(Long alertId, String resolvedBy, String resolutionNotes) {
        log.info("Resolving alert ID {} by user: {}", alertId, resolvedBy);

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", alertId));

        alert.setResolved(true);
        alert.setResolvedBy(resolvedBy != null ? resolvedBy : "ADMIN");
        alert.setResolutionNotes(resolutionNotes != null ? resolutionNotes : "Temperature re-stabilized. Chiller reset performed.");
        alert.setResolvedAt(LocalDateTime.now());

        Alert updatedAlert = alertRepository.save(alert);
        return mapToAlertResponse(updatedAlert);
    }

    public AlertResponse mapToAlertResponse(Alert alert) {
        if (alert == null) return null;
        return AlertResponse.builder()
                .id(alert.getId())
                .shipmentId(alert.getShipment().getId())
                .trackingNumber(alert.getShipment().getTrackingNumber())
                .productName(alert.getShipment().getProductName())
                .temperatureLogId(alert.getTemperatureLog() != null ? alert.getTemperatureLog().getId() : null)
                .recordedTemperature(alert.getRecordedTemperature())
                .breachType(alert.getBreachType())
                .severity(alert.getSeverity())
                .message(alert.getMessage())
                .isResolved(alert.isResolved())
                .resolvedBy(alert.getResolvedBy())
                .resolutionNotes(alert.getResolutionNotes())
                .createdAt(alert.getCreatedAt())
                .resolvedAt(alert.getResolvedAt())
                .build();
    }
}
