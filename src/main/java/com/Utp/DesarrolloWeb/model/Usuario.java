package com.Utp.DesarrolloWeb.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nombre_completo", nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 20, unique = true)
    private String numeroDocumento;

    @Column(length = 20)
    private String telefono;

    @Column(name = "email_contacto")
    private String emailContacto;

    @Column(name = "telefono_secundario", length = 20)
    private String telefonoSecundario;

    @Column(length = 50)
    private String zona;

    public Usuario() {}

    // --- GETTERS Y SETTERS ---

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }

    public String getTelefonoSecundario() { return telefonoSecundario; }
    public void setTelefonoSecundario(String telefonoSecundario) { this.telefonoSecundario = telefonoSecundario; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}