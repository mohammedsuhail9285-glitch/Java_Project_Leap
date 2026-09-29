package com.coldchain.tracker.dto.response;

import lombok.*;

import java.util.List;

/**
 * Aggregated cold-chain metrics for the executive and operations dashboard.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {

    private long totalShipments;
    private long activeShipments;
    private long deliveredShipments;
    private long delayedShipments;
    private long violationsCount;
    private long unresolvedAlertsCount;
    private double safeCompliancePercentage;
    private long totalVehicles;
    private long availableVehicles;
    private long totalTemperatureLogs;
    private List<AlertResponse> recentAlerts;
    private List<ShipmentResponse> recentShipments;
}
