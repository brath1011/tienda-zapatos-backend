package com.Utp.DesarrolloWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.Utp.DesarrolloWeb.model.Producto;

// [CAPA DE DATOS - SPRING DATA JPA]: Provee abstracción para no escribir el CRUD en SQL directamente.
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // 1. [CONSULTAS HEREDADAS]: JpaRepository ya gestiona internamente: save(), findById(), findAll(), deleteById()


}