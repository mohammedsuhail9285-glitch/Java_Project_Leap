package com.coldchain.tracker.repository;

import com.coldchain.tracker.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Alert entities.
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByShipmentIdOrderByCreatedAtDesc(Long shipmentId);

    List<Alert> findByIsResolvedFalseOrderByCreatedAtDesc();

    List<Alert> findAllByOrderByCreatedAtDesc();

    List<Alert> findTop5ByOrderByCreatedAtDesc();

    long countByIsResolvedFalse();

    long countByShipmentId(Long shipmentId);
}
