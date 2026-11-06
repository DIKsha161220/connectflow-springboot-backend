package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.dto.LoginRequest;
import com.connectflow.backend.dto.LoginResponse;
import com.connectflow.backend.security.IpAddressResolver;
import com.connectflow.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final IpAddressResolver ipAddressResolver;

    public AuthController(AuthService authService, IpAddressResolver ipAddressResolver) {
        this.authService = authService;
        this.ipAddressResolver = ipAddressResolver;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest) {
        LoginResponse response = authService.login(request, ipAddressResolver.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
