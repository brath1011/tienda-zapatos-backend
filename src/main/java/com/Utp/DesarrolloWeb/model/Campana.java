package com.Utp.DesarrolloWeb.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "campanas_promocionales")
public class Campana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_campana")
    private Long idCampana;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "porcentaje_descuento", nullable = false)
    private Double porcentajeDescuento;

    @Column(name = "filtro_modelo")
    private String filtroModelo;

    @Column(name = "filtro_marca")
    private String filtroMarca;

    @Column(name = "filtro_color")
    private String filtroColor;

    @Column(name = "sin_reembolso", columnDefinition = "boolean default false")
    private Boolean sinReembolso = false;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean activa = true;

    // Getters y Setters
    public Long getIdCampana() { return idCampana; }
    public void setIdCampana(Long idCampana) { this.idCampana = idCampana; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(Double porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }

    public String getFiltroMarca() { return filtroMarca; }
    public void setFiltroMarca(String filtroMarca) { this.filtroMarca = filtroMarca; }

    public String getFiltroColor() { return filtroColor; }
    public void setFiltroColor(String filtroColor) { this.filtroColor = filtroColor; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public String getFiltroModelo() { return filtroModelo; }
    public void setFiltroModelo(String filtroModelo) { this.filtroModelo = filtroModelo; }

    public Boolean getSinReembolso() { return sinReembolso; }
    public void setSinReembolso(Boolean sinReembolso) { this.sinReembolso = sinReembolso; }
}
