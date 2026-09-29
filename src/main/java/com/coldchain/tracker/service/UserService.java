package com.coldchain.tracker.service;

import com.coldchain.tracker.dto.request.UserLoginRequest;
import com.coldchain.tracker.dto.request.UserRegisterRequest;
import com.coldchain.tracker.dto.response.AuthResponse;
import com.coldchain.tracker.dto.response.UserResponse;

import java.util.List;

/**
 * Service interface for User registration, authentication, and directory lookup.
 */
public interface UserService {

    UserResponse registerUser(UserRegisterRequest request);

    AuthResponse loginUser(UserLoginRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();
}
