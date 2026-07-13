package com.Utp.DesarrolloWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Utp.DesarrolloWeb.model.Direccion;
import com.Utp.DesarrolloWeb.model.Usuario;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    java.util.List<Direccion> findByUsuario(Usuario usuario);
}
