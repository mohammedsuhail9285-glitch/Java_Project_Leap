package com.coldchain.tracker.dto.request;

import com.coldchain.tracker.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Payload for registering a new user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(example = "john_operator")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Schema(example = "john@coldchain.com")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(example = "SecurePass123!")
    private String password;

    @NotBlank(message = "Full name cannot be blank")
    @Schema(example = "John Doe")
    private String fullName;

    @NotNull(message = "Role must be specified (ADMIN or OPERATOR)")
    @Schema(example = "OPERATOR")
    private Role role;
}
