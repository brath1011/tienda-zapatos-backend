package com.Utp.DesarrolloWeb.service;


import com.Utp.DesarrolloWeb.model.Producto;
import org.springframework.stereotype.Service;

@Service
public class OfertaService {

    private final ProductoService productoService;

    public OfertaService(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Aplicar descuento a un producto
    public Producto aplicarDescuento(Long id, double porcentaje) {
        Producto producto = productoService.obtenerPorId(id);

        if (producto != null) {
            double nuevoPrecio = producto.getPrecio() - (producto.getPrecio() * porcentaje / 100);
            producto.setPrecio(nuevoPrecio);
        }

        return producto;
    }
}
