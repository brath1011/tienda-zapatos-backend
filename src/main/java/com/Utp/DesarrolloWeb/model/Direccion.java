package com.Utp.DesarrolloWeb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "direcciones")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Long idDireccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "direccion_exacta", nullable = false)
    private String direccionExacta;

    @Column(name = "referencia")
    private String referencia;

    @Column(name = "distrito", nullable = false)
    private String distrito;

    @Column(name = "activo", columnDefinition = "boolean default true")
    private Boolean activo = true;
}
