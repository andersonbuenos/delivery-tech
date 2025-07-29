package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.LoginRequest;
import com.deliverytech.delivery_api.dto.request.RegisterRequest;
import com.deliverytech.delivery_api.dto.response.LoginResponse;

public interface UsuarioService {
    LoginResponse login(LoginRequest loginRequest);
    String register(RegisterRequest registerRequest);
}