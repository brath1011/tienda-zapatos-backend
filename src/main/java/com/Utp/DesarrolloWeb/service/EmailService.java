package com.Utp.DesarrolloWeb.service;

import com.Utp.DesarrolloWeb.model.Pedido;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void enviarBoletaCorreo(Pedido pedido, String correoCliente, String metodoPago) {
        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(correoCliente);
            helper.setSubject("¡Gracias por tu compra en ZapatosStore! - Boleta Electrónica");

            // Crear el cuerpo HTML de la boleta de manera dinámica
            String tipoCompra = "PRESENCIAL".equalsIgnoreCase(pedido.getTipoPedido()) 
                    ? "presencial en nuestra tienda física" 
                    : "online en nuestra tienda web";

            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                    + "<div style='text-align: center; border-bottom: 2px dashed #ccc; padding-bottom: 10px; margin-bottom: 20px;'>"
                    + "<h1 style='color: #1e3a8a; margin: 0;'>Moda UTP</h1>"
                    + "<p style='color: #666; margin: 5px 0 0 0;'>Comprobante de Pago Electrónico</p>"
                    + "</div>"
                    + "<p><strong>¡Hola " + (pedido.getUsuario() != null ? pedido.getUsuario().getNombre() : "Cliente") + "!</strong></p>"
                    + "<p>Gracias por tu compra " + tipoCompra + ". Aquí tienes el detalle de tu boleta:</p>"
                    
                    + "<div style='background-color: #f9f9f9; padding: 15px; border-radius: 8px; margin-bottom: 20px;'>"
                    + "<h3 style='margin-top: 0; color: #333; font-size: 16px; border-bottom: 1px solid #ccc; padding-bottom: 5px;'>Datos del Cliente</h3>"
                    + "<p style='margin: 5px 0; font-size: 14px;'><strong>Nombre:</strong> " + (pedido.getUsuario() != null ? pedido.getUsuario().getNombre() : "N/A") + "</p>"
                    + "<p style='margin: 5px 0; font-size: 14px;'><strong>Documento:</strong> " + (pedido.getUsuario() != null && pedido.getUsuario().getNumeroDocumento() != null ? pedido.getUsuario().getNumeroDocumento() : "N/A") + "</p>"
                    + "<p style='margin: 5px 0; font-size: 14px;'><strong>Email:</strong> " + correoCliente + "</p>"
                    + "<p style='margin: 5px 0; font-size: 14px;'><strong>Celular:</strong> " + (pedido.getUsuario() != null && pedido.getUsuario().getTelefono() != null ? pedido.getUsuario().getTelefono() : "N/A") + "</p>"
                    + "<p style='margin: 5px 0; font-size: 14px;'><strong>Dirección:</strong> " + (pedido.getDireccion() != null ? pedido.getDireccion().getDireccionExacta() : "Recojo en Tienda / Por confirmar") + "</p>"
                    + "</div>"
                    
                    + "<h3 style='color: #333; font-size: 16px; border-bottom: 1px solid #ccc; padding-bottom: 5px;'>Detalle de Compra</h3>"
                    + "<table style='width: 100%; border-collapse: collapse; margin-bottom: 20px; font-size: 14px;'>"
                    + "<tr style='border-bottom: 1px dashed #ccc; text-align: left;'>"
                    + "<th style='padding: 8px 0;'>Cant.</th>"
                    + "<th style='padding: 8px 0;'>Descripción</th>"
                    + "<th style='padding: 8px 0;'>P. Unit</th>"
                    + "<th style='padding: 8px 0; text-align: right;'>Importe</th>"
                    + "</tr>";

            if(pedido.getDetalles() != null) {
                for (var detalle : pedido.getDetalles()) {
                    String nombreProd = detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto";
                    String marca = detalle.getProducto() != null && detalle.getProducto().getMarca() != null ? " (" + detalle.getProducto().getMarca() + ")" : "";
                    htmlBody += "<tr style='border-bottom: 1px solid #eee;'>"
                            + "<td style='padding: 8px 0;'>" + detalle.getCantidad() + "</td>"
                            + "<td style='padding: 8px 0;'>" + nombreProd + marca + "</td>"
                            + "<td style='padding: 8px 0;'>S/ " + String.format("%.2f", detalle.getPrecioUnitario()) + "</td>"
                            + "<td style='padding: 8px 0; text-align: right;'>S/ " + String.format("%.2f", detalle.getCantidad() * detalle.getPrecioUnitario()) + "</td>"
                            + "</tr>";
                }
            }

            double total = pedido.getTotal();
            double igv = total * 0.18;
            double subtotal = total - igv;

            htmlBody += "</table>"
                    + "<table style='width: 100%; border-top: 2px dashed #ccc; margin-top: 15px; padding-top: 10px; font-size: 14px;'>"
                    + "<tr><td style='padding: 5px 0; color: #555;'>Subtotal:</td><td style='text-align: right;'><strong>S/ " + String.format("%.2f", subtotal) + "</strong></td></tr>"
                    + "<tr><td style='padding: 5px 0; color: #555;'>I.G.V. (18%):</td><td style='text-align: right;'><strong>S/ " + String.format("%.2f", igv) + "</strong></td></tr>"
                    + "<tr><td style='padding: 15px 0; font-size: 18px; color: #059669;'><strong>TOTAL A PAGAR:</strong></td><td style='text-align: right; font-size: 18px; color: #059669;'><strong>S/ " + String.format("%.2f", total) + "</strong></td></tr>"
                    + "</table>"
                    + "<p style='margin-top: 10px; font-size: 13px; color: #666; text-align: right;'>Método de pago: <strong>" + (metodoPago != null ? metodoPago.toUpperCase() : "EFECTIVO") + "</strong></p>"
                    
                    + "<div style='text-align: center; color: #888; font-size: 13px; margin-top: 30px; border-top: 1px solid #eee; padding-top: 15px;'>"
                    + "<p style='font-style: italic; margin-bottom: 5px;'>¡Gracias por tu preferencia!</p>"
                    + "<p style='margin-top: 0;'>Puedes revisar el historial de tus compras registrándote en nuestra tienda web con este mismo correo.</p>"
                    + "</div>"
                    + "</div>";

            helper.setText(htmlBody, true);

            javaMailSender.send(mensaje);
            System.out.println("Correo de boleta enviado exitosamente a: " + correoCliente);

        } catch (MessagingException e) {
            System.err.println("Error enviando el correo de boleta: " + e.getMessage());
        }
    }
}
