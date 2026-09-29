package com.coldchain.tracker.service.impl;

import com.coldchain.tracker.dto.response.AlertResponse;
import com.coldchain.tracker.dto.response.DashboardSummaryResponse;
import com.coldchain.tracker.dto.response.ShipmentResponse;
import com.coldchain.tracker.entity.enums.ShipmentStatus;
import com.coldchain.tracker.entity.enums.VehicleStatus;
import com.coldchain.tracker.repository.AlertRepository;
import com.coldchain.tracker.repository.ShipmentRepository;
import com.coldchain.tracker.repository.TemperatureLogRepository;
import com.coldchain.tracker.repository.VehicleRepository;
import com.coldchain.tracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of DashboardService aggregating logistics and compliance metrics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final ShipmentRepository shipmentRepository;
    private final AlertRepository alertRepository;
    private final VehicleRepository vehicleRepository;
    private final TemperatureLogRepository temperatureLogRepository;
    private final AlertServiceImpl alertService;
    private final ShipmentServiceImpl shipmentService;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        log.info("Aggregating dashboard compliance and logistics metrics");

        long totalShipments = shipmentRepository.count();
        long activeShipments = shipmentRepository.countByStatusIn(
                List.of(ShipmentStatus.CREATED, ShipmentStatus.IN_TRANSIT)
        );
        long deliveredShipments = shipmentRepository.countByStatus(ShipmentStatus.DELIVERED);
        long delayedShipments = shipmentRepository.countByStatus(ShipmentStatus.DELAYED);

        long violationsCount = alertRepository.count();
        long unresolvedAlerts = alertRepository.countByIsResolvedFalse();

        long totalVehicles = vehicleRepository.count();
        long availableVehicles = vehicleRepository.countByStatus(VehicleStatus.AVAILABLE);

        long totalLogs = temperatureLogRepository.count();
        long totalBreachLogs = temperatureLogRepository.countByIsViolationTrue();

        double complianceRate = 100.0;
        if (totalLogs > 0) {
            double rate = ((double) (totalLogs - totalBreachLogs) / totalLogs) * 100.0;
            complianceRate = BigDecimal.valueOf(rate)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        List<AlertResponse> recentAlerts = alertRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(alertService::mapToAlertResponse)
                .collect(Collectors.toList());

        List<ShipmentResponse> recentShipments = shipmentRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(shipmentService::mapToShipmentResponse)
                .collect(Collectors.toList());

        return DashboardSummaryResponse.builder()
                .totalShipments(totalShipments)
                .activeShipments(activeShipments)
                .deliveredShipments(deliveredShipments)
                .delayedShipments(delayedShipments)
                .violationsCount(violationsCount)
                .unresolvedAlertsCount(unresolvedAlerts)
                .safeCompliancePercentage(complianceRate)
                .totalVehicles(totalVehicles)
                .availableVehicles(availableVehicles)
                .totalTemperatureLogs(totalLogs)
                .recentAlerts(recentAlerts)
                .recentShipments(recentShipments)
                .build();
    }
}
