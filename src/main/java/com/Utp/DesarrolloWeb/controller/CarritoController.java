package com.Utp.DesarrolloWeb.controller;

import com.Utp.DesarrolloWeb.model.Carrito;
import com.Utp.DesarrolloWeb.service.CarritoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam Long idProducto, @RequestParam int cantidad) {
        return service.agregar(idProducto, cantidad);
    }

    @GetMapping
    public List<Carrito> listar() {
        return service.listar();
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) { 
        return service.eliminar(id);
    }

    @GetMapping("/total")
    public double total() {
        return service.total();
    }

    @DeleteMapping("/vaciar")
    public String vaciar() {
        return service.vaciar();
    }
}