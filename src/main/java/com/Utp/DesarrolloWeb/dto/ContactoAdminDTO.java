package com.Utp.DesarrolloWeb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactoAdminDTO {
    private String nombre;
    private String email;          // Email de login
    private String emailContacto;  // Gmail personal
    private String telefono;       // Celular personal
    private String telefonoSecundario; // Celular de la empresa
}
