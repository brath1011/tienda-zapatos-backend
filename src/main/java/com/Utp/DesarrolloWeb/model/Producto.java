package com.Utp.DesarrolloWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// [MAPEO OBJETO-RELACIONAL (ORM)]: Define a la clase como una representación de la tabla física en MySQL.
@Entity
@Table(name = "productos")
public class Producto {
    
    // [IDENTIDAD DE TABLA]: Configura la Primary Key con estrategia Auto-incremental en el motor relacional.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    // [RESTRICCIONES DDL]: Protege la integridad referencial obligando a que la columna no reciba valores nulos.
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La marca es obligatoria")
    @Column(nullable = false)
    private String marca;

    @NotBlank(message = "El género es obligatorio")
    @Column(name = "genero")
    private String genero;

    @NotBlank(message = "El color es obligatorio")
    @Column(nullable = false)
    private String color;

    @NotBlank(message = "La categoría es obligatoria")
    @Column(nullable = false)
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private double precio;

    @ElementCollection
    @CollectionTable(name = "producto_tallas", joinColumns = @JoinColumn(name = "id_producto"))
    @MapKeyColumn(name = "talla")
    @Column(name = "stock")
    private java.util.Map<String, Integer> tallasStock = new java.util.HashMap<>();

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT", name = "descripcion_general")
    private String descripcionGeneral;
    
    @Column(columnDefinition = "TEXT", name = "imagen")
    private String imagen;

    // [DATO CALCULADO]: No se guarda en la base de datos, se calcula "al vuelo" si hay campañas activas
    @Transient
    private Double precioDescuento;

    public Producto() {}

    // [ENCAPSULAMIENTO DE CLASE]: Métodos de acceso (Getters/Setters) para la protección y manipulación del estado.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public java.util.Map<String, Integer> getTallasStock() { return tallasStock; }
    public void setTallasStock(java.util.Map<String, Integer> tallasStock) { this.tallasStock = tallasStock; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDescripcionGeneral() { return descripcionGeneral; }
    public void setDescripcionGeneral(String descripcionGeneral) { this.descripcionGeneral = descripcionGeneral; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Double getPrecioDescuento() { return precioDescuento; }
    public void setPrecioDescuento(Double precioDescuento) { this.precioDescuento = precioDescuento; }
}