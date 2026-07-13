package com.Utp.DesarrolloWeb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.Utp.DesarrolloWeb.dto.UsuarioAdminRequest;
import com.Utp.DesarrolloWeb.model.Usuario;
import com.Utp.DesarrolloWeb.service.AuthService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAuthority('ADMINISTRADOR')") // Toda la clase protegida para el Administrador
public class UsuarioController {

    private final AuthService authService;

    public UsuarioController(AuthService authService) {
        this.authService = authService;
    }

    // 1. CREAR EMPLEADO MANUALMENTE [ENDPOINT PRIVADO - POST]
    @PostMapping
    public ResponseEntity<String> registrarUsuarioAdmin(@Valid @RequestBody UsuarioAdminRequest request) {
        authService.registrarUsuarioAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario administrativo creado con éxito");
    }

    // 2. LISTAR TODOS LOS USUARIOS/EMPLEADOS [ENDPOINT PRIVADO - GET]
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodosLosUsuarios() {
        return ResponseEntity.ok(authService.listarTodosLosUsuarios());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<String> cambiarEstadoUsuario(@PathVariable Long id, @RequestParam boolean activo) {
        authService.cambiarEstadoUsuario(id, activo);
        String estadoMsg = activo ? "reactivado" : "suspendido";
        return ResponseEntity.ok("Usuario " + estadoMsg + " exitosamente");
    }

    // 4. ACTUALIZAR ZONA DE REPARTIDOR [ENDPOINT PRIVADO - PUT]
    @PutMapping("/{id}/zona")
    public ResponseEntity<String> actualizarZona(@PathVariable Long id, @RequestParam String zona) {
        authService.actualizarZona(id, zona);
        return ResponseEntity.ok("Zona actualizada correctamente");
    }
}
