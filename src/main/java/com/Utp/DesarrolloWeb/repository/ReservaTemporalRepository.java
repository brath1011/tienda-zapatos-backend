package com.Utp.DesarrolloWeb.repository;

import com.Utp.DesarrolloWeb.model.ReservaTemporal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaTemporalRepository extends JpaRepository<ReservaTemporal, Long> {
    List<ReservaTemporal> findByExpiraEnBefore(LocalDateTime dateTime);
}
