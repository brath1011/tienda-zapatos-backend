package com.Utp.DesarrolloWeb.repository;

import com.Utp.DesarrolloWeb.model.Campana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampanaRepository extends JpaRepository<Campana, Long> {

    @Query("SELECT c FROM Campana c WHERE c.activa = true AND :fechaActual BETWEEN c.fechaInicio AND c.fechaFin")
    List<Campana> findCampanasActivasHoy(@Param("fechaActual") LocalDate fechaActual);
}
