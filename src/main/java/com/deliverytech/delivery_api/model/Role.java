package com.deliverytech.delivery_api.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN, CLIENTE, RESTAURANTE, ENTREGADOR,USER;

    @Override
    public String getAuthority() {
        return name();
    }
}