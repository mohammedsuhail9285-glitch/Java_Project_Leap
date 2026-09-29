package com.coldchain.tracker.repository;

import com.coldchain.tracker.entity.Shipment;
import com.coldchain.tracker.entity.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Shipment entities.
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    boolean existsByTrackingNumber(String trackingNumber);

    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);

    List<Shipment> findByStatus(ShipmentStatus status);

    long countByStatus(ShipmentStatus status);

    long countByStatusIn(Collection<ShipmentStatus> statuses);

    List<Shipment> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT s FROM Shipment s LEFT JOIN FETCH s.vehicle WHERE s.id = :id")
    Optional<Shipment> findByIdWithVehicle(Long id);
}
