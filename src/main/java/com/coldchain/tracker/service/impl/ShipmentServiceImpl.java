package com.coldchain.tracker.service.impl;

import com.coldchain.tracker.dto.request.ShipmentCreateRequest;
import com.coldchain.tracker.dto.request.ShipmentStatusUpdateRequest;
import com.coldchain.tracker.dto.response.PagedResponse;
import com.coldchain.tracker.dto.response.ShipmentResponse;
import com.coldchain.tracker.entity.Shipment;
import com.coldchain.tracker.entity.TemperatureLog;
import com.coldchain.tracker.entity.User;
import com.coldchain.tracker.entity.Vehicle;
import com.coldchain.tracker.entity.enums.ShipmentStatus;
import com.coldchain.tracker.entity.enums.VehicleStatus;
import com.coldchain.tracker.exception.BadRequestException;
import com.coldchain.tracker.exception.ResourceNotFoundException;
import com.coldchain.tracker.repository.ShipmentRepository;
import com.coldchain.tracker.repository.TemperatureLogRepository;
import com.coldchain.tracker.repository.UserRepository;
import com.coldchain.tracker.repository.VehicleRepository;
import com.coldchain.tracker.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ShipmentService managing cold-chain logistics lifecycle and telemetry states.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final TemperatureLogRepository temperatureLogRepository;

    @Override
    @Transactional
    public ShipmentResponse createShipment(ShipmentCreateRequest request, String currentUsername) {
        log.info("Creating cold-chain shipment for product: {}", request.getProductName());

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
            throw new BadRequestException("Vehicle " + vehicle.getVehicleNumber() + " is under maintenance and cannot be assigned.");
        }

        User createdBy = null;
        if (currentUsername != null) {
            createdBy = userRepository.findByUsername(currentUsername).orElse(null);
        }

        String trackingNumber = generateTrackingNumber();

        double minTemp = request.getMinTempThreshold() != null ? request.getMinTempThreshold() : 2.0;
        double maxTemp = request.getMaxTempThreshold() != null ? request.getMaxTempThreshold() : 8.0;

        if (minTemp >= maxTemp) {
            throw new BadRequestException("Minimum temperature threshold must be lower than maximum temperature threshold.");
        }

        Shipment shipment = Shipment.builder()
                .trackingNumber(trackingNumber)
                .productName(request.getProductName().trim())
                .productCategory(request.getProductCategory() != null ? request.getProductCategory().trim() : "PHARMACEUTICALS")
                .origin(request.getOrigin().trim())
                .destination(request.getDestination().trim())
                .status(ShipmentStatus.CREATED)
                .vehicle(vehicle)
                .createdBy(createdBy)
                .minTempThreshold(minTemp)
                .maxTempThreshold(maxTemp)
                .departureTime(request.getDepartureTime())
                .estimatedArrivalTime(request.getEstimatedArrivalTime())
                .build();

        // Mark vehicle as in use
        vehicle.setStatus(VehicleStatus.IN_USE);
        vehicleRepository.save(vehicle);

        Shipment savedShipment = shipmentRepository.save(shipment);
        log.info("Shipment created successfully with Tracking Number: {}", savedShipment.getTrackingNumber());

        return mapToShipmentResponse(savedShipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));
        return mapToShipmentResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "trackingNumber", trackingNumber));
        return mapToShipmentResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ShipmentResponse> getAllShipments(
            int page, int size, String sortBy, String sortDir, ShipmentStatus status) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Shipment> shipmentsPage;

        if (status != null) {
            shipmentsPage = shipmentRepository.findByStatus(status, pageable);
        } else {
            shipmentsPage = shipmentRepository.findAll(pageable);
        }

        List<ShipmentResponse> content = shipmentsPage.getContent().stream()
                .map(this::mapToShipmentResponse)
                .collect(Collectors.toList());

        return PagedResponse.<ShipmentResponse>builder()
                .content(content)
                .pageNo(shipmentsPage.getNumber())
                .pageSize(shipmentsPage.getSize())
                .totalElements(shipmentsPage.getTotalElements())
                .totalPages(shipmentsPage.getTotalPages())
                .isLast(shipmentsPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public ShipmentResponse updateShipmentStatus(Long id, ShipmentStatusUpdateRequest request) {
        log.info("Updating shipment ID {} status to {}", id, request.getStatus());

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));

        ShipmentStatus newStatus = request.getStatus();
        shipment.setStatus(newStatus);

        if (newStatus == ShipmentStatus.IN_TRANSIT && shipment.getDepartureTime() == null) {
            shipment.setDepartureTime(LocalDateTime.now());
        } else if (newStatus == ShipmentStatus.DELIVERED) {
            shipment.setActualArrivalTime(LocalDateTime.now());
            // Release vehicle back to available pool
            Vehicle vehicle = shipment.getVehicle();
            if (vehicle != null) {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
                vehicleRepository.save(vehicle);
            }
        }

        Shipment updatedShipment = shipmentRepository.save(shipment);
        log.info("Shipment status updated to {} for tracking number: {}", newStatus, updatedShipment.getTrackingNumber());

        return mapToShipmentResponse(updatedShipment);
    }

    private String generateTrackingNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "CC-" + datePart + "-" + randomPart;
    }

    public ShipmentResponse mapToShipmentResponse(Shipment shipment) {
        if (shipment == null) return null;

        int totalLogs = (int) temperatureLogRepository.countByShipmentId(shipment.getId());
        int violationsCount = (int) temperatureLogRepository.countByShipmentIdAndIsViolationTrue(shipment.getId());

        Optional<TemperatureLog> latestLogOpt = temperatureLogRepository.findTopByShipmentIdOrderByTimestampDesc(shipment.getId());
        Double latestTemp = latestLogOpt.map(TemperatureLog::getTemperature).orElse(null);
        LocalDateTime latestLogTime = latestLogOpt.map(TemperatureLog::getTimestamp).orElse(null);

        boolean hasActiveViolation = latestLogOpt.map(TemperatureLog::isViolation).orElse(false);

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .trackingNumber(shipment.getTrackingNumber())
                .productName(shipment.getProductName())
                .productCategory(shipment.getProductCategory())
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .status(shipment.getStatus())
                .minTempThreshold(shipment.getMinTempThreshold())
                .maxTempThreshold(shipment.getMaxTempThreshold())
                .vehicle(VehicleServiceImpl.mapToVehicleResponse(shipment.getVehicle()))
                .createdByUsername(shipment.getCreatedBy() != null ? shipment.getCreatedBy().getUsername() : "SYSTEM")
                .departureTime(shipment.getDepartureTime())
                .estimatedArrivalTime(shipment.getEstimatedArrivalTime())
                .actualArrivalTime(shipment.getActualArrivalTime())
                .createdAt(shipment.getCreatedAt())
                .totalLogsCount(totalLogs)
                .violationsCount(violationsCount)
                .latestTemperature(latestTemp)
                .latestLogTime(latestLogTime)
                .hasActiveViolation(hasActiveViolation)
                .build();
    }
}
