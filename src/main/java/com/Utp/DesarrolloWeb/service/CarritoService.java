package com.Utp.DesarrolloWeb.service;

import com.Utp.DesarrolloWeb.model.Carrito;
import com.Utp.DesarrolloWeb.model.Producto;
import com.Utp.DesarrolloWeb.repository.CarritoRepository;
import com.Utp.DesarrolloWeb.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(CarritoRepository carritoRepository, ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public String agregar(Long idProducto, int cantidad) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) return "Producto no encontrado";

        Carrito itemExistente = carritoRepository.findByProductoId(idProducto);
        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
            carritoRepository.save(itemExistente);
            return "Cantidad actualizada";
        }
        carritoRepository.save(new Carrito(producto, cantidad));
        return "Producto agregado";
    }

    public List<Carrito> listar() {
        return carritoRepository.findAll();
    }

    @Transactional
    public String eliminar(Long idProducto) {
        Carrito item = carritoRepository.findByProductoId(idProducto);
        if (item != null) {
            carritoRepository.delete(item);
            return "Producto eliminado";
        }
        return "Producto no encontrado en el carrito";
    }

    public double total() {
        return carritoRepository.findAll().stream().mapToDouble(Carrito::getSubtotal).sum();
    }

    @Transactional
    public String vaciar() {
        carritoRepository.deleteAll();
        return "Carrito vacío";
    }
}