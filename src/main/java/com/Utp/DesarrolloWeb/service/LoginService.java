package com.Utp.DesarrolloWeb.service;

import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public boolean autenticarUsuario(String username, String password) {
        // Lógica de negocio: validación de credenciales
        // Credenciales fijas
        return "adminZapatos".equals(username) && "clave123".equals(password);
    }
}