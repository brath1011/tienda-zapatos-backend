package com.Utp.DesarrolloWeb.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Utp.DesarrolloWeb.dto.AuthResponse;
import com.Utp.DesarrolloWeb.dto.LoginRequest;
import com.Utp.DesarrolloWeb.dto.RegisterRequest;
import com.Utp.DesarrolloWeb.model.Usuario;
import com.Utp.DesarrolloWeb.repository.UsuarioRepository;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public void registrar(RegisterRequest request) {
        java.util.Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(request.getEmail());
        
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            // Si el usuario existe pero no tiene contraseña, significa que es una cuenta "sombra" creada en tienda física
            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                usuario.setNombre(request.getNombre());
                usuario.setPassword(passwordEncoder.encode(request.getPassword()));
                if (request.getTipoDocumento() != null) usuario.setTipoDocumento(request.getTipoDocumento());
                if (request.getNumeroDocumento() != null) usuario.setNumeroDocumento(request.getNumeroDocumento());
                if (request.getTelefono() != null) usuario.setTelefono(request.getTelefono());
                usuarioRepository.save(usuario);
                return;
            } else {
                throw new RuntimeException("El correo ya está registrado");
            }
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(com.Utp.DesarrolloWeb.model.Rol.CLIENTE);
        usuario.setTipoDocumento(request.getTipoDocumento());
        usuario.setNumeroDocumento(request.getNumeroDocumento());
        usuario.setTelefono(request.getTelefono());
        usuarioRepository.save(usuario);
    }

    public void registrarUsuarioAdmin(com.Utp.DesarrolloWeb.dto.UsuarioAdminRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        if (request.getZona() != null && !request.getZona().isEmpty()) {
            usuario.setZona(request.getZona());
        }
        usuarioRepository.save(usuario);
    }

    public java.util.List<Usuario> listarTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            System.out.println("--- INTENTO DE LOGIN ---");
            System.out.println("Buscando usuario: " + request.getEmail());
            
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

            if (usuario.getActivo() != null && !usuario.getActivo()) {
                throw new RuntimeException("Cuenta suspendida, consulte con el administrador");
            }

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            System.out.println("¡Autenticación exitosa! Generando token...");
            String token = jwtService.generarToken(authentication);
            String rol = authentication.getAuthorities().iterator().next().getAuthority();
            
            return new AuthResponse(token, "Bearer", authentication.getName(), rol, usuario.getZona());
            
        } catch (RuntimeException e) {
            System.out.println("❌ ERROR AL INICIAR SESIÓN: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("❌ ERROR FATAL AL INICIAR SESIÓN: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Login fallido");
        }
    }

    public void cambiarEstadoUsuario(Long idUsuario, boolean estado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(estado);
        usuarioRepository.save(usuario);
    }

    public void actualizarZona(Long idUsuario, String zona) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setZona(zona);
        usuarioRepository.save(usuario);
    }
}