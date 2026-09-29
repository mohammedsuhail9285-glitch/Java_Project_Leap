# Automated Cold-Chain Logistics & Temperature Compliance Tracker

[![Java](https://img.shields.io/badge/Java-17%2F21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-blue.svg)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208-blue.svg)](https://www.mysql.com/)
[![Swagger](https://img.shields.io/badge/OpenAPI-Swagger%20UI%203.0-green.svg)](http://localhost:8080/swagger-ui.html)

---

## 📌 Executive Summary & Project Objective

The **Automated Cold-Chain Logistics & Temperature Compliance Tracker** is an enterprise-grade backend system engineered to track, safeguard, and monitor shipments of temperature-sensitive biologicals, pharmaceuticals (e.g., mRNA vaccines, insulin, blood plasma), and perishable goods throughout transit.

The system ingests real-time IoT telematics data, strictly enforces regulatory temperature safety standards (**Safe Compliance Range: 2.0°C to 8.0°C**), dynamically identifies thermal breaches, and triggers instant automated alerts to safeguard cargo integrity and avert multimillion-dollar spoilage.

---

## 🏛️ System Architecture

The application is built following **Clean Layered Architecture** with strict separation of concerns:

```
controller/       --> Handles incoming HTTP REST requests, maps endpoints, performs validation.
service/          --> Encapsulates core cold-chain business rules, compliance evaluations, and state transitions.
repository/       --> Spring Data JPA data access interfaces leveraging Hibernate ORM.
entity/           --> Relational domain models mapping tables, foreign keys, and indexes.
dto/              --> Request and Response Transfer Objects preventing direct entity leakage.
exception/        --> Centralized @RestControllerAdvice exception handler with standardized RFC error payloads.
config/           --> Spring Security (JWT), OpenAPI Swagger UI documentation, and Data Seeding.
security/         --> JWT Token Provider, Bearer Filter, and UserDetails authentication adapter.
```

---

## 🗂️ Complete Project Structure

```
d:\JavaProjectReport\
├── pom.xml
├── README.md
├── schema.sql
├── cold-chain-tracker.postman_collection.json
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── coldchain
│   │   │           └── tracker
│   │   │               ├── ColdChainTrackerApplication.java
│   │   │               ├── config
│   │   │               │   ├── DataInitializer.java
│   │   │               │   ├── OpenApiConfig.java
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── controller
│   │   │               │   ├── AlertController.java
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── DashboardController.java
│   │   │               │   ├── ShipmentController.java
│   │   │               │   ├── TemperatureLogController.java
│   │   │               │   ├── UserController.java
│   │   │               │   └── VehicleController.java
│   │   │               ├── dto
│   │   │               │   ├── request
│   │   │               │   │   ├── ShipmentCreateRequest.java
│   │   │               │   │   ├── ShipmentStatusUpdateRequest.java
│   │   │               │   │   ├── TemperatureLogRequest.java
│   │   │               │   │   ├── UserLoginRequest.java
│   │   │               │   │   ├── UserRegisterRequest.java
│   │   │               │   │   └── VehicleRequest.java
│   │   │               │   └── response
│   │   │               │       ├── AlertResponse.java
│   │   │               │       ├── AuthResponse.java
│   │   │               │       ├── DashboardSummaryResponse.java
│   │   │               │       ├── ErrorResponse.java
│   │   │               │       ├── PagedResponse.java
│   │   │               │       ├── ShipmentResponse.java
│   │   │               │       ├── TemperatureLogResponse.java
│   │   │               │       ├── UserResponse.java
│   │   │               │       └── VehicleResponse.java
│   │   │               ├── entity
│   │   │               │   ├── Alert.java
│   │   │               │   ├── Shipment.java
│   │   │               │   ├── TemperatureLog.java
│   │   │               │   ├── User.java
│   │   │               │   ├── Vehicle.java
│   │   │               │   └── enums
│   │   │               │       ├── AlertSeverity.java
│   │   │               │       ├── Role.java
│   │   │               │       ├── ShipmentStatus.java
│   │   │               │       └── VehicleStatus.java
│   │   │               ├── exception
│   │   │               │   ├── BadRequestException.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── InvalidTemperatureException.java
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   └── UnauthorizedException.java
│   │   │               ├── repository
│   │   │               │   ├── AlertRepository.java
│   │   │               │   ├── ShipmentRepository.java
│   │   │               │   ├── TemperatureLogRepository.java
│   │   │               │   ├── UserRepository.java
│   │   │               │   └── VehicleRepository.java
│   │   │               ├── security
│   │   │               │   ├── CustomUserDetailsService.java
│   │   │               │   ├── JwtAuthenticationEntryPoint.java
│   │   │               │   ├── JwtAuthenticationFilter.java
│   │   │               │   ├── JwtTokenProvider.java
│   │   │               │   └── UserPrincipal.java
│   │   │               └── service
│   │   │                   ├── AlertService.java
│   │   │                   ├── DashboardService.java
│   │   │                   ├── ShipmentService.java
│   │   │                   ├── TemperatureMonitoringService.java
│   │   │                   ├── UserService.java
│   │   │                   ├── VehicleService.java
│   │   │                   └── impl
│   │   │                       ├── AlertServiceImpl.java
│   │   │                       ├── DashboardServiceImpl.java
│   │   │                       ├── ShipmentServiceImpl.java
│   │   │                       ├── TemperatureMonitoringServiceImpl.java
│   │   │                       ├── UserServiceImpl.java
│   │   │                       └── VehicleServiceImpl.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── schema.sql
│   └── test
│       └── java
│           └── com
│               └── coldchain
│                   └── tracker
│                       └── ColdChainTrackerApplicationTests.java
```

---

## 🗄️ Relational Database Design & Entity Relationships

The data model is engineered for high throughput and transactional integrity:

```
  +------------------+         1:N          +----------------------+
  |      User        | -------------------> |       Shipment       |
  | (ADMIN/OPERATOR) |                      +----------------------+
  +------------------+                                 |
                                                       |
  +------------------+         N:1                     |
  |     Vehicle      | <-------------------------------+
  | (AVAILABLE/IN_USE)                                 |
  +------------------+                                 | 1:N
                                                       |
                                 +---------------------+---------------------+
                                 |                                           |
                                 v                                           v
                     +-----------------------+                   +-----------------------+
                     |    TemperatureLog     | <--- 1:N -------- |         Alert         |
                     |  (Sensor Telemetry)   |                   | (Breach Notification) |
                     +-----------------------+                   +-----------------------+
```

### Table Mappings:
1. **`users`**:
   - Stores authenticated credentials (`username`, `email`, BCrypt `password`), full name, and authorization `role` (`ADMIN`, `OPERATOR`).
2. **`vehicles`**:
   - Fleet assets tracking refrigerated reefer units (`vehicle_number`, `driver_name`, `driver_contact`, `capacity_kg`, `status`).
3. **`shipments`**:
   - Master consignment records (`tracking_number`, `product_name`, `origin`, `destination`, `status`, `min_temp_threshold` [2.0°C], `max_temp_threshold` [8.0°C]).
   - **Many-to-One** relationship with `Vehicle` and `User`.
   - **One-to-Many** cascading relation with `TemperatureLog` and `Alert`.
4. **`temperature_logs`**:
   - Immutable time-series telemetry records (`temperature`, `humidity`, `location_coordinates`, `timestamp`, `is_violation`).
5. **`alerts`**:
   - Automated incident records triggered upon breach (`recorded_temperature`, `breach_type`, `severity` [WARNING, HIGH, CRITICAL], `message`, `is_resolved`).

---

## ⚡ Cold-Chain Temperature Compliance Business Logic

```
   < -50°C OR > +60°C  --> Throws InvalidTemperatureException (Physical sensor malfunction / corruption)
   < 2.0°C             --> Freezing breach! Automatically triggers LOWER_LIMIT_EXCEEDED Alert.
   2.0°C to 8.0°C      --> COMPLIANT SAFE RANGE. Cargo intact. Status: Normal.
   > 8.0°C             --> Thermal spike! Automatically triggers UPPER_LIMIT_EXCEEDED Alert.
```

When a temperature reading is logged via `POST /api/v1/temperature-logs/{shipmentId}`:
1. **Range Validation**: Ensures value is within physical sensor operating limits [-50°C, 60°C]. Otherwise, throws `InvalidTemperatureException`.
2. **Safety Check**: Compares reading against the shipment's threshold bounds (2.0°C to 8.0°C).
3. **Automated Alert Generation**:
   - If `temp > 8.0°C`: Sets breach type `UPPER_LIMIT_EXCEEDED`. If deviation > 5.0°C, assigns `CRITICAL` severity, else `HIGH`.
   - If `temp < 2.0°C`: Sets breach type `LOWER_LIMIT_EXCEEDED`. If deviation > 5.0°C, assigns `CRITICAL` severity, else `HIGH`.
   - Persists the alert with the exact temperature, delta calculation, and timestamp.
4. **Dashboard KPI Impact**: Compliance percentage and violation counters update in real time.

---

## 📡 REST API Reference

All protected endpoints require the HTTP Header: `Authorization: Bearer <JWT_TOKEN>`.

### 1. Authentication & Users
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` or `/api/v1/users` | Public | Register a new user (`ADMIN` or `OPERATOR`) |
| `POST` | `/api/v1/auth/login` or `/api/v1/users/login` | Public | Login with credentials and receive JWT |
| `GET` | `/api/v1/users` | ADMIN | List all registered users |
| `GET` | `/api/v1/users/{id}` | ADMIN | Get user profile by ID |

### 2. Vehicle Fleet Module
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/vehicles` | ADMIN | Register a new refrigerated vehicle |
| `GET` | `/api/v1/vehicles` | Authenticated | List all vehicles in fleet |
| `GET` | `/api/v1/vehicles/{id}` | Authenticated | Get vehicle by ID |
| `GET` | `/api/v1/vehicles/number/{num}` | Authenticated | Lookup vehicle by registration number |
| `PATCH` | `/api/v1/vehicles/{id}/status` | ADMIN | Update vehicle status (`AVAILABLE`, `IN_USE`, `MAINTENANCE`) |

### 3. Shipment Module
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/shipments` | Authenticated | Create cold-chain shipment and assign vehicle |
| `GET` | `/api/v1/shipments` | Authenticated | Paginated & sorted shipments (supports `?status=...`) |
| `GET` | `/api/v1/shipments/{id}` | Authenticated | Get shipment details with telemetry stats |
| `GET` | `/api/v1/shipments/tracking/{code}` | Authenticated | Lookup shipment by tracking number |
| `PATCH` | `/api/v1/shipments/{id}/status` | Authenticated | Update status (`CREATED`, `IN_TRANSIT`, `DELIVERED`, `DELAYED`) |

### 4. Temperature Monitoring Module
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/temperature-logs/{shipmentId}` | Authenticated | Ingest sensor reading. Evaluates compliance & triggers alert |
| `GET` | `/api/v1/temperature-logs/{shipmentId}` | Authenticated | Audit trail of all logs for shipment |
| `GET` | `/api/v1/temperature-logs/{shipmentId}/latest` | Authenticated | Get latest temperature reading |

### 5. Alert System Module
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/alerts/{shipmentId}` | Authenticated | Get all breach alerts for a specific shipment |
| `GET` | `/api/v1/alerts` | Authenticated | List all system-wide breach alerts |
| `PATCH` | `/api/v1/alerts/{id}/resolve` | ADMIN | Mark alert as resolved with supervisor notes |

### 6. Executive & Operations Dashboard
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/dashboard/summary` | Public / All | Get live summary of shipments, compliance %, violations, and fleet |

---

## 🚀 Setup & Execution Guide

### Prerequisites
- **Java**: JDK 17 or JDK 21 (LTS recommended)
- **Maven**: 3.8+
- **MySQL**: 8.0+ running on port `3306`

### Step 1: Clone or Navigate to Project
```bash
cd d:\JavaProjectReport
```

### Step 2: Configure Database
Ensure MySQL service is started. Create the database:
```sql
CREATE DATABASE coldchain_db;
```
Configure your username and password in `src/main/resources/application.properties` (defaults to `root`/`root`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coldchain_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### Step 3: Build & Compile
```bash
mvn clean compile
```

### Step 4: Run Application
```bash
mvn spring-boot:run
```
Upon startup, `DataInitializer` automatically runs and seeds:
- Default Admin Account: `admin` / `Admin@123`
- Default Operator Account: `operator` / `Operator@123`
- 2 Refrigerated Fleet Vehicles (`KA-01-EQ-1001`, `MH-12-RN-2002`)
- 1 Active Vaccine Consignment (`CC-DEMO-VACCINE-01`)
- 3 Telemetry Sensor Readings (including a breach reading demonstrating automatic alert creation)

### Step 5: Explore Interactive Swagger UI
Open your browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```
Click the **Authorize** button on the top right, enter `Bearer <your_token>`, and execute any API interactively!

---

## 💻 Sample API Usage (cURL & PowerShell)

### 1. Authenticate (Get JWT Token)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"Admin@123"}'
```

### 2. View Dashboard Summary
```bash
curl http://localhost:8080/api/v1/dashboard/summary
```
**Response Sample**:
```json
{
  "totalShipments": 1,
  "activeShipments": 1,
  "deliveredShipments": 0,
  "delayedShipments": 0,
  "violationsCount": 1,
  "unresolvedAlertsCount": 1,
  "safeCompliancePercentage": 66.67,
  "totalVehicles": 2,
  "availableVehicles": 1,
  "totalTemperatureLogs": 3,
  "recentAlerts": [
    {
      "id": 1,
      "shipmentId": 1,
      "trackingNumber": "CC-DEMO-VACCINE-01",
      "productName": "Pfizer-BioNTech mRNA COVID-19 Vaccine (5,000 Doses)",
      "recordedTemperature": 9.8,
      "breachType": "UPPER_LIMIT_EXCEEDED",
      "severity": "HIGH",
      "message": "AUTOMATED ALERT: High Temperature Breach! Recorded 9.80°C exceeds maximum safe threshold (8.00°C) by 1.80°C...",
      "resolved": false
    }
  ]
}
```

### 3. Log a Temperature Reading (Sensor Ingestion)
```bash
curl -X POST http://localhost:8080/api/v1/temperature-logs/1 \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "temperature": 11.2,
    "humidity": 68.0,
    "locationCoordinates": "15.364708, 75.123955"
  }'
```
*Because 11.2°C exceeds 8.0°C, an alert is automatically created and attached to shipment 1!*

### 4. Fetch Triggered Alerts
```bash
curl http://localhost:8080/api/v1/alerts/1 \
  -H "Authorization: Bearer <TOKEN>"
```

### 5. Resolve Alert (Admin Action)
```bash
curl -X PATCH "http://localhost:8080/api/v1/alerts/1/resolve?notes=Auxiliary+compressor+started" \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 🎯 Viva & Evaluation Defense Preparation

### 1. How the System Works (Simple Explanation for Viva)
> "The Cold-Chain Compliance Tracker provides end-to-end monitoring for sensitive cargo like vaccines and dairy products.
> 
> 1. An administrator or operator registers transport vehicles and creates a shipment specifying its origin, destination, and acceptable temperature range (typically 2°C to 8°C).
> 2. As the vehicle travels, IoT telematics sensors log temperature, humidity, and GPS coordinates via our REST API.
> 3. The service layer performs real-time validation: if the reading is within 2°C–8°C, it records a compliant log; if it breaches the upper or lower boundary, the system instantly generates an automated Alert, classifies its severity (Warning, High, Critical), links it to the shipment, and notifies operations.
> 4. The dashboard aggregates these events into key metrics: compliance rate %, active shipments, active alerts, and fleet status."

### 2. Real-World Use Cases
- **Pharmaceutical Supply Chains**: Distributing temperature-sensitive vaccines (e.g., Pfizer-BioNTech, Covaxin, MMR) and insulin that lose potency if exposed to ambient heat or freezing conditions.
- **Dairy & Perishable Logistics**: Transporting fresh milk, cheese, and meats where a temperature spike induces bacterial growth and renders products hazardous.
- **Organs & Bio-Specimens**: Hospital inter-facility transfer of blood platelets, human organs, and lab specimens requiring strict cryogenic or chilled parameters.
- **Regulatory Compliance (FDA / WHO / GDP)**: Provides verifiable electronic audit trails demanded by regulatory bodies (such as FDA 21 CFR Part 11 and WHO Good Distribution Practices).

### 3. High-Value Future Improvements
1. **MQTT / Kafka Ingestion**: Integrate an Apache Kafka or MQTT broker for high-throughput streaming from thousands of vehicle telematics devices concurrently.
2. **Push Notifications & SMS/WhatsApp Gateway**: Integrate Twilio or Firebase Cloud Messaging (FCM) to send instant SMS/WhatsApp alerts directly to drivers when a breach occurs.
3. **Predictive Temperature Spikes (AI/ML)**: Implement time-series machine learning models to forecast compressor failure 30 minutes before the temperature breaches 8°C.
4. **Geofencing & ETA recalculation**: Integrate OpenStreetMap / Google Maps API to track reefer delays and re-route around traffic jams.
5. **Blockchain Audit Trail**: Store temperature violation hashes on a private ledger for tamper-proof regulatory audits and insurance claims.
