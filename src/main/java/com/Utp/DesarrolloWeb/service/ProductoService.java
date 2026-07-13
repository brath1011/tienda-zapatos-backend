package com.Utp.DesarrolloWeb.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.Utp.DesarrolloWeb.model.Producto;
import com.Utp.DesarrolloWeb.repository.ProductoRepository;

@Service
public class ProductoService {
    
    private final ProductoRepository repository;
    private final CampanaService campanaService;

    public ProductoService(ProductoRepository repository, CampanaService campanaService) {
        this.repository = repository;
        this.campanaService = campanaService;
    }

    public List<Producto> listarTodos() {
        List<Producto> productos = repository.findAll();
        campanaService.aplicarDescuentos(productos);
        return productos;
    }

    public Producto guardar(Producto producto) {
        return repository.save(producto);
    }

    public Producto obtenerPorId(Long id) {
        Producto producto = repository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        campanaService.aplicarDescuentos(java.util.List.of(producto));
        return producto;
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}