package com.coldchain.tracker.service;

import com.coldchain.tracker.dto.request.ShipmentCreateRequest;
import com.coldchain.tracker.dto.request.ShipmentStatusUpdateRequest;
import com.coldchain.tracker.dto.response.PagedResponse;
import com.coldchain.tracker.dto.response.ShipmentResponse;
import com.coldchain.tracker.entity.enums.ShipmentStatus;

/**
 * Service interface for Cold-Chain Shipment lifecycle management.
 */
public interface ShipmentService {

    ShipmentResponse createShipment(ShipmentCreateRequest request, String currentUsername);

    ShipmentResponse getShipmentById(Long id);

    ShipmentResponse getShipmentByTrackingNumber(String trackingNumber);

    PagedResponse<ShipmentResponse> getAllShipments(int page, int size, String sortBy, String sortDir, ShipmentStatus status);

    ShipmentResponse updateShipmentStatus(Long id, ShipmentStatusUpdateRequest request);
}
