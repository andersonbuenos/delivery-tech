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
            // MUDANÇA: usar username e password em vez de email e senha
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // Se a autenticação for bem-sucedida, gera um token JWT
            String username = authentication.getName();
            String token = jwtUtil.gerarToken(username); // MUDANÇA: usar gerarToken em vez de generateToken

            LoginResponse dto = new LoginResponse(token);
            return ResponseEntity.ok(dto);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar um usuário", description = "Cadastrar um novo usuário na plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Registro inválido", content = @Content(schema = @Schema(implementation = Void.class))),
    })
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        Usuario novoUsuario = usuarioService.salvar(request);
        UserResponse response = UserResponse.fromEntity(novoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}