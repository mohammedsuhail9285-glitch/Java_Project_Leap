package com.coldchain.tracker.dto.response;

import com.coldchain.tracker.entity.enums.VehicleStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Details of a refrigerated vehicle.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    private Long id;
    private String vehicleNumber;
    private String driverName;
    private String driverContact;
    private String model;
    private Double capacityKg;
    private VehicleStatus status;
    private LocalDateTime createdAt;
}
