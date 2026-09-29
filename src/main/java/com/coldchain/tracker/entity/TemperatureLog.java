package com.coldchain.tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing an IoT / sensor telemetry temperature reading for a shipment.
 */
@Entity
@Table(name = "temperature_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemperatureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(nullable = false)
    private Double temperature;

    @Column
    private Double humidity;

    @Column(name = "location_coordinates", length = 100)
    private String locationCoordinates;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "is_violation", nullable = false)
    @Builder.Default
    private boolean isViolation = false;

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
