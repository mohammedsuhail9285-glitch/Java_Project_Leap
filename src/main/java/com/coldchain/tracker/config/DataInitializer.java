package com.coldchain.tracker.config;

import com.coldchain.tracker.dto.request.TemperatureLogRequest;
import com.coldchain.tracker.entity.Shipment;
import com.coldchain.tracker.entity.User;
import com.coldchain.tracker.entity.Vehicle;
import com.coldchain.tracker.entity.enums.Role;
import com.coldchain.tracker.entity.enums.ShipmentStatus;
import com.coldchain.tracker.entity.enums.VehicleStatus;
import com.coldchain.tracker.repository.ShipmentRepository;
import com.coldchain.tracker.repository.UserRepository;
import com.coldchain.tracker.repository.VehicleRepository;
import com.coldchain.tracker.service.TemperatureMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeds initial demo data (Admin, Operator, Vehicles, Shipments, Telemetry) on application startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ShipmentRepository shipmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemperatureMonitoringService temperatureMonitoringService;

    @Override
    public void run(String... args) {
        log.info("Checking and seeding initial cold-chain demo datasets...");

        // 1. Seed Default Admin
        User admin = userRepository.findByUsername("admin").orElseGet(() -> {
            User newAdmin = User.builder()
                    .username("admin")
                    .email("admin@coldchain.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .fullName("ColdChain System Administrator")
                    .role(Role.ADMIN)
                    .build();
            log.info("Seeding default ADMIN user: admin / Admin@123");
            return userRepository.save(newAdmin);
        });

        // 2. Seed Default Operator
        userRepository.findByUsername("operator").orElseGet(() -> {
            User newOperator = User.builder()
                    .username("operator")
                    .email("operator@coldchain.com")
                    .password(passwordEncoder.encode("Operator@123"))
                    .fullName("Rajesh Patel (Logistics Operator)")
                    .role(Role.OPERATOR)
                    .build();
            log.info("Seeding default OPERATOR user: operator / Operator@123");
            return userRepository.save(newOperator);
        });

        // 3. Seed Vehicles
        Vehicle vehicle1 = vehicleRepository.findByVehicleNumber("KA-01-EQ-1001").orElseGet(() -> {
            Vehicle v = Vehicle.builder()
                    .vehicleNumber("KA-01-EQ-1001")
                    .driverName("Ramesh Babu")
                    .driverContact("+91-9845012345")
                    .model("Eicher Pro 2049 Reefer 4-Ton")
                    .capacityKg(4000.0)
                    .status(VehicleStatus.IN_USE)
                    .build();
            log.info("Seeding demo vehicle: KA-01-EQ-1001");
            return vehicleRepository.save(v);
        });

        vehicleRepository.findByVehicleNumber("MH-12-RN-2002").orElseGet(() -> {
            Vehicle v = Vehicle.builder()
                    .vehicleNumber("MH-12-RN-2002")
                    .driverName("Suresh Nair")
                    .driverContact("+91-9822098765")
                    .model("BharatBenz 1217C Heavy Chiller")
                    .capacityKg(7500.0)
                    .status(VehicleStatus.AVAILABLE)
                    .build();
            log.info("Seeding demo vehicle: MH-12-RN-2002");
            return vehicleRepository.save(v);
        });

        // 4. Seed Initial Active Shipment if none exists
        if (shipmentRepository.count() == 0) {
            Shipment shipment = Shipment.builder()
                    .trackingNumber("CC-DEMO-VACCINE-01")
                    .productName("Pfizer-BioNTech mRNA COVID-19 Vaccine (5,000 Doses)")
                    .productCategory("VACCINE")
                    .origin("Serum Bio-Pharma Hub, Pune")
                    .destination("Government General Hospital, Bengaluru")
                    .status(ShipmentStatus.IN_TRANSIT)
                    .vehicle(vehicle1)
                    .createdBy(admin)
                    .minTempThreshold(2.0)
                    .maxTempThreshold(8.0)
                    .departureTime(LocalDateTime.now().minusHours(4))
                    .estimatedArrivalTime(LocalDateTime.now().plusHours(8))
                    .build();

            Shipment savedShipment = shipmentRepository.save(shipment);
            log.info("Seeded demo shipment: {}", savedShipment.getTrackingNumber());

            // 5. Seed Temperature Logs (2 safe readings, 1 breach reading to verify automated alert)
            temperatureMonitoringService.recordTemperatureLog(
                    savedShipment.getId(),
                    TemperatureLogRequest.builder()
                            .temperature(4.2)
                            .humidity(55.0)
                            .locationCoordinates("18.520430, 73.856744")
                            .timestamp(LocalDateTime.now().minusHours(3))
                            .build()
            );

            temperatureMonitoringService.recordTemperatureLog(
                    savedShipment.getId(),
                    TemperatureLogRequest.builder()
                            .temperature(5.5)
                            .humidity(58.2)
                            .locationCoordinates("17.659920, 75.906391")
                            .timestamp(LocalDateTime.now().minusHours(2))
                            .build()
            );

            // Breach reading (> 8.0°C) -> Automatically triggers Alert!
            temperatureMonitoringService.recordTemperatureLog(
                    savedShipment.getId(),
                    TemperatureLogRequest.builder()
                            .temperature(9.8)
                            .humidity(68.5)
                            .locationCoordinates("16.852398, 74.581474")
                            .timestamp(LocalDateTime.now().minusHours(1))
                            .build()
            );

            log.info("Successfully seeded telemetry logs and automated alert demonstration!");
        }

        log.info("Cold-Chain System Data Initializer completed successfully.");
    }
}
