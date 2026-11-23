package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.dto.LoginRequest;
import com.connectflow.backend.dto.LoginResponse;
import com.connectflow.backend.dto.RegisterRequest;
import com.connectflow.backend.entity.LoginLogoutLog;
import com.connectflow.backend.security.IpAddressResolver;
import com.connectflow.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest servletRequest) {
        LoginResponse response = authService.register(request, ipAddressResolver.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success("Account created successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestBody(required = false) Map<String, String> body,
            HttpServletRequest servletRequest,
            Principal principal) {
        String email = principal != null ? principal.getName() : (body != null ? body.get("email") : null);
        authService.logout(email, ipAddressResolver.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success("Logout successful", "Logged out successfully"));
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<LoginLogoutLog>>> getAuthLogs() {
        return ResponseEntity.ok(ApiResponse.success("Auth logs retrieved", authService.getAuthLogs()));
    }
}
