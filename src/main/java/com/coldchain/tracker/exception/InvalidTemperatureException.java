package com.coldchain.tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when sensor temperature values fall outside realistic physical / telemetry boundaries.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTemperatureException extends RuntimeException {

    public InvalidTemperatureException(String message) {
        super(message);
    }

    public InvalidTemperatureException(Double temperature, Double minLimit, Double maxLimit) {
        super(String.format("Invalid temperature reading: %.2f°C is outside valid sensor operating range [%.2f°C to %.2f°C]. Check sensor calibration.",
                temperature, minLimit, maxLimit));
    }
}
