package com.Utp.DesarrolloWeb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Column(name = "metodo_pago", length = 50, nullable = false)
    private String metodoPago;

    @Column(name = "codigo_transaccion", length = 100)
    private String codigoTransaccion;

    @Column(name = "estado_pago", length = 50, nullable = false)
    private String estadoPago;
}
