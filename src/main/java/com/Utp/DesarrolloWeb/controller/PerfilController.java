package com.Utp.DesarrolloWeb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.Utp.DesarrolloWeb.dto.PasswordUpdateRequest;
import com.Utp.DesarrolloWeb.dto.PerfilUpdateRequest;
import com.Utp.DesarrolloWeb.dto.ContactoAdminDTO;
import com.Utp.DesarrolloWeb.model.Usuario;
import com.Utp.DesarrolloWeb.service.PerfilService;

@RestController
@RequestMapping("/api/perfil")
@CrossOrigin(origins = "*")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public ResponseEntity<Usuario> obtenerMiPerfil() {
        return ResponseEntity.ok(perfilService.obtenerMiPerfil());
    }

    @PutMapping
    public ResponseEntity<Usuario> actualizarPerfil(@RequestBody PerfilUpdateRequest request) {
        return ResponseEntity.ok(perfilService.actualizarPerfil(request));
    }

    @PutMapping("/password")
    public ResponseEntity<String> cambiarPassword(@Valid @RequestBody PasswordUpdateRequest request) {
        perfilService.cambiarPassword(request);
        return ResponseEntity.ok("Contraseña actualizada con éxito");
    }

    @GetMapping("/contacto-admin")
    public ResponseEntity<ContactoAdminDTO> obtenerContactoAdmin() {
        return ResponseEntity.ok(perfilService.obtenerContactoAdmin());
    }

    @GetMapping("/repartidores")
    @PreAuthorize("hasAnyAuthority('VENTAS', 'ADMINISTRADOR')")
    public ResponseEntity<java.util.List<Usuario>> listarRepartidores() {
        return ResponseEntity.ok(perfilService.listarRepartidores());
    }

    @GetMapping("/ventas")
    @PreAuthorize("hasAnyAuthority('REPARTIDOR', 'ADMINISTRADOR')")
    public ResponseEntity<java.util.List<Usuario>> listarVendedores() {
        return ResponseEntity.ok(perfilService.listarVentas());
    }
}
