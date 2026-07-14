package com.Utp.DesarrolloWeb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que gestiona las conexiones SSE activas de los repartidores.
 * Mantiene un registro de SseEmitter por email de repartidor.
 * Cuando Ventas asigna un pedido, este servicio notifica en tiempo real.
 */
@Service
public class SseService {

    // Registro de emitters activos: email del repartidor -> SseEmitter
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Registra una nueva conexión SSE para un repartidor.
     * El timeout en 0L = sin límite de tiempo (la conexión vive hasta que el
     * repartidor cierra la app o hay un error de red).
     */
    public SseEmitter suscribir(String emailRepartidor) {
        // Si ya tiene una conexión previa abierta, la cerramos
        SseEmitter anterior = emitters.get(emailRepartidor);
        if (anterior != null) {
            anterior.complete();
        }

        SseEmitter emitter = new SseEmitter(0L); // Sin timeout

        // Cuando el repartidor cierra el navegador o pierde señal
        emitter.onCompletion(() -> emitters.remove(emailRepartidor));
        emitter.onTimeout(() -> emitters.remove(emailRepartidor));
        emitter.onError(e -> emitters.remove(emailRepartidor));

        emitters.put(emailRepartidor, emitter);

        // Enviar evento de bienvenida para confirmar la conexión
        try {
            emitter.send(SseEmitter.event().name("conectado").data("Conexión establecida"));
        } catch (IOException e) {
            emitters.remove(emailRepartidor);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * Empuja una notificación de "nuevo pedido asignado" al repartidor específico.
     * Si el repartidor no está conectado (app cerrada), simplemente se ignora.
     */
    public void notificarNuevoPedido(String emailRepartidor) {
        SseEmitter emitter = emitters.get(emailRepartidor);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event()
                    .name("nuevo-pedido")
                    .data("Tienes un nuevo pedido asignado"));
        } catch (IOException e) {
            emitters.remove(emailRepartidor);
            emitter.completeWithError(e);
        }
    }
}
