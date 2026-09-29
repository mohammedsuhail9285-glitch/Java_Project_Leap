package com.coldchain.tracker.service;

import com.coldchain.tracker.dto.response.AlertResponse;
import com.coldchain.tracker.entity.Alert;
import com.coldchain.tracker.entity.Shipment;
import com.coldchain.tracker.entity.TemperatureLog;
import com.coldchain.tracker.entity.enums.AlertSeverity;

import java.util.List;

/**
 * Service interface for triggering, dispatching, and resolving temperature breach alerts.
 */
public interface AlertService {

    Alert createAlertForViolation(
            Shipment shipment,
            TemperatureLog log,
            Double temperature,
            String breachType,
            AlertSeverity severity,
            String message
    );

    List<AlertResponse> getAlertsByShipmentId(Long shipmentId);

    List<AlertResponse> getAllAlerts();

    AlertResponse resolveAlert(Long alertId, String resolvedBy, String resolutionNotes);
}
