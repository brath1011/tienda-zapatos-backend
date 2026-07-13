package com.Utp.DesarrolloWeb.dto;

import com.Utp.DesarrolloWeb.model.Pedido;

public class CompraWebRequest {
    
    private Pedido pedido;
    private String metodoPago; // "TARJETA", "YAPE", "PLIN"
    private String numeroTarjeta;
    private String cvv;
    private Long reservaId;

    // Getters y Setters
    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }

    // Getters y Setters
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    private String departamento;
    private String provincia;
    private String distrito;
    private String calleJiron;
    private String numero;
    private String dptoInterior;

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public String getCalleJiron() { return calleJiron; }
    public void setCalleJiron(String calleJiron) { this.calleJiron = calleJiron; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getDptoInterior() { return dptoInterior; }
    public void setDptoInterior(String dptoInterior) { this.dptoInterior = dptoInterior; }
}
