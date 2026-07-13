package com.Utp.DesarrolloWeb.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "reserva_temporal_detalle")
public class ReservaTemporalDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    @JsonIgnore
    private ReservaTemporal reserva;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "talla")
    private String talla;

    @Column(name = "cantidad")
    private Integer cantidad;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ReservaTemporal getReserva() { return reserva; }
    public void setReserva(ReservaTemporal reserva) { this.reserva = reserva; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
