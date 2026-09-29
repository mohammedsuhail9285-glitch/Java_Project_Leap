package com.coldchain.tracker.service;

import com.coldchain.tracker.dto.request.TemperatureLogRequest;
import com.coldchain.tracker.dto.response.TemperatureLogResponse;

import java.util.List;

/**
 * Service interface for logging sensor temperatures, enforcing safe compliance range (2°C - 8°C),
 * and automatically triggering alerts upon violation.
 */
public interface TemperatureMonitoringService {

    TemperatureLogResponse recordTemperatureLog(Long shipmentId, TemperatureLogRequest request);

    List<TemperatureLogResponse> getLogsByShipmentId(Long shipmentId);

    TemperatureLogResponse getLatestLog(Long shipmentId);
}
