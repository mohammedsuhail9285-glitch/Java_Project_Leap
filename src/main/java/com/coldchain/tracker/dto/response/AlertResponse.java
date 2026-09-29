package com.coldchain.tracker.dto.response;

import com.coldchain.tracker.entity.enums.AlertSeverity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Details of an alert triggered due to temperature non-compliance.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertResponse {

    private Long id;
    private Long shipmentId;
    private String trackingNumber;
    private String productName;
    private Long temperatureLogId;
    private Double recordedTemperature;
    private String breachType;
    private AlertSeverity severity;
    private String message;
    private boolean isResolved;
    private String resolvedBy;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
