package com.Utp.DesarrolloWeb.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Utp.DesarrolloWeb.dto.PasswordUpdateRequest;
import com.Utp.DesarrolloWeb.dto.PerfilUpdateRequest;
import com.Utp.DesarrolloWeb.dto.ContactoAdminDTO;
import com.Utp.DesarrolloWeb.model.Usuario;
import com.Utp.DesarrolloWeb.repository.UsuarioRepository;

@Service
public class PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario obtenerMiPerfil() {
        return getUsuarioAutenticado();
    }

    @Transactional
    public Usuario actualizarPerfil(PerfilUpdateRequest request) {
        Usuario usuario = getUsuarioAutenticado();
        if (request.getNombre() != null) usuario.setNombre(request.getNombre());
        if (request.getTipoDocumento() != null) usuario.setTipoDocumento(request.getTipoDocumento());
        if (request.getNumeroDocumento() != null) usuario.setNumeroDocumento(request.getNumeroDocumento());
        if (request.getTelefono() != null) usuario.setTelefono(request.getTelefono());
        if (request.getEmailContacto() != null) usuario.setEmailContacto(request.getEmailContacto());
        if (request.getTelefonoSecundario() != null) usuario.setTelefonoSecundario(request.getTelefonoSecundario());
        
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarPassword(PasswordUpdateRequest request) {
        Usuario usuario = getUsuarioAutenticado();
        
        // Verificar que la contraseña actual es correcta
        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        
        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public ContactoAdminDTO obtenerContactoAdmin() {
        Usuario admin = usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == com.Utp.DesarrolloWeb.model.Rol.ADMINISTRADOR)
                .findFirst()
                .orElse(null);
                
        if (admin == null) {
            return new ContactoAdminDTO("Administrador UTP", "admin@utpfashion.com", "admin@gmail.com", "999999999", "999999998");
        }
        
        return new ContactoAdminDTO(
            admin.getNombre(),
            admin.getEmail(),
            admin.getEmailContacto(),
            admin.getTelefono(),
            admin.getTelefonoSecundario()
        );
    }

    @Transactional(readOnly = true)
    public java.util.List<Usuario> listarRepartidores() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == com.Utp.DesarrolloWeb.model.Rol.REPARTIDOR)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public java.util.List<Usuario> listarVentas() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == com.Utp.DesarrolloWeb.model.Rol.VENTAS)
                .collect(java.util.stream.Collectors.toList());
    }
}
