package com.Utp.DesarrolloWeb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.Utp.DesarrolloWeb.model.Pedido;
import com.Utp.DesarrolloWeb.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*") // Clave para que Angular pueda conectarse
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // POST: El cliente compra sus zapatos (Requiere estar logueado como USER o ADMIN)
    @PostMapping("/comprar")
    public ResponseEntity<Pedido> realizarCompra(@RequestBody com.Utp.DesarrolloWeb.dto.CompraWebRequest request) {
        Pedido nuevaCompra = pedidoService.registrarCompra(request);
        return new ResponseEntity<>(nuevaCompra, HttpStatus.CREATED);
    }

    // ---------------- ENDPOINTS RESERVA TEMPORAL ---------------- //
    @PostMapping("/reservar")
    public ResponseEntity<com.Utp.DesarrolloWeb.dto.ReservaResponse> crearReserva(@RequestBody com.Utp.DesarrolloWeb.dto.ReservaRequest request) {
        Long reservaId = pedidoService.crearReserva(request);
        return ResponseEntity.ok(new com.Utp.DesarrolloWeb.dto.ReservaResponse(reservaId, "Reserva creada exitosamente por 10 minutos."));
    }

    @DeleteMapping("/reservar/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        pedidoService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }

    // GET: El cliente revisa su propio historial de compras (Requiere estar logueado)
    @GetMapping("/mis-compras")
    public ResponseEntity<java.util.List<Pedido>> obtenerMisCompras() {
        return ResponseEntity.ok(pedidoService.listarMisCompras());
    }

    // POST: El cliente solicita el reembolso de su compra
    @PostMapping("/{id}/reembolso")
    public ResponseEntity<Pedido> solicitarReembolso(@PathVariable Long id) {
        Pedido pedidoReembolsado = pedidoService.solicitarReembolso(id);
        return ResponseEntity.ok(pedidoReembolsado);
    }

    // GET: El jefe revisa la caja global (SOLO ADMINISTRADOR)
    @GetMapping("/admin/ganancias")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')") // Restricción estricta por Roles del JWT
    public ResponseEntity<Double> getGananciasTotales() {
        Double ganancias = pedidoService.obtenerGananciasTotales();
        return ResponseEntity.ok(ganancias);
    }

    // GET: Obtener ganancias por fechas (ADMINISTRADOR y VENDEDOR)
    @GetMapping("/ganancias/fechas")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'VENTAS')")
    public ResponseEntity<Double> getGananciasFechas(
            @RequestParam("inicio") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
            @RequestParam("fin") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fin) {
        Double ganancias = pedidoService.obtenerGananciasFechas(inicio, fin);
        return ResponseEntity.ok(ganancias);
    }

    // GET: Obtener datos para gráfico (SOLO ADMINISTRADOR)
    @GetMapping("/admin/ganancias/grafico")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getDatosGrafico(
            @RequestParam("inicio") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
            @RequestParam("fin") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fin) {
        return ResponseEntity.ok(pedidoService.obtenerDatosGrafico(inicio, fin));
    }

    // ---------------- ENDPOINTS VENDEDOR ---------------- //
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('VENTAS', 'ADMINISTRADOR')")
    public ResponseEntity<java.util.List<Pedido>> listarTodosLosPedidos() {
        return ResponseEntity.ok(pedidoService.listarTodosLosPedidos());
    }

    @GetMapping("/fechas")
    @PreAuthorize("hasAnyAuthority('VENTAS', 'ADMINISTRADOR')")
    public ResponseEntity<java.util.List<Pedido>> listarPedidosPorFechas(
            @RequestParam("inicio") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
            @RequestParam("fin") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fin) {
        return ResponseEntity.ok(pedidoService.listarPedidosFechas(inicio, fin));
    }

    @PostMapping("/presencial")
    @PreAuthorize("hasAnyAuthority('VENTAS', 'ADMINISTRADOR')")
    public ResponseEntity<Pedido> registrarCompraPresencial(@RequestBody com.Utp.DesarrolloWeb.dto.PedidoPresencialRequest request) {
        Pedido nuevaCompra = pedidoService.registrarCompraPresencial(request);
        return new ResponseEntity<>(nuevaCompra, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/despachar")
    @PreAuthorize("hasAnyAuthority('VENTAS', 'ADMINISTRADOR')")
    public ResponseEntity<Pedido> despacharPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.despacharPedido(id));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyAuthority('VENTAS', 'ADMINISTRADOR')")
    public ResponseEntity<Pedido> cambiarEstadoPedido(@PathVariable Long id, @RequestParam String nuevoEstado, @RequestParam(required = false) Long idRepartidor) {
        return ResponseEntity.ok(pedidoService.cambiarEstadoPedido(id, nuevoEstado, idRepartidor));
    }

    @GetMapping("/repartidores")
    @PreAuthorize("hasAnyAuthority('VENTAS', 'ADMINISTRADOR')")
    public ResponseEntity<java.util.List<com.Utp.DesarrolloWeb.model.Usuario>> listarRepartidores() {
        return ResponseEntity.ok(pedidoService.listarRepartidores());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- ENDPOINTS REPARTIDOR ---------------- //

    @GetMapping("/reparto")
    @PreAuthorize("hasAuthority('REPARTIDOR')")
    public ResponseEntity<java.util.List<Pedido>> listarPedidosParaReparto() {
        return ResponseEntity.ok(pedidoService.listarPedidosParaReparto());
    }

    @PutMapping("/{id}/entregar")
    @PreAuthorize("hasAuthority('REPARTIDOR')")
    public ResponseEntity<Pedido> entregarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.entregarPedido(id));
    }

    @PutMapping("/{id}/devolver")
    @PreAuthorize("hasAuthority('REPARTIDOR')")
    public ResponseEntity<Pedido> devolverPedidoRepartidor(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cambiarEstadoPedido(id, "DEVUELTO", null));
    }

    @GetMapping("/mis-entregas")
    @PreAuthorize("hasAuthority('REPARTIDOR')")
    public ResponseEntity<java.util.List<Pedido>> obtenerMisEntregas() {
        return ResponseEntity.ok(pedidoService.obtenerMisEntregas());
    }

    @GetMapping("/mis-entregas/fechas")
    @PreAuthorize("hasAuthority('REPARTIDOR')")
    public ResponseEntity<java.util.List<Pedido>> obtenerMisEntregasFechas(
            @RequestParam("inicio") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
            @RequestParam("fin") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fin) {
        return ResponseEntity.ok(pedidoService.obtenerMisEntregasFechas(inicio, fin));
    }
}
