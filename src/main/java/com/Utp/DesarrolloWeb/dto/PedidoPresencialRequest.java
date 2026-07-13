package com.Utp.DesarrolloWeb.dto;

import com.Utp.DesarrolloWeb.model.Pedido;

public class PedidoPresencialRequest {
    private String emailCliente;
    private String dniCliente;
    private String nombreCliente;
    private Pedido pedido;
    private String metodoPago; // "EFECTIVO", "YAPE", "PLIN", "TARJETA"

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public String getDniCliente() { return dniCliente; }
    public void setDniCliente(String dniCliente) { this.dniCliente = dniCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}
