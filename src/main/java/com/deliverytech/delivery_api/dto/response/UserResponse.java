package com.deliverytech.delivery_api.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private String message;
    private String email;
    private String nome;
    private String role;
    
    // Método temporário até implementar corretamente
    public static UserResponse fromEntity(Object usuario) {
        UserResponse response = new UserResponse();
        response.setMessage("Usuário criado com sucesso");
        return response;
    }
}



