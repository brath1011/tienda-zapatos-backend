package com.Utp.DesarrolloWeb.service;

import com.Utp.DesarrolloWeb.model.Campana;
import com.Utp.DesarrolloWeb.model.Producto;
import com.Utp.DesarrolloWeb.repository.CampanaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CampanaService {

    private final CampanaRepository campanaRepository;

    public CampanaService(CampanaRepository campanaRepository) {
        this.campanaRepository = campanaRepository;
    }

    /**
     * Aplica los descuentos de las campañas activas a una lista de productos.
     */
    public void aplicarDescuentos(List<Producto> productos) {
        List<Campana> campanasActivas = campanaRepository.findCampanasActivasHoy(LocalDate.now());

        if (campanasActivas.isEmpty()) {
            return; // No hay campañas, no hacemos nada.
        }

        for (Producto producto : productos) {
            Double maxDescuento = 0.0; // Solo aplicaremos el mejor descuento si coinciden varias campañas

            for (Campana campana : campanasActivas) {
                if (aplicaCampana(producto, campana)) {
                    if (campana.getPorcentajeDescuento() > maxDescuento) {
                        maxDescuento = campana.getPorcentajeDescuento();
                    }
                }
            }

            if (maxDescuento > 0) {
                // Calcular el nuevo precio descontado
                double descuentoMonto = producto.getPrecio() * (maxDescuento / 100.0);
                producto.setPrecioDescuento(producto.getPrecio() - descuentoMonto);
            }
        }
    }

    /**
     * Verifica si una campaña aplica a un producto específico.
     */
    private boolean aplicaCampana(Producto producto, Campana campana) {
        // Si la campaña no tiene filtros, aplica a todo
        if (campana.getFiltroMarca() == null && campana.getFiltroColor() == null && campana.getFiltroModelo() == null) {
            return true;
        }

        boolean coincideModelo = true;
        if (campana.getFiltroModelo() != null && !campana.getFiltroModelo().isEmpty()) {
            coincideModelo = campana.getFiltroModelo().equalsIgnoreCase(producto.getNombre());
        }

        boolean coincideMarca = true;
        if (campana.getFiltroMarca() != null && !campana.getFiltroMarca().isEmpty()) {
            coincideMarca = campana.getFiltroMarca().equalsIgnoreCase(producto.getMarca());
        }

        boolean coincideColor = true;
        if (campana.getFiltroColor() != null && !campana.getFiltroColor().isEmpty()) {
            // Soporta múltiples colores separados por coma
            String[] coloresCampana = campana.getFiltroColor().split(",");
            coincideColor = false;
            for (String c : coloresCampana) {
                if (c.trim().equalsIgnoreCase(producto.getColor())) {
                    coincideColor = true;
                    break;
                }
            }
        }

        // Si tiene filtros, deben coincidir todos los que se hayan especificado.
        return coincideModelo && coincideMarca && coincideColor;
    }
}
