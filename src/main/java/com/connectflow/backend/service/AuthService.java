package com.connectflow.backend.service;

import com.connectflow.backend.dto.LoginRequest;
import com.connectflow.backend.dto.LoginResponse;
import com.connectflow.backend.dto.RegisterRequest;
import com.connectflow.backend.entity.LoginLogoutLog;

import java.net.InetAddress;
import java.util.List;

public interface AuthService {
    LoginResponse login(LoginRequest request, InetAddress ipAddress);
    LoginResponse register(RegisterRequest request, InetAddress ipAddress);
    void logout(String email, InetAddress ipAddress);
    List<LoginLogoutLog> getAuthLogs();
}
