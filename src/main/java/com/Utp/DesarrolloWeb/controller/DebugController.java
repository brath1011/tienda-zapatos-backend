package com.Utp.DesarrolloWeb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Utp.DesarrolloWeb.repository.PedidoRepository;
import com.Utp.DesarrolloWeb.repository.ProductoRepository;
import com.Utp.DesarrolloWeb.model.Pedido;
import com.Utp.DesarrolloWeb.model.Producto;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class DebugController {
    
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    
    public DebugController(PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }
    
    @GetMapping("/api/debug/pedidos")
    public List<Pedido> debugPedidos() {
        return pedidoRepository.findAll();
    }

    // Endpoint para ver qué imágenes están en uso vs en disco
    @GetMapping("/api/debug/imagenes")
    public Map<String, Object> diagnosticarImagenes() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        
        // 1. Obtener TODAS las URLs de imagen referenciadas por los productos
        List<Producto> productos = productoRepository.findAll();
        Set<String> imagenesEnUso = new HashSet<>();
        for (Producto p : productos) {
            if (p.getImagen() != null && !p.getImagen().isBlank()) {
                // El campo imagen puede tener múltiples URLs separadas por coma
                String[] urls = p.getImagen().split(",");
                for (String url : urls) {
                    String trimmed = url.trim();
                    if (trimmed.startsWith("/uploads/")) {
                        imagenesEnUso.add(trimmed.replace("/uploads/", ""));
                    }
                }
            }
        }
        
        // 2. Obtener todas las imágenes físicas en la carpeta uploads
        File folder = new File("uploads");
        Set<String> imagenesEnDisco = new HashSet<>();
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    imagenesEnDisco.add(f.getName());
                }
            }
        }
        
        // 3. Calcular huérfanas (en disco pero no en uso)
        Set<String> huerfanas = new HashSet<>(imagenesEnDisco);
        huerfanas.removeAll(imagenesEnUso);
        
        resultado.put("totalProductos", productos.size());
        resultado.put("imagenesReferenciadasEnDB", imagenesEnUso.size());
        resultado.put("imagenesEnDisco", imagenesEnDisco.size());
        resultado.put("imagenesHuerfanas", huerfanas.size());
        resultado.put("archivosHuerfanos", huerfanas.stream().sorted().collect(Collectors.toList()));
        resultado.put("archivosEnUso", imagenesEnUso.stream().sorted().collect(Collectors.toList()));
        
        return resultado;
    }

    // Endpoint para eliminar las imágenes huérfanas
    @DeleteMapping("/api/debug/imagenes/limpiar")
    public Map<String, Object> limpiarImagenes() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        
        // 1. Obtener todas las URLs referenciadas
        List<Producto> productos = productoRepository.findAll();
        Set<String> imagenesEnUso = new HashSet<>();
        for (Producto p : productos) {
            if (p.getImagen() != null && !p.getImagen().isBlank()) {
                String[] urls = p.getImagen().split(",");
                for (String url : urls) {
                    String trimmed = url.trim();
                    if (trimmed.startsWith("/uploads/")) {
                        imagenesEnUso.add(trimmed.replace("/uploads/", ""));
                    }
                }
            }
        }
        
        // 2. Eliminar archivos que no están referenciados
        File folder = new File("uploads");
        int eliminadas = 0;
        List<String> eliminadasList = new ArrayList<>();
        
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!imagenesEnUso.contains(f.getName())) {
                        if (f.delete()) {
                            eliminadas++;
                            eliminadasList.add(f.getName());
                        }
                    }
                }
            }
        }
        
        resultado.put("imagenesEliminadas", eliminadas);
        resultado.put("archivosEliminados", eliminadasList);
        resultado.put("imagenesRestantes", imagenesEnUso.size());
        
        return resultado;
    }
}
