package com.Utp.DesarrolloWeb.controller;

import com.Utp.DesarrolloWeb.model.Producto;
import com.Utp.DesarrolloWeb.service.OfertaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ofertas")
public class OfertaController {

    private final OfertaService ofertaService;

    public OfertaController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    // Endpoint para aplicar descuento
    @PutMapping("/descuento/{id}")
    public Producto aplicarDescuento(@PathVariable Long id,
                                     @RequestParam double porcentaje) {
        return ofertaService.aplicarDescuento(id, porcentaje);
    }
}