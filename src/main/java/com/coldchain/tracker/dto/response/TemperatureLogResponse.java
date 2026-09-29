package com.coldchain.tracker.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Details of an individual temperature log entry.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemperatureLogResponse {

    private Long id;
    private Long shipmentId;
    private String trackingNumber;
    private Double temperature;
    private Double humidity;
    private String locationCoordinates;
    private LocalDateTime timestamp;
    private boolean isViolation;
    private String complianceStatus; // "COMPLIANT" or "BREACH_DETECTED"
}
