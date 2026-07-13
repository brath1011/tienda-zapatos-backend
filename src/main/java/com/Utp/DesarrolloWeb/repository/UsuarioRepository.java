package com.Utp.DesarrolloWeb.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Utp.DesarrolloWeb.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
    boolean existsByEmail(String email);
    java.util.List<Usuario> findByRol(com.Utp.DesarrolloWeb.model.Rol rol);
}