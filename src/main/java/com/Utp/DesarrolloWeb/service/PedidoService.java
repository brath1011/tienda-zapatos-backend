package com.Utp.DesarrolloWeb.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Utp.DesarrolloWeb.model.Pedido;
import com.Utp.DesarrolloWeb.model.DetallePedido;
import com.Utp.DesarrolloWeb.model.Producto;
import com.Utp.DesarrolloWeb.repository.PedidoRepository;
import com.Utp.DesarrolloWeb.repository.ProductoRepository;
import com.Utp.DesarrolloWeb.repository.PagoRepository;
import com.Utp.DesarrolloWeb.repository.ComprobanteRepository;
import com.Utp.DesarrolloWeb.model.Pago;
import com.Utp.DesarrolloWeb.model.Comprobante;
import com.Utp.DesarrolloWeb.model.Direccion;
import com.Utp.DesarrolloWeb.model.Usuario;
import com.Utp.DesarrolloWeb.repository.DireccionRepository;
import com.Utp.DesarrolloWeb.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final CampanaService campanaService;
    private final SimuladorPagoService simuladorPagoService;
    private final DireccionRepository direccionRepository;
    private final com.Utp.DesarrolloWeb.repository.ReservaTemporalRepository reservaTemporalRepository;

    // Respetando el Patrón Singleton e Inyección por Constructor
    public PedidoService(PedidoRepository pedidoRepository, 
                         ProductoRepository productoRepository,
                         PagoRepository pagoRepository,
                         ComprobanteRepository comprobanteRepository,
                         UsuarioRepository usuarioRepository,
                         EmailService emailService,
                         CampanaService campanaService,
                         SimuladorPagoService simuladorPagoService,
                         DireccionRepository direccionRepository,
                         com.Utp.DesarrolloWeb.repository.ReservaTemporalRepository reservaTemporalRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.pagoRepository = pagoRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.campanaService = campanaService;
        this.simuladorPagoService = simuladorPagoService;
        this.direccionRepository = direccionRepository;
        this.reservaTemporalRepository = reservaTemporalRepository;
    }

    // FLUJO DEL CLIENTE: Registrar compra con control estricto de transacciones (Guía 07)
    @Transactional
    public Pedido registrarCompra(com.Utp.DesarrolloWeb.dto.CompraWebRequest request) {
        
        Pedido pedido = request.getPedido();
        
        // 0. SIMULACIÓN DE PAGO (Si falla, aborta todo)
        simuladorPagoService.procesarPago(request.getMetodoPago(), request.getNumeroTarjeta(), request.getCvv());
        
        // SEGURIDAD: Obtener el usuario autenticado desde el token JWT
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        
        pedido.setUsuario(usuario); // Ignoramos el ID enviado por el frontend por seguridad
        pedido.setFecha(java.time.LocalDate.now()); // Autocompletar la fecha del pedido en el backend
        pedido.setEstado("PENDIENTE"); // Por seguridad, toda compra web nace en estado PENDIENTE
        pedido.setTipoPedido("WEB"); // Indica que la compra se realizó en la plataforma web
        
        // SOLUCIÓN A RESTRICCIÓN DE DB y NUEVO FLUJO DE DIRECCIÓN
        if (pedido.getDireccion() == null) {
            Direccion nuevaDireccion = new Direccion();
            nuevaDireccion.setUsuario(usuario);
            
            // Construir la dirección completa a partir de los nuevos campos
            String direccionCompleta = "";
            if (request.getDepartamento() != null && !request.getDepartamento().isEmpty()) {
                direccionCompleta += request.getDepartamento() + ", " + request.getProvincia() + ", " + request.getDistrito() + " - ";
                direccionCompleta += request.getCalleJiron();
                if (request.getNumero() != null && !request.getNumero().isEmpty()) {
                    direccionCompleta += " #" + request.getNumero();
                }
                if (request.getDptoInterior() != null && !request.getDptoInterior().isEmpty()) {
                    direccionCompleta += " " + request.getDptoInterior();
                }
                nuevaDireccion.setDistrito(request.getDistrito());
            } else {
                direccionCompleta = "Recojo en tienda / Por confirmar";
                nuevaDireccion.setDistrito("Lima");
            }
            
            nuevaDireccion.setDireccionExacta(direccionCompleta);
            nuevaDireccion = direccionRepository.save(nuevaDireccion);
            pedido.setDireccion(nuevaDireccion);
        }
        
        boolean usarReserva = request.getReservaId() != null;
        if (usarReserva) {
            com.Utp.DesarrolloWeb.model.ReservaTemporal reserva = reservaTemporalRepository.findById(request.getReservaId())
                .orElseThrow(() -> new RuntimeException("La reserva expiró o no existe. Por favor, vuelva a intentar la compra."));
            
            // Reconstruir detalles desde la reserva
            for (com.Utp.DesarrolloWeb.model.ReservaTemporalDetalle rDetalle : reserva.getDetalles()) {
                Producto producto = rDetalle.getProducto();
                
                // Aplicamos descuentos si los hay
                campanaService.aplicarDescuentos(java.util.List.of(producto));
                double precioFinal = producto.getPrecioDescuento() != null ? producto.getPrecioDescuento() : producto.getPrecio();
                
                DetallePedido detalle = new DetallePedido();
                detalle.setProducto(producto);
                detalle.setTallaSeleccionada(rDetalle.getTalla());
                detalle.setCantidad(rDetalle.getCantidad());
                detalle.setPrecioUnitario(precioFinal);
                detalle.setPedido(pedido);
                pedido.getDetalles().add(detalle);
            }
            // Eliminar la reserva ya que se va a convertir en pedido real
            reservaTemporalRepository.delete(reserva);
            
        } else {
            for (DetallePedido detalle : pedido.getDetalles()) {
                // Verificamos que el producto exista
                Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalle.getProducto().getId()));

                // Disminuir stock por talla específica
                String tallaSeleccionada = detalle.getTallaSeleccionada();
                if (tallaSeleccionada == null || tallaSeleccionada.isEmpty()) {
                    throw new RuntimeException("Talla no seleccionada para el producto: " + producto.getNombre());
                }
                
                java.util.Map<String, Integer> stockPorTalla = producto.getTallasStock();
                Integer stockActual = stockPorTalla.getOrDefault(tallaSeleccionada, 0);
                
                if (stockActual < detalle.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() + " en talla " + tallaSeleccionada);
                }
                
                stockPorTalla.put(tallaSeleccionada, stockActual - detalle.getCantidad());
                producto.setTallasStock(stockPorTalla);
                productoRepository.save(producto);
                
                // Aplicamos descuentos si los hay
                campanaService.aplicarDescuentos(java.util.List.of(producto));
                double precioFinal = producto.getPrecioDescuento() != null ? producto.getPrecioDescuento() : producto.getPrecio();
                
                // Seguridad backend: Guardamos el precio unitario exacto del producto (con o sin descuento)
                detalle.setProducto(producto); // Asociar el producto completo de la DB (nombre, marca, etc.)
                detalle.setPrecioUnitario(precioFinal);
                detalle.setPedido(pedido);
            }
        }

        // Calculamos el total de toda la compra sumando precio_unitario * cantidad
        double total = pedido.getDetalles().stream().mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad()).sum();
        pedido.setTotal(total);

        // Guardamos el pedido (y los detalles en cascada)
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 1. Generar el Pago
        Pago pago = new Pago();
        pago.setPedido(pedidoGuardado);
        pago.setMetodoPago(request.getMetodoPago() != null ? request.getMetodoPago().toUpperCase() : "EFECTIVO");
        pago.setEstadoPago("APROBADO");
        pagoRepository.save(pago);

        // 2. Generar el Comprobante (Boleta)
        Comprobante boleta = new Comprobante();
        boleta.setPedido(pedidoGuardado);
        boleta.setTipoComprobante("BOLETA");
        boleta.setSerieNumero("B001-" + pedidoGuardado.getIdPedido()); // Generación simple
        boleta.setIgv(pedidoGuardado.getTotal() * 0.18);
        boleta.setSubtotal(pedidoGuardado.getTotal() - boleta.getIgv());
        boleta.setTotal(pedidoGuardado.getTotal());
        comprobanteRepository.save(boleta);

        // Enviar boleta por correo (Asíncrono)
        emailService.enviarBoletaCorreo(pedidoGuardado, emailAutenticado, pago.getMetodoPago());

        return pedidoGuardado;
    }

    // FLUJO HÍBRIDO: Registrar compra presencial (Tienda Física)
    @Transactional
    public Pedido registrarCompraPresencial(com.Utp.DesarrolloWeb.dto.PedidoPresencialRequest request) {
        
        Pedido pedido = request.getPedido();
        String emailCliente = request.getEmailCliente();
        
        // 1. Buscar al usuario o crear cuenta sombra (por Email o DNI)
        Usuario cliente = usuarioRepository.findByEmail(emailCliente).orElse(null);
        if (cliente == null) {
            cliente = usuarioRepository.findByNumeroDocumento(request.getDniCliente()).orElse(null);
        }
        if (cliente == null) {
            Usuario nuevoCliente = new Usuario();
            nuevoCliente.setEmail(emailCliente);
            nuevoCliente.setNombre(request.getNombreCliente());
            nuevoCliente.setNumeroDocumento(request.getDniCliente());
            nuevoCliente.setPassword(""); // Sin contraseña por ahora, la crearán al registrarse en la web
            nuevoCliente.setRol(com.Utp.DesarrolloWeb.model.Rol.CLIENTE);
            cliente = usuarioRepository.save(nuevoCliente);
        }
        
        pedido.setUsuario(cliente);
        pedido.setFecha(java.time.LocalDate.now());
        pedido.setEstado("ENTREGADO_PRESENCIAL");
        pedido.setTipoPedido("PRESENCIAL");
        try {
            String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
            usuarioRepository.findByEmail(emailAutenticado).ifPresent(pedido::setVendedor);
        } catch (Exception e) {}
        
        // SOLUCIÓN A RESTRICCIÓN DE DB: Asignamos dirección temporal si no la hay (Igual que en WEB)
        if (pedido.getDireccion() == null) {
            Direccion dirTemporal = new Direccion();
            dirTemporal.setUsuario(cliente);
            dirTemporal.setDireccionExacta("Compra Física en Tienda");
            dirTemporal.setDistrito("Tienda");
            dirTemporal = direccionRepository.save(dirTemporal);
            pedido.setDireccion(dirTemporal);
        }
        
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Disminuir stock por talla específica
            String tallaSeleccionada = detalle.getTallaSeleccionada();
            if (tallaSeleccionada == null || tallaSeleccionada.isEmpty()) {
                throw new RuntimeException("Talla no seleccionada para el producto: " + producto.getNombre());
            }
            
            java.util.Map<String, Integer> stockPorTalla = producto.getTallasStock();
            Integer stockActual = stockPorTalla.getOrDefault(tallaSeleccionada, 0);
            
            if (stockActual < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() + " en talla " + tallaSeleccionada);
            }
            
            stockPorTalla.put(tallaSeleccionada, stockActual - detalle.getCantidad());
            producto.setTallasStock(stockPorTalla);
            productoRepository.save(producto);
            // Aplicamos descuentos si los hay
            campanaService.aplicarDescuentos(java.util.List.of(producto));
            double precioFinal = producto.getPrecioDescuento() != null ? producto.getPrecioDescuento() : producto.getPrecio();
            
            detalle.setProducto(producto); // Asociar el producto completo de la DB (nombre, marca, etc.)
            detalle.setPrecioUnitario(precioFinal);
            detalle.setPedido(pedido);
        }

        double total = pedido.getDetalles().stream().mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad()).sum();
        pedido.setTotal(total);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Generar Pago Automático
        Pago pago = new Pago();
        pago.setPedido(pedidoGuardado);
        pago.setMetodoPago(request.getMetodoPago() != null ? request.getMetodoPago().toUpperCase() : "EFECTIVO");
        pago.setEstadoPago("APROBADO");
        pagoRepository.save(pago);

        // Generar Boleta
        Comprobante boleta = new Comprobante();
        boleta.setPedido(pedidoGuardado);
        boleta.setTipoComprobante("BOLETA");
        boleta.setSerieNumero("P001-" + pedidoGuardado.getIdPedido()); // Serie de tienda física
        boleta.setIgv(pedidoGuardado.getTotal() * 0.18);
        boleta.setSubtotal(pedidoGuardado.getTotal() - boleta.getIgv());
        boleta.setTotal(pedidoGuardado.getTotal());
        comprobanteRepository.save(boleta);

        // Enviar boleta por correo (Asíncrono)
        emailService.enviarBoletaCorreo(pedidoGuardado, emailCliente, pago.getMetodoPago());

        return pedidoGuardado;
    }

    // FLUJO DEL ADMINISTRADOR: Obtener el reporte de dinero total de la tienda
    @Transactional(readOnly = true)
    public Double obtenerGananciasTotales() {
        return pedidoRepository.calcularTotalVentasGrosas();
    }

    // FLUJO DEL ADMINISTRADOR/VENDEDOR: Ganancias por rango de fechas
    @Transactional(readOnly = true)
    public Double obtenerGananciasFechas(java.time.LocalDate inicio, java.time.LocalDate fin) {
        return pedidoRepository.calcularTotalVentasFechas(inicio, fin);
    }

    // FLUJO DEL ADMINISTRADOR: Datos para el gráfico
    @Transactional(readOnly = true)
    public java.util.List<java.util.Map<String, Object>> obtenerDatosGrafico(java.time.LocalDate inicio, java.time.LocalDate fin) {
        java.util.List<Object[]> resultados = pedidoRepository.obtenerVentasAgrupadasPorDia(inicio, fin);
        java.util.List<java.util.Map<String, Object>> lista = new java.util.ArrayList<>();
        for (Object[] fila : resultados) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("fecha", fila[0].toString());
            map.put("total", fila[1]);
            lista.add(map);
        }
        return lista;
    }

    // FLUJO DEL CLIENTE: Obtener su propio historial de pedidos
    @Transactional(readOnly = true)
    public java.util.List<Pedido> listarMisCompras() {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        return pedidoRepository.findByUsuario(usuario);
    }

    // FLUJO DEL CLIENTE: Solicitar Reembolso
    @Transactional
    public Pedido solicitarReembolso(Long idPedido) {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
            
        Pedido pedido = pedidoRepository.findById(idPedido)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para reembolsar este pedido");
        }
        
        if ("REEMBOLSADO".equals(pedido.getEstado()) || "DEVUELTO".equals(pedido.getEstado())) {
            throw new RuntimeException("El pedido ya se encuentra reembolsado o devuelto");
        }
        
        // Aquí en la Fase 2 verificaremos si fue comprado en evento especial.
        // boolean esEventoEspecial = false;
        // if (esEventoEspecial) throw new RuntimeException("No procede reembolso por evento especial");
        
        // 1. Restaurar Stock
        for (DetallePedido detalle : pedido.getDetalles()) {
            String tallaSeleccionada = detalle.getTallaSeleccionada();
            if (tallaSeleccionada != null && !tallaSeleccionada.isEmpty()) {
                Producto producto = productoRepository.findById(detalle.getProducto().getId()).orElse(null);
                if (producto != null) {
                    java.util.Map<String, Integer> stockPorTalla = producto.getTallasStock();
                    Integer stockActual = stockPorTalla.getOrDefault(tallaSeleccionada, 0);
                    stockPorTalla.put(tallaSeleccionada, stockActual + detalle.getCantidad());
                    producto.setTallasStock(stockPorTalla);
                    productoRepository.save(producto);
                }
            }
        }
        
        // 2. Marcar Pago como Reembolsado
        java.util.List<Pago> pagos = pagoRepository.findByPedido(pedido);
        for (Pago p : pagos) {
            p.setEstadoPago("REEMBOLSADO");
            pagoRepository.save(p);
        }
        
        // 3. Cambiar estado
        pedido.setEstado("REEMBOLSADO");
        return pedidoRepository.save(pedido);
    }

    // ---------------- ROLES DE OPERACIÓN ---------------- //

    // FLUJO VENDEDOR: Ver todos los pedidos
    public java.util.List<Pedido> listarTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    // FLUJO ADMIN/VENDEDOR: Ver pedidos por rango de fechas (Tabla de auditoría)
    @Transactional(readOnly = true)
    public java.util.List<Pedido> listarPedidosFechas(java.time.LocalDate inicio, java.time.LocalDate fin) {
        return pedidoRepository.findByFechaBetween(inicio, fin);
    }

    // FLUJO VENDEDOR: Cambiar a LISTO_PARA_ENVIO (Compatibilidad legacy)
    @Transactional
    public Pedido despacharPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        try {
            String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
            usuarioRepository.findByEmail(emailAutenticado).ifPresent(pedido::setVendedor);
        } catch (Exception e) {}
        pedido.setEstado("LISTO_PARA_ENVIO");
        return pedidoRepository.save(pedido);
    }

    // FLUJO ADMIN/VENDEDOR: Cambiar estado arbitrariamente
    @Transactional
    public Pedido cambiarEstadoPedido(Long id, String nuevoEstado, Long idRepartidor) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
        // Lógica de Devolución
        if ("DEVUELTO".equals(nuevoEstado) && !"DEVUELTO".equals(pedido.getEstado())) {
            // 1. Restaurar Stock
            for (DetallePedido detalle : pedido.getDetalles()) {
                String tallaSeleccionada = detalle.getTallaSeleccionada();
                if (tallaSeleccionada != null && !tallaSeleccionada.isEmpty()) {
                    Producto producto = productoRepository.findById(detalle.getProducto().getId()).orElse(null);
                    if (producto != null) {
                        java.util.Map<String, Integer> stockPorTalla = producto.getTallasStock();
                        Integer stockActual = stockPorTalla.getOrDefault(tallaSeleccionada, 0);
                        stockPorTalla.put(tallaSeleccionada, stockActual + detalle.getCantidad());
                        producto.setTallasStock(stockPorTalla);
                        productoRepository.save(producto);
                    }
                }
            }
            // 2. Marcar Pago como Reembolsado
            java.util.List<Pago> pagos = pagoRepository.findByPedido(pedido);
            for (Pago p : pagos) {
                p.setEstadoPago("REEMBOLSADO");
                pagoRepository.save(p);
            }
        }
        
        pedido.setEstado(nuevoEstado);
        
        if ("LISTO_PARA_ENVIO".equals(nuevoEstado)) {
            try {
                String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
                usuarioRepository.findByEmail(emailAutenticado).ifPresent(pedido::setVendedor);
            } catch (Exception e) {}
            
            if (idRepartidor != null) {
                Usuario repartidor = usuarioRepository.findById(idRepartidor)
                    .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
                pedido.setRepartidor(repartidor);
            }
        }
        
        if ("ENTREGADO".equals(nuevoEstado)) {
            pedido.setFechaEntrega(java.time.LocalDateTime.now());
        }
        
        return pedidoRepository.save(pedido);
    }

    // FLUJO ADMINISTRADOR: Eliminar pedido y restaurar stock (Hard Delete)
    @Transactional
    public void eliminarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // 1. Restaurar el stock de los productos comprados
        for (DetallePedido detalle : pedido.getDetalles()) {
            String tallaSeleccionada = detalle.getTallaSeleccionada();
            if (tallaSeleccionada != null && !tallaSeleccionada.isEmpty()) {
                Producto producto = productoRepository.findById(detalle.getProducto().getId()).orElse(null);
                if (producto != null) {
                    java.util.Map<String, Integer> stockPorTalla = producto.getTallasStock();
                    Integer stockActual = stockPorTalla.getOrDefault(tallaSeleccionada, 0);
                    stockPorTalla.put(tallaSeleccionada, stockActual + detalle.getCantidad());
                    producto.setTallasStock(stockPorTalla);
                    productoRepository.save(producto);
                }
            }
        }

        // 2. Eliminar pagos asociados (al no tener CascadeType.ALL en Pedido para Pago)
        pagoRepository.deleteByPedido(pedido);

        // 3. Eliminar pedido (los detalles y comprobante se borran solos por CascadeType.ALL)
        pedidoRepository.delete(pedido);
    }

    // FLUJO REPARTIDOR: Ver solo los listos para envío y los reembolsados recientemente asignados a él
    public java.util.List<Pedido> listarPedidosParaReparto() {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario repartidor = usuarioRepository.findByEmail(emailAutenticado)
            .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
        return pedidoRepository.findByEstadoInAndRepartidor(java.util.List.of("LISTO_PARA_ENVIO", "REEMBOLSADO", "DEVUELTO"), repartidor);
    }

    // LISTAR REPARTIDORES DISPONIBLES
    public java.util.List<Usuario> listarRepartidores() {
        return usuarioRepository.findByRol(com.Utp.DesarrolloWeb.model.Rol.REPARTIDOR);
    }

    // FLUJO REPARTIDOR: Entregar y registrar su ID
    @Transactional
    public Pedido entregarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
        if (!"LISTO_PARA_ENVIO".equals(pedido.getEstado())) {
            throw new RuntimeException("El pedido ya no está en reparto. Estado actual: " + pedido.getEstado() + ". Por favor, refresca tu lista.");
        }
        
        // Obtener el repartidor desde el Token JWT
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario repartidor = usuarioRepository.findByEmail(emailAutenticado)
            .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        pedido.setEstado("ENTREGADO");
        pedido.setFechaEntrega(java.time.LocalDateTime.now());
        pedido.setRepartidor(repartidor); // Guarda el ID del repartidor en MySQL
        return pedidoRepository.save(pedido);
    }

    // FLUJO REPARTIDOR: Ver historial de pedidos que este motorizado entregó
    @Transactional(readOnly = true)
    public java.util.List<Pedido> obtenerMisEntregas() {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario repartidor = usuarioRepository.findByEmail(emailAutenticado)
            .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
        return pedidoRepository.findByRepartidor(repartidor);
    }

    // FLUJO REPARTIDOR: Ver historial por fechas
    @Transactional(readOnly = true)
    public java.util.List<Pedido> obtenerMisEntregasFechas(java.time.LocalDate inicio, java.time.LocalDate fin) {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario repartidor = usuarioRepository.findByEmail(emailAutenticado)
            .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
        return pedidoRepository.findByRepartidorAndFechaBetween(repartidor, inicio, fin);
    }

    // ---------------- LÓGICA DE RESERVA TEMPORAL ---------------- //
    
    @Transactional
    public Long crearReserva(com.Utp.DesarrolloWeb.dto.ReservaRequest request) {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        
        com.Utp.DesarrolloWeb.model.ReservaTemporal reserva = new com.Utp.DesarrolloWeb.model.ReservaTemporal();
        reserva.setEmailCliente(emailAutenticado);
        reserva.setFechaCreacion(java.time.LocalDateTime.now());
        reserva.setExpiraEn(java.time.LocalDateTime.now().plusMinutes(10));
        
        for (com.Utp.DesarrolloWeb.dto.ReservaRequest.ReservaItem item : request.getDetalles()) {
            Producto producto = productoRepository.findById(item.getZapatoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + item.getZapatoId()));
                
            String tallaSeleccionada = item.getTalla();
            java.util.Map<String, Integer> stockPorTalla = producto.getTallasStock();
            Integer stockActual = stockPorTalla.getOrDefault(tallaSeleccionada, 0);
            
            if (stockActual < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() + " en talla " + tallaSeleccionada);
            }
            
            // Restar stock
            stockPorTalla.put(tallaSeleccionada, stockActual - item.getCantidad());
            producto.setTallasStock(stockPorTalla);
            productoRepository.save(producto);
            
            // Crear detalle de reserva
            com.Utp.DesarrolloWeb.model.ReservaTemporalDetalle detalle = new com.Utp.DesarrolloWeb.model.ReservaTemporalDetalle();
            detalle.setProducto(producto);
            detalle.setTalla(tallaSeleccionada);
            detalle.setCantidad(item.getCantidad());
            reserva.addDetalle(detalle);
        }
        
        reserva = reservaTemporalRepository.save(reserva);
        return reserva.getId();
    }
    
    @Transactional
    public void cancelarReserva(Long reservaId) {
        com.Utp.DesarrolloWeb.model.ReservaTemporal reserva = reservaTemporalRepository.findById(reservaId).orElse(null);
        if (reserva != null) {
            // Devolver stock
            for (com.Utp.DesarrolloWeb.model.ReservaTemporalDetalle detalle : reserva.getDetalles()) {
                Producto producto = detalle.getProducto();
                java.util.Map<String, Integer> stockPorTalla = producto.getTallasStock();
                Integer stockActual = stockPorTalla.getOrDefault(detalle.getTalla(), 0);
                stockPorTalla.put(detalle.getTalla(), stockActual + detalle.getCantidad());
                producto.setTallasStock(stockPorTalla);
                productoRepository.save(producto);
            }
            reservaTemporalRepository.delete(reserva);
        }
    }
}
