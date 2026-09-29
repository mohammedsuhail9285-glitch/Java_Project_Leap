package com.coldchain.tracker.dto.request;

import com.coldchain.tracker.entity.enums.VehicleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Payload for registering or updating a refrigerated vehicle.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {

    @NotBlank(message = "Vehicle number cannot be blank")
    @Schema(example = "KA-01-EQ-9876")
    private String vehicleNumber;

    @NotBlank(message = "Driver name cannot be blank")
    @Schema(example = "Rajesh Kumar")
    private String driverName;

    @NotBlank(message = "Driver contact number cannot be blank")
    @Schema(example = "+91-9876543210")
    private String driverContact;

    @Schema(example = "Tata 407 Reefer 4-Ton")
    private String model;

    @Positive(message = "Capacity must be greater than zero")
    @Schema(example = "4000.0")
    private Double capacityKg;

    @Schema(example = "AVAILABLE")
    private VehicleStatus status;
}
