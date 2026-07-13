package com.Utp.DesarrolloWeb.dto;

public class AuthResponse {
    
    private String token;
    private String tipo;
    private String email;
    private String rol;
    private String zona;

    public AuthResponse(String token, String tipo, String email, String rol, String zona) {
        this.token = token;
        this.tipo = tipo;
        this.email = email;
        this.rol = rol;
        this.zona = zona;
    }

    // --- GETTERS --- (En las respuestas usualmente solo enviamos datos, no los modificamos)

    public String getToken() { return token; }
    public String getTipo() { return tipo; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public String getZona() { return zona; }
}