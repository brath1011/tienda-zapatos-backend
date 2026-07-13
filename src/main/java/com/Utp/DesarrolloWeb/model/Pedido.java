package com.Utp.DesarrolloWeb.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido", nullable = false)
    private Long idPedido;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDate fecha = LocalDate.now();

    @Column(name = "fecha_entrega")
    private java.time.LocalDateTime fechaEntrega;

    @Column(name = "total_pagar", nullable = false)
    private Double total;

    @Column(length = 50)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_direccion")
    private Direccion direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_repartidor")
    private Usuario repartidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor")
    private Usuario vendedor;

    @Column(name = "tipo_pedido", length = 20)
    private String tipoPedido = "WEB";

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles = new ArrayList<>();
    
    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Comprobante comprobante;
    public Double getTotal() {
    return total;
}

public void setTotal(Double total) {
    this.total = total;
}
}