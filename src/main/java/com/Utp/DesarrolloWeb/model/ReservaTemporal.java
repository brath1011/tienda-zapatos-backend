package com.Utp.DesarrolloWeb.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "reserva_temporal")
public class ReservaTemporal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_cliente")
    private String emailCliente;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "expira_en")
    private LocalDateTime expiraEn;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaTemporalDetalle> detalles = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getExpiraEn() { return expiraEn; }
    public void setExpiraEn(LocalDateTime expiraEn) { this.expiraEn = expiraEn; }

    public List<ReservaTemporalDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<ReservaTemporalDetalle> detalles) { this.detalles = detalles; }
    
    public void addDetalle(ReservaTemporalDetalle detalle) {
        detalles.add(detalle);
        detalle.setReserva(this);
    }
}
