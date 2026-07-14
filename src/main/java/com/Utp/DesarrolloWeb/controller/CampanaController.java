package com.Utp.DesarrolloWeb.controller;

import com.Utp.DesarrolloWeb.model.Campana;
import com.Utp.DesarrolloWeb.repository.CampanaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/campanas")
@CrossOrigin(origins = "*")
public class CampanaController {

    private final CampanaRepository campanaRepository;

    public CampanaController(CampanaRepository campanaRepository) {
        this.campanaRepository = campanaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Campana>> listarCampanas() {
        return ResponseEntity.ok(campanaRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Campana> crearCampana(@RequestBody Campana campana) {
        if (campana.getFechaInicio() == null) {
            campana.setFechaInicio(LocalDate.now());
        }
        if (campana.getFechaFin() == null) {
            campana.setFechaFin(LocalDate.now().plusYears(1)); // Default a 1 año si no envían
        }
        return ResponseEntity.ok(campanaRepository.save(campana));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCampana(@PathVariable Long id) {
        campanaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
