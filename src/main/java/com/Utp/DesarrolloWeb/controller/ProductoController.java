package com.Utp.DesarrolloWeb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.Utp.DesarrolloWeb.model.Producto;
import com.Utp.DesarrolloWeb.repository.ProductoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

// [API REST]: Componente de la capa de presentación que expone la lógica mediante endpoints JSON.
@RestController
@RequestMapping("/api/zapatos")
// [POLÍTICAS CORS]: Permite la comunicación inter-origen entre Angular (4200) y Spring (8090).
@CrossOrigin(origins = "*") 
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final com.Utp.DesarrolloWeb.service.CampanaService campanaService;

    @PersistenceContext
    private EntityManager entityManager;

    // [INYECCIÓN DE DEPENDENCIAS]: Desacoplamiento utilizando el patrón Singleton por constructor.
    public ProductoController(ProductoRepository productoRepository, com.Utp.DesarrolloWeb.service.CampanaService campanaService) {
        this.productoRepository = productoRepository;
        this.campanaService = campanaService;
    }

    // 1. LEER CATÁLOGO [ENDPOINT PÚBLICO - GET]: Responde a las peticiones del frontend para la grilla principal con paginación.
    @GetMapping
    public ResponseEntity<Page<Producto>> listarTodosLosZapatos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> listaPaginada = productoRepository.findAll(pageable);
        campanaService.aplicarDescuentos(listaPaginada.getContent());
        return ResponseEntity.ok(listaPaginada);
    }

    // 2. BUSCAR POR ID [ENDPOINT PÚBLICO - GET]: Utiliza variables de ruta para resolver solicitudes unitarias.
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerZapatoPorId(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calzado no encontrado con ID: " + id));
        campanaService.aplicarDescuentos(java.util.List.of(producto));
        return ResponseEntity.ok(producto);
    }

    // 3. CREAR PRODUCTO [ENDPOINT PRIVADO - POST]: Transforma el Body JSON y ejecuta la persistencia.
    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')") // [SEGURIDAD JWT]: Valida roles antes de procesar la petición.
    public ResponseEntity<Producto> guardarNuevoZapato(@Valid @RequestBody Producto producto) {
        Producto nuevoProducto = productoRepository.save(producto); // Query heredado
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // 4. MODIFICAR/EDITAR [ENDPOINT PRIVADO - PUT]: Mapea y sobreescribe un registro existente.
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Producto> actualizarZapato(@PathVariable Long id, @Valid @RequestBody Producto datosActualizados) {
        Producto productoExistente = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Calzado no encontrado con ID: " + id));
            
        // Actualizamos los campos unificados de la tienda de zapatos
        productoExistente.setNombre(datosActualizados.getNombre());
        productoExistente.setMarca(datosActualizados.getMarca());
        productoExistente.setGenero(datosActualizados.getGenero());
        productoExistente.setColor(datosActualizados.getColor());
        productoExistente.setPrecio(datosActualizados.getPrecio());
        productoExistente.setTallasStock(datosActualizados.getTallasStock());
        productoExistente.setCategoria(datosActualizados.getCategoria());
        productoExistente.setDescripcion(datosActualizados.getDescripcion());
        productoExistente.setDescripcionGeneral(datosActualizados.getDescripcionGeneral());
        productoExistente.setImagen(datosActualizados.getImagen());
        
        Producto productoSalvado = productoRepository.save(productoExistente);
        return ResponseEntity.ok(productoSalvado);
    }

    // 5. ELIMINAR [ENDPOINT PRIVADO - DELETE]: Limpia relaciones y elimina el producto.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Transactional
    public ResponseEntity<Void> eliminarZapato(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("No existe el calzado con ID: " + id);
        }
        // Eliminar dependencias en orden para evitar violaciones de FK
        entityManager.createNativeQuery("DELETE FROM carrito_items WHERE producto_id = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM reserva_temporal_detalle WHERE producto_id = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM detalle_pedidos WHERE id_producto = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM producto_tallas WHERE id_producto = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM productos WHERE id_producto = :id").setParameter("id", id).executeUpdate();
        return ResponseEntity.noContent().build();
    }

    // 6. SUBIR IMAGEN [ENDPOINT PRIVADO - POST]: Sube una imagen al servidor y retorna su URL
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> subirImagen(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo seleccionado está vacío");
        }
        try {
            String folder = "uploads/";
            java.io.File directory = new java.io.File(folder);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            java.nio.file.Path path = java.nio.file.Paths.get(folder + fileName);
            java.nio.file.Files.write(path, file.getBytes());

            java.util.Map<String, String> response = new java.util.HashMap<>();
            response.put("url", "/uploads/" + fileName);
            return ResponseEntity.ok(response);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error al guardar la imagen en el servidor: " + e.getMessage());
        }
    }
}