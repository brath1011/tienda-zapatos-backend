package com.Utp.DesarrolloWeb.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RolConverter implements AttributeConverter<Rol, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Rol attribute) {
        if (attribute == null) {
            return null;
        }
        return switch (attribute) {
            case CLIENTE -> 1;
            case ADMINISTRADOR -> 2;
            case REPARTIDOR -> 3;
            case VENTAS -> 4;
        };
    }

    @Override
    public Rol convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return switch (dbData) {
            case 1 -> Rol.CLIENTE;
            case 2 -> Rol.ADMINISTRADOR;
            case 3 -> Rol.REPARTIDOR;
            case 4 -> Rol.VENTAS;
            default -> throw new IllegalArgumentException("ID de rol desconocido: " + dbData);
        };
    }
}
