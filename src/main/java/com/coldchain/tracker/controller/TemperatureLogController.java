package com.coldchain.tracker.controller;

import com.coldchain.tracker.dto.request.TemperatureLogRequest;
import com.coldchain.tracker.dto.response.TemperatureLogResponse;
import com.coldchain.tracker.service.TemperatureMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for IoT telemetry ingestion, real-time temperature auditing, and violation detection.
 */
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "5. Temperature Monitoring Module", description = "Endpoints for ingesting IoT sensor readings, checking 2°C - 8°C boundaries, and retrieving telemetry history")
public class TemperatureLogController {

    private final TemperatureMonitoringService temperatureMonitoringService;

    @PostMapping({"/api/v1/temperature-logs/{shipmentId}", "/temperature-log/{shipmentId}", "/api/v1/temperature-log/{shipmentId}"})
    @Operation(summary = "Record temperature log", description = "Ingests a sensor reading for a shipment. If outside [2°C - 8°C], automatically persists an alert.")
    public ResponseEntity<TemperatureLogResponse> recordTemperature(
            @PathVariable Long shipmentId,
            @Valid @RequestBody TemperatureLogRequest request) {
        TemperatureLogResponse response = temperatureMonitoringService.recordTemperatureLog(shipmentId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping({"/api/v1/temperature-logs/{shipmentId}", "/api/v1/temperature-log/{shipmentId}"})
    @Operation(summary = "Get all temperature logs for a shipment", description = "Fetches complete telemetry audit trail for the given shipment in reverse chronological order")
    public ResponseEntity<List<TemperatureLogResponse>> getLogsByShipment(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(temperatureMonitoringService.getLogsByShipmentId(shipmentId));
    }

    @GetMapping({"/api/v1/temperature-logs/{shipmentId}/latest", "/api/v1/temperature-log/{shipmentId}/latest"})
    @Operation(summary = "Get latest temperature log", description = "Fetches the most recently recorded temperature telemetry reading for a shipment")
    public ResponseEntity<TemperatureLogResponse> getLatestLog(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(temperatureMonitoringService.getLatestLog(shipmentId));
    }
}
