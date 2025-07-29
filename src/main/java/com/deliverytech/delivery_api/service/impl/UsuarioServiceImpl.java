package com.deliverytech.delivery_api.service.impl;

import com.deliverytech.delivery_api.dto.request.LoginRequest;
import com.deliverytech.delivery_api.dto.request.RegisterRequest;
import com.deliverytech.delivery_api.dto.response.LoginResponse;
import com.deliverytech.delivery_api.service.UsuarioService;
import com.deliverytech.delivery_api.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );

            String token = jwtUtil.generateToken(authentication.getName());
            
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(authentication.getName());
            response.setMessage("Login realizado com sucesso");
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Credenciais inválidas", e);
        }
    }

    @Override
    public String register(RegisterRequest registerRequest) {
        try {
            // Validação básica
            if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Email é obrigatório");
            }
            
            // Log para debug
            System.out.println("Registrando usuário: " + registerRequest.getEmail());
            
            // Por enquanto, apenas simula o registro
            return "Usuário " + registerRequest.getNome() + " registrado com sucesso!";
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    public void logout(String token) {
        // Implementar lógica de logout se necessário
        // Por exemplo, adicionar token a uma blacklist
    }

    public Object buscarPorId(Long id) {
        // TODO: Implementar busca de usuário por ID
        // Por enquanto, retorna null ou lança exceção
        throw new UnsupportedOperationException("Método buscarPorId ainda não implementado");
    }

    // Implementar outros métodos que possam estar na interface UsuarioService
    // Adicione conforme necessário baseado na interface existente

    public void inativarUsuario(Long id) {
        // TODO: Implementar lógica para inativar usuário
        throw new UnsupportedOperationException("Método inativarUsuario ainda não implementado");
    }

    public boolean existePorEmail(String email) {
        // TODO: Implementar lógica para verificar existência por email
        throw new UnsupportedOperationException("Método existePorEmail ainda não implementado");
    }

    public Object buscarPorEmail(String email) {
        // TODO: Implementar lógica para buscar usuário por email
        throw new UnsupportedOperationException("Método buscarPorEmail ainda não implementado");
    }

    public Object salvar(RegisterRequest registerRequest) {
        // TODO: Implementar lógica para salvar usuário
        throw new UnsupportedOperationException("Método salvar ainda não implementado");
    }
}
