package com.coldchain.tracker.service;

import com.coldchain.tracker.dto.request.VehicleRequest;
import com.coldchain.tracker.dto.response.VehicleResponse;
import com.coldchain.tracker.entity.enums.VehicleStatus;

import java.util.List;

/**
 * Service interface for managing refrigerated vehicle assets.
 */
public interface VehicleService {

    VehicleResponse createVehicle(VehicleRequest request);

    VehicleResponse getVehicleById(Long id);

    VehicleResponse getVehicleByNumber(String vehicleNumber);

    List<VehicleResponse> getAllVehicles();

    VehicleResponse updateVehicleStatus(Long id, VehicleStatus status);
}
