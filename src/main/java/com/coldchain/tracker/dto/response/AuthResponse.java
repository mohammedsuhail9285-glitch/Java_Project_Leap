package com.coldchain.tracker.dto.response;

import com.coldchain.tracker.entity.enums.Role;
import lombok.*;

/**
 * Authentication response returning JWT access token and logged-in user profile.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
}
