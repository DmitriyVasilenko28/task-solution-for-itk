package org.example.service;

import org.example.dto.AuthResponse;
import org.example.dto.NewUserRequestDto;
import org.example.dto.LoginRequest;

public interface AuthService {

    void registration(NewUserRequestDto user);

    AuthResponse login(LoginRequest request);
}
