package com.tlfdt.bonrecreme.service.auth;

import com.tlfdt.bonrecreme.controller.api.v1.auth.dto.LoginRequest;
import com.tlfdt.bonrecreme.controller.api.v1.auth.dto.RegisterRequest;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.user.Role;
import com.tlfdt.bonrecreme.model.user.User;
import com.tlfdt.bonrecreme.repository.user.UserRepository;
import com.tlfdt.bonrecreme.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public String register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new CustomExceptionHandler("Username is already taken!");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .realName(registerRequest.getRealName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.MANAGER)
                .build();

        userRepository.save(user);

        return "User registered successfully!";
    }
}