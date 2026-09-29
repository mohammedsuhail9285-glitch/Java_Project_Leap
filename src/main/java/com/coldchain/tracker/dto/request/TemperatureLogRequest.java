package com.coldchain.tracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Payload sent by IoT telematics devices or field operators logging ambient/cargo temperatures.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemperatureLogRequest {

    @NotNull(message = "Temperature reading cannot be null")
    @Schema(example = "4.5", description = "Current temperature reading in Celsius")
    private Double temperature;

    @Schema(example = "62.5", description = "Relative humidity percentage")
    private Double humidity;

    @Schema(example = "17.385044, 78.486671", description = "GPS coordinates (Latitude, Longitude)")
    private String locationCoordinates;

    @Schema(example = "2026-09-27T16:30:00", description = "Timestamp of reading (optional, defaults to current server time)")
    private LocalDateTime timestamp;
}
