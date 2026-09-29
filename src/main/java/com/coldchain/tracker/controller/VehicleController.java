package com.coldchain.tracker.controller;

import com.coldchain.tracker.dto.request.VehicleRequest;
import com.coldchain.tracker.dto.response.VehicleResponse;
import com.coldchain.tracker.entity.enums.VehicleStatus;
import com.coldchain.tracker.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for refrigerated vehicle fleet operations.
 */
@RestController
@RequestMapping({"/api/v1/vehicles", "/vehicles"})
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "3. Vehicle Module", description = "Endpoints for registering and managing cold-chain fleet vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register new vehicle", description = "ADMIN only: Add a new refrigerated transport vehicle")
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all vehicles", description = "Retrieve list of all registered logistics vehicles")
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Retrieve details for a specific vehicle by primary key")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/number/{vehicleNumber}")
    @Operation(summary = "Get vehicle by registration number", description = "Lookup vehicle by unique license/registration plate")
    public ResponseEntity<VehicleResponse> getVehicleByNumber(@PathVariable String vehicleNumber) {
        return ResponseEntity.ok(vehicleService.getVehicleByNumber(vehicleNumber));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update vehicle operational status", description = "ADMIN only: Change vehicle status (AVAILABLE, IN_USE, MAINTENANCE)")
    public ResponseEntity<VehicleResponse> updateVehicleStatus(
            @PathVariable Long id,
            @RequestParam VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(id, status));
    }
}
