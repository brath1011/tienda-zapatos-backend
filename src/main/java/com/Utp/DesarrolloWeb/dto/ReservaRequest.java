package com.Utp.DesarrolloWeb.dto;

import java.util.List;

public class ReservaRequest {
    private String emailCliente;
    private List<ReservaItem> detalles;

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public List<ReservaItem> getDetalles() { return detalles; }
    public void setDetalles(List<ReservaItem> detalles) { this.detalles = detalles; }

    public static class ReservaItem {
        private Long zapatoId;
        private String talla;
        private Integer cantidad;

        public Long getZapatoId() { return zapatoId; }
        public void setZapatoId(Long zapatoId) { this.zapatoId = zapatoId; }

        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}
