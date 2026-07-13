package com.Utp.DesarrolloWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.Utp.DesarrolloWeb.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // 3. QUERY PERSONALIZADO (JPQL): Calcula la suma total de dinero vendido (excluye devoluciones)
    // Ideal para el panel administrativo (Dashboard) en tu Angular.
    @Query("SELECT COALESCE(SUM(p.total), 0.0) FROM Pedido p WHERE p.estado != 'DEVUELTO' AND p.estado != 'REEMBOLSADO'")
    Double calcularTotalVentasGrosas();

    // Suma de dinero en un rango de fechas
    @Query("SELECT COALESCE(SUM(p.total), 0.0) FROM Pedido p WHERE p.estado != 'DEVUELTO' AND p.estado != 'REEMBOLSADO' AND p.fecha BETWEEN :inicio AND :fin")
    Double calcularTotalVentasFechas(@org.springframework.data.repository.query.Param("inicio") java.time.LocalDate inicio, @org.springframework.data.repository.query.Param("fin") java.time.LocalDate fin);

    // Agrupación por día para los gráficos (Chart.js)
    @Query("SELECT p.fecha, SUM(p.total) FROM Pedido p WHERE p.estado != 'DEVUELTO' AND p.estado != 'REEMBOLSADO' AND p.fecha BETWEEN :inicio AND :fin GROUP BY p.fecha ORDER BY p.fecha ASC")
    java.util.List<Object[]> obtenerVentasAgrupadasPorDia(@org.springframework.data.repository.query.Param("inicio") java.time.LocalDate inicio, @org.springframework.data.repository.query.Param("fin") java.time.LocalDate fin);

    java.util.List<Pedido> findByEstado(String estado);
    java.util.List<Pedido> findByEstadoAndRepartidor(String estado, com.Utp.DesarrolloWeb.model.Usuario repartidor);
    java.util.List<Pedido> findByEstadoInAndRepartidor(java.util.List<String> estados, com.Utp.DesarrolloWeb.model.Usuario repartidor);
    
    // Consulta para que el cliente pueda ver su historial de compras
    java.util.List<Pedido> findByUsuario(com.Utp.DesarrolloWeb.model.Usuario usuario);

    // Consulta para que el repartidor vea su historial de entregas
    java.util.List<Pedido> findByRepartidor(com.Utp.DesarrolloWeb.model.Usuario repartidor);

    // Consulta para que el repartidor vea su historial por fechas
    java.util.List<Pedido> findByRepartidorAndFechaBetween(com.Utp.DesarrolloWeb.model.Usuario repartidor, java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin);

    // Consulta para obtener pedidos en un rango de fechas (para los reportes de ventas)
    java.util.List<Pedido> findByFechaBetween(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin);
}