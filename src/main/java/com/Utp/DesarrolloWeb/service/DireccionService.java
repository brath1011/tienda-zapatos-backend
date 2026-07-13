package com.Utp.DesarrolloWeb.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Utp.DesarrolloWeb.model.Direccion;
import com.Utp.DesarrolloWeb.model.Usuario;
import com.Utp.DesarrolloWeb.repository.DireccionRepository;
import com.Utp.DesarrolloWeb.repository.UsuarioRepository;

@Service
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    public DireccionService(DireccionRepository direccionRepository, UsuarioRepository usuarioRepository) {
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public java.util.List<Direccion> listarMisDirecciones() {
        return direccionRepository.findByUsuario(getUsuarioAutenticado()).stream()
                .filter(d -> d.getActivo() == null || d.getActivo())
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public Direccion agregarDireccion(Direccion direccion) {
        Usuario usuario = getUsuarioAutenticado();
        direccion.setUsuario(usuario);
        direccion.setActivo(true);
        return direccionRepository.save(direccion);
    }

    @Transactional
    public Direccion actualizarDireccion(Long id, Direccion direccionActualizada) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        
        Usuario usuario = getUsuarioAutenticado();
        if (!direccion.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para editar esta dirección");
        }

        direccion.setDireccionExacta(direccionActualizada.getDireccionExacta());
        direccion.setReferencia(direccionActualizada.getReferencia());
        direccion.setDistrito(direccionActualizada.getDistrito());
        return direccionRepository.save(direccion);
    }

    @Transactional
    public void eliminarDireccion(Long id) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        
        Usuario usuario = getUsuarioAutenticado();
        if (!direccion.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar esta dirección");
        }

        // En lugar de borrar de la base de datos (lo cual falla si hay pedidos con esta dirección),
        // realizamos un borrado lógico (soft-delete).
        direccion.setActivo(false);
        direccionRepository.save(direccion);
    }
}
