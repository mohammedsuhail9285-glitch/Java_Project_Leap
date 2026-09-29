package com.coldchain.tracker.dto.request;

import com.coldchain.tracker.entity.enums.ShipmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Payload for transitioning a shipment's lifecycle status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentStatusUpdateRequest {

    @NotNull(message = "Shipment status cannot be null")
    @Schema(example = "IN_TRANSIT", allowableValues = {"CREATED", "IN_TRANSIT", "DELIVERED", "DELAYED"})
    private ShipmentStatus status;

    @Schema(example = "Shipment departed warehouse gateway. Chiller unit active.")
    private String remarks;
}
