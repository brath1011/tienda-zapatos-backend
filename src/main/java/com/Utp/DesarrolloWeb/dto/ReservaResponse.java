package com.Utp.DesarrolloWeb.dto;

public class ReservaResponse {
    private Long reservaId;
    private String mensaje;

    public ReservaResponse(Long reservaId, String mensaje) {
        this.reservaId = reservaId;
        this.mensaje = mensaje;
    }

    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
