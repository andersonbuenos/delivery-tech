package com.deliverytech.delivery_api.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import com.deliverytech.delivery_api.dto.request.LoginRequest;
import com.deliverytech.delivery_api.dto.request.RegisterRequest;
import com.deliverytech.delivery_api.dto.response.LoginResponse;
import com.deliverytech.delivery_api.model.Usuario;

public interface UsuarioService {

    Usuario salvar(RegisterRequest usuario);

    UserDetails buscarPorEmail(String email);

    boolean existePorEmail(String email);

    Object buscarPorId(Long id);

    void inativarUsuario(Long id);

    LoginResponse login(LoginRequest loginRequest);

    void logout(String token);
}