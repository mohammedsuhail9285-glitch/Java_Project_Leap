package com.coldchain.tracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Payload for authenticating a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {

    @NotBlank(message = "Username or email is required")
    @Schema(example = "admin@coldchain.com")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    @Schema(example = "Admin@123")
    private String password;
}
