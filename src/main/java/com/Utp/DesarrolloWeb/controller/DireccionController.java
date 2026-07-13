package com.Utp.DesarrolloWeb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Utp.DesarrolloWeb.model.Direccion;
import com.Utp.DesarrolloWeb.service.DireccionService;

@RestController
@RequestMapping("/api/direcciones")
@CrossOrigin(origins = "*")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @GetMapping
    public ResponseEntity<java.util.List<Direccion>> listarMisDirecciones() {
        return ResponseEntity.ok(direccionService.listarMisDirecciones());
    }

    @PostMapping
    public ResponseEntity<Direccion> agregarDireccion(@RequestBody Direccion direccion) {
        return new ResponseEntity<>(direccionService.agregarDireccion(direccion), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Direccion> actualizarDireccion(@PathVariable Long id, @RequestBody Direccion direccion) {
        return ResponseEntity.ok(direccionService.actualizarDireccion(id, direccion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long id) {
        direccionService.eliminarDireccion(id);
        return ResponseEntity.noContent().build();
    }
}
