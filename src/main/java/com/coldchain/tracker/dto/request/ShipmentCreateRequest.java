package com.coldchain.tracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Payload for dispatching/creating a cold-chain shipment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentCreateRequest {

    @NotBlank(message = "Product name cannot be blank")
    @Schema(example = "Covaxin COVID-19 Vaccine (10,000 vials)")
    private String productName;

    @Schema(example = "VACCINE")
    private String productCategory;

    @NotBlank(message = "Origin address/hub cannot be blank")
    @Schema(example = "Bharat Biotech Hub, Hyderabad")
    private String origin;

    @NotBlank(message = "Destination address/hub cannot be blank")
    @Schema(example = "Apollo Central Hospital, Bengaluru")
    private String destination;

    @NotNull(message = "Assigned vehicle ID must be specified")
    @Schema(example = "1")
    private Long vehicleId;

    @Schema(example = "2.0", description = "Minimum acceptable temperature in Celsius")
    private Double minTempThreshold;

    @Schema(example = "8.0", description = "Maximum acceptable temperature in Celsius")
    private Double maxTempThreshold;

    @Schema(example = "2026-09-28T08:00:00")
    private LocalDateTime departureTime;

    @Schema(example = "2026-09-28T20:00:00")
    private LocalDateTime estimatedArrivalTime;
}
