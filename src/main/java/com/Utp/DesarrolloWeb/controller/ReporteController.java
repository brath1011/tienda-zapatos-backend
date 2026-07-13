package com.Utp.DesarrolloWeb.controller;

import com.Utp.DesarrolloWeb.model.Pedido;
import com.Utp.DesarrolloWeb.repository.PedidoRepository;
import com.Utp.DesarrolloWeb.service.ReporteExcelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final PedidoRepository pedidoRepository;
    private final ReporteExcelService reporteExcelService;

    public ReporteController(PedidoRepository pedidoRepository, ReporteExcelService reporteExcelService) {
        this.pedidoRepository = pedidoRepository;
        this.reporteExcelService = reporteExcelService;
    }

    // 1. VISTA PREVIA (JSON): Retorna los datos crudos para que el frontend pueda mostrarlos en una tabla antes de descargar
    @GetMapping("/ventas")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'VENTAS')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Pedido>> obtenerVistaPreviaVentas(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        List<Pedido> pedidos = pedidoRepository.findByFechaBetween(fechaInicio, fechaFin)
                .stream()
                .filter(p -> !"DEVUELTO".equals(p.getEstado()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(pedidos);
    }

    // 2. EXPORTAR A EXCEL: Retorna el archivo binario estilizado
    @GetMapping("/ventas/excel")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'VENTAS')")
    @Transactional(readOnly = true)
    public ResponseEntity<InputStreamResource> descargarExcelVentas(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        // Obtener la data filtrada (excluyendo devoluciones para que el Excel sume correctamente)
        List<Pedido> pedidos = pedidoRepository.findByFechaBetween(fechaInicio, fechaFin)
                .stream()
                .filter(p -> !"DEVUELTO".equals(p.getEstado()))
                .collect(java.util.stream.Collectors.toList());
        
        // Generar el archivo Excel
        ByteArrayInputStream stream = reporteExcelService.generarReporteVentas(pedidos);
        
        // Configurar las cabeceras HTTP para forzar la descarga de un archivo .xlsx
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte_ventas_" + fechaInicio.toString() + "_al_" + fechaFin.toString() + ".xlsx");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(stream));
    }
}
