package com.deliverytech.delivery_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.dto.request.LoginRequest;
import com.deliverytech.delivery_api.dto.request.RegisterRequest;
import com.deliverytech.delivery_api.dto.response.LoginResponse;
import com.deliverytech.delivery_api.dto.response.UserResponse;
import com.deliverytech.delivery_api.model.Usuario;
import com.deliverytech.delivery_api.security.JwtUtil;
import com.deliverytech.delivery_api.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Operações relacionadas com autenticação e cadastro de usuários")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login de usuário", description = "Realizar o login do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Login inválido", content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("=== DEBUG LOGIN ===");
            System.out.println("Username: " + request.getUsername());
            System.out.println("Password: " + request.getPassword());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            System.out.println("Autenticação bem-sucedida para: " + authentication.getName());
            
            String username = authentication.getName();
            String token = jwtUtil.generateToken(username); // TROCAR para generateToken

            LoginResponse dto = new LoginResponse(token);
            return ResponseEntity.ok(dto);
            
        } catch (BadCredentialsException e) {
            System.out.println("Erro de credenciais: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.out.println("Erro geral: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar um usuário", description = "Cadastrar um novo usuário na plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Registro inválido", content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        try {
            Object resultado = usuarioService.register(request); // TROCAR salvar para register
            
            // Por enquanto, retornar resposta simples
            UserResponse response = new UserResponse(); // Criar construtor simples
            response.setMessage("Usuário registrado com sucesso");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.out.println("Erro no register: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
