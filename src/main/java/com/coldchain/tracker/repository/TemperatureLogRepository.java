package com.coldchain.tracker.repository;

import com.coldchain.tracker.entity.TemperatureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for TemperatureLog entities.
 */
@Repository
public interface TemperatureLogRepository extends JpaRepository<TemperatureLog, Long> {

    List<TemperatureLog> findByShipmentIdOrderByTimestampDesc(Long shipmentId);

    List<TemperatureLog> findByShipmentIdOrderByTimestampAsc(Long shipmentId);

    Optional<TemperatureLog> findTopByShipmentIdOrderByTimestampDesc(Long shipmentId);

    long countByShipmentId(Long shipmentId);

    long countByShipmentIdAndIsViolationTrue(Long shipmentId);

    long countByIsViolationTrue();
}
