package com.Utp.DesarrolloWeb.scheduler;

import com.Utp.DesarrolloWeb.model.ReservaTemporal;
import com.Utp.DesarrolloWeb.repository.ReservaTemporalRepository;
import com.Utp.DesarrolloWeb.service.PedidoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservaScheduler {

    private final ReservaTemporalRepository reservaRepository;
    private final PedidoService pedidoService;

    public ReservaScheduler(ReservaTemporalRepository reservaRepository, PedidoService pedidoService) {
        this.reservaRepository = reservaRepository;
        this.pedidoService = pedidoService;
    }

    // Se ejecuta cada minuto (60000 ms)
    @Scheduled(fixedRate = 60000)
    public void limpiarReservasExpiradas() {
        List<ReservaTemporal> expiradas = reservaRepository.findByExpiraEnBefore(LocalDateTime.now());
        for (ReservaTemporal reserva : expiradas) {
            System.out.println("Cancelando reserva expirada ID: " + reserva.getId());
            pedidoService.cancelarReserva(reserva.getId());
        }
    }
}
