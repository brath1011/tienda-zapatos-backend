package com.Utp.DesarrolloWeb.service;

import org.springframework.stereotype.Service;

@Service
public class SimuladorPagoService {

    public boolean procesarPago(String metodo, String numeroTarjeta, String cvv) {
        if (metodo == null || metodo.trim().isEmpty()) {
            throw new RuntimeException("Método de pago no especificado.");
        }

        String metodoNormalizado = metodo.toUpperCase();

        if (metodoNormalizado.equals("TARJETA")) {
            // Removemos espacios en blanco antes de validar
            String tarjetaSinEspacios = numeroTarjeta != null ? numeroTarjeta.replaceAll("\\s+", "") : null;
            
            if (tarjetaSinEspacios == null || !tarjetaSinEspacios.matches("\\d{16}")) {
                throw new RuntimeException("Pago rechazado: El número de tarjeta debe tener 16 dígitos.");
            }
            if (cvv == null || !cvv.matches("\\d{3}")) {
                throw new RuntimeException("Pago rechazado: El CVV debe tener 3 dígitos.");
            }
            System.out.println("💳 [SIMULACIÓN] Validando tarjeta terminada en " + tarjetaSinEspacios.substring(12) + "...");
            System.out.println("✅ [SIMULACIÓN] Cobro con TARJETA aprobado exitosamente.");
            return true;
        } else if (metodoNormalizado.equals("YAPE") || metodoNormalizado.equals("PLIN") || metodoNormalizado.equals("EFECTIVO")) {
            System.out.println("💸 [SIMULACIÓN] Cobro validado vía " + metodoNormalizado + ".");
            return true;
        } else {
            throw new RuntimeException("Método de pago no soportado: " + metodoNormalizado);
        }
    }
}
