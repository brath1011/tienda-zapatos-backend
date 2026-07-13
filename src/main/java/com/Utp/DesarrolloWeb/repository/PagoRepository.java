package com.Utp.DesarrolloWeb.repository;

import com.Utp.DesarrolloWeb.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    void deleteByPedido(com.Utp.DesarrolloWeb.model.Pedido pedido);
    java.util.List<Pago> findByPedido(com.Utp.DesarrolloWeb.model.Pedido pedido);
}
