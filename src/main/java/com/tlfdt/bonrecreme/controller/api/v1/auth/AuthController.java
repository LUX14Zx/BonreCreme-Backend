package com.tlfdt.bonrecreme.controller.api.v1.auth;

import com.tlfdt.bonrecreme.controller.api.v1.auth.dto.LoginRequest;
import com.tlfdt.bonrecreme.controller.api.v1.auth.dto.RegisterRequest;
import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated // Enables validation for path variables and request parameters.
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponseDTO.success(token, "Login successful."));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String response = authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponseDTO.success(response, "Registration successful."));
    }
}