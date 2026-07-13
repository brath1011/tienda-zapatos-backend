package com.Utp.DesarrolloWeb.repository;

import com.Utp.DesarrolloWeb.model.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
}
