package com.tlfdt.bonrecreme.service.auth;

import com.tlfdt.bonrecreme.controller.api.v1.auth.dto.LoginRequest;
import com.tlfdt.bonrecreme.controller.api.v1.auth.dto.RegisterRequest;

public interface AuthService {
    String login(LoginRequest loginRequest);

    String register(RegisterRequest registerRequest);
}