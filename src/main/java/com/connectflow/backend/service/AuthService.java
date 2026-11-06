package com.connectflow.backend.service;

import com.connectflow.backend.dto.LoginRequest;
import com.connectflow.backend.dto.LoginResponse;

import java.net.InetAddress;

public interface AuthService {
    LoginResponse login(LoginRequest request, InetAddress ipAddress);
}
