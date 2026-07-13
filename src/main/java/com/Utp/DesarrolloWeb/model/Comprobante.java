package com.Utp.DesarrolloWeb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comprobantes")
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Long idComprobante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    @Column(name = "tipo_comprobante", length = 20, nullable = false)
    private String tipoComprobante;

    @Column(name = "serie_numero", length = 50, nullable = false, unique = true)
    private String serieNumero;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision = LocalDate.now();

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    @Column(name = "igv", nullable = false)
    private Double igv;

    @Column(name = "total", nullable = false)
    private Double total;
}
