package com.Utp.DesarrolloWeb.repository;

import com.Utp.DesarrolloWeb.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Carrito findByProductoId(Long productoId); 
}