package com.coldchain.tracker.controller;

import com.coldchain.tracker.dto.request.ShipmentCreateRequest;
import com.coldchain.tracker.dto.request.ShipmentStatusUpdateRequest;
import com.coldchain.tracker.dto.response.PagedResponse;
import com.coldchain.tracker.dto.response.ShipmentResponse;
import com.coldchain.tracker.entity.enums.ShipmentStatus;
import com.coldchain.tracker.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Cold-Chain Shipment dispatch, tracking, and status lifecycle.
 */
@RestController
@RequestMapping({"/api/v1/shipments", "/shipments"})
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "4. Shipment Module", description = "Endpoints for creating shipments, tracking transit lifecycles, and querying telemetry status")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    @Operation(summary = "Create cold-chain shipment", description = "Dispatches a new shipment with origin, destination, safe temperature boundaries, and vehicle assignment")
    public ResponseEntity<ShipmentResponse> createShipment(
            @Valid @RequestBody ShipmentCreateRequest request,
            Authentication authentication) {
        String currentUsername = (authentication != null) ? authentication.getName() : null;
        ShipmentResponse response = shipmentService.createShipment(request, currentUsername);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List shipments with pagination and filters", description = "Retrieve paginated and sorted cold-chain shipments, optionally filtered by status")
    public ResponseEntity<PagedResponse<ShipmentResponse>> getAllShipments(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filter by status (CREATED, IN_TRANSIT, DELIVERED, DELAYED)") @RequestParam(required = false) ShipmentStatus status) {

        PagedResponse<ShipmentResponse> response = shipmentService.getAllShipments(page, size, sortBy, sortDir, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shipment by ID", description = "Retrieve shipment metadata, vehicle assignment, and current telemetry summary")
    public ResponseEntity<ShipmentResponse> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentById(id));
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Get shipment by tracking number", description = "Look up a consignment via its unique cold-chain tracking code")
    public ResponseEntity<ShipmentResponse> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(shipmentService.getShipmentByTrackingNumber(trackingNumber));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update shipment lifecycle status", description = "Transitions shipment status between CREATED, IN_TRANSIT, DELIVERED, and DELAYED")
    public ResponseEntity<ShipmentResponse> updateShipmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentStatusUpdateRequest request) {
        return ResponseEntity.ok(shipmentService.updateShipmentStatus(id, request));
    }
}
