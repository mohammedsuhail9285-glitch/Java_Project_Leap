package com.coldchain.tracker.dto.response;

import com.coldchain.tracker.entity.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Public profile response for a registered user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private LocalDateTime createdAt;
}
