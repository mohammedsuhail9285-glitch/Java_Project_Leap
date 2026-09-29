package com.coldchain.tracker.service.impl;

import com.coldchain.tracker.dto.request.VehicleRequest;
import com.coldchain.tracker.dto.response.VehicleResponse;
import com.coldchain.tracker.entity.Vehicle;
import com.coldchain.tracker.entity.enums.VehicleStatus;
import com.coldchain.tracker.exception.BadRequestException;
import com.coldchain.tracker.exception.ResourceNotFoundException;
import com.coldchain.tracker.repository.VehicleRepository;
import com.coldchain.tracker.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of VehicleService managing fleet records and operating statuses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public VehicleResponse createVehicle(VehicleRequest request) {
        log.info("Creating vehicle with number: {}", request.getVehicleNumber());

        if (vehicleRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new BadRequestException("Vehicle with registration '" + request.getVehicleNumber() + "' already exists.");
        }

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(request.getVehicleNumber().trim().toUpperCase())
                .driverName(request.getDriverName().trim())
                .driverContact(request.getDriverContact().trim())
                .model(request.getModel() != null ? request.getModel().trim() : null)
                .capacityKg(request.getCapacityKg())
                .status(request.getStatus() != null ? request.getStatus() : VehicleStatus.AVAILABLE)
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created successfully with ID: {}", savedVehicle.getId());

        return mapToVehicleResponse(savedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        return mapToVehicleResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleByNumber(String vehicleNumber) {
        Vehicle vehicle = vehicleRepository.findByVehicleNumber(vehicleNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "vehicleNumber", vehicleNumber));
        return mapToVehicleResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(VehicleServiceImpl::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicleStatus(Long id, VehicleStatus status) {
        log.info("Updating vehicle ID {} status to {}", id, status);
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        vehicle.setStatus(status);
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return mapToVehicleResponse(updatedVehicle);
    }

    public static VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
        if (vehicle == null) return null;
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .driverName(vehicle.getDriverName())
                .driverContact(vehicle.getDriverContact())
                .model(vehicle.getModel())
                .capacityKg(vehicle.getCapacityKg())
                .status(vehicle.getStatus())
                .createdAt(vehicle.getCreatedAt())
                .build();
    }
}
