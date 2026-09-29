package com.coldchain.tracker.dto.response;

import com.coldchain.tracker.entity.enums.ShipmentStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Detailed view of a shipment including telemetry summary and assigned vehicle.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentResponse {

    private Long id;
    private String trackingNumber;
    private String productName;
    private String productCategory;
    private String origin;
    private String destination;
    private ShipmentStatus status;
    private Double minTempThreshold;
    private Double maxTempThreshold;
    private VehicleResponse vehicle;
    private String createdByUsername;
    private LocalDateTime departureTime;
    private LocalDateTime estimatedArrivalTime;
    private LocalDateTime actualArrivalTime;
    private LocalDateTime createdAt;
    private int totalLogsCount;
    private int violationsCount;
    private Double latestTemperature;
    private LocalDateTime latestLogTime;
    private boolean hasActiveViolation;
}
