package com.coldchain.tracker.controller;

import com.coldchain.tracker.dto.response.AlertResponse;
import com.coldchain.tracker.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Cold-Chain Alert audit trails and violation resolution.
 */
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "6. Alert System Module", description = "Endpoints for viewing automated temperature breach alerts and recording corrective actions")
public class AlertController {

    private final AlertService alertService;

    @GetMapping({"/api/v1/alerts/{shipmentId}", "/alerts/{shipmentId}"})
    @Operation(summary = "Get alerts for a shipment", description = "Retrieves all automated breach alerts linked to a specific shipment")
    public ResponseEntity<List<AlertResponse>> getAlertsByShipment(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(alertService.getAlertsByShipmentId(shipmentId));
    }

    @GetMapping({"/api/v1/alerts", "/alerts"})
    @Operation(summary = "Get all alerts", description = "Retrieves listing of all temperature breach alerts system-wide")
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @PatchMapping({"/api/v1/alerts/{id}/resolve", "/alerts/{id}/resolve"})
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resolve alert", description = "ADMIN only: Mark an alert as resolved with supervisor notes and corrective actions taken")
    public ResponseEntity<AlertResponse> resolveAlert(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Chiller unit adjusted. Cargo inspected.") String notes,
            Authentication authentication) {
        String resolver = (authentication != null) ? authentication.getName() : "ADMIN";
        return ResponseEntity.ok(alertService.resolveAlert(id, resolver, notes));
    }
}
