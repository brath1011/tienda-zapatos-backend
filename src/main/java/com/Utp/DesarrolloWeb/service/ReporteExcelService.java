package com.Utp.DesarrolloWeb.service;

import com.Utp.DesarrolloWeb.model.Pedido;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReporteExcelService {

    public ByteArrayInputStream generarReporteVentas(List<Pedido> pedidos) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte de Ventas");

            // --- ESTILOS ---
            
            // Estilo para Cabeceras (Fondo Azul, Texto Blanco, Negrita)
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Estilo para Moneda (S/ #,##0.00)
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("\"S/ \"#,##0.00"));

            // Estilo para Fila de Totales (Negrita, Línea Doble Superior, Moneda)
            CellStyle totalStyle = workbook.createCellStyle();
            totalStyle.cloneStyleFrom(currencyStyle);
            totalStyle.setBorderTop(BorderStyle.DOUBLE);
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            
            // Estilo para Texto de Totales (Negrita, Línea Doble Superior, Alineación Derecha)
            CellStyle totalLabelStyle = workbook.createCellStyle();
            totalLabelStyle.setBorderTop(BorderStyle.DOUBLE);
            totalLabelStyle.setAlignment(HorizontalAlignment.RIGHT);
            totalLabelStyle.setFont(totalFont);

            // --- CABECERAS ---
            String[] headers = {"N° Pedido", "Fecha", "Cliente", "Email", "Repartidor", "Zona Reparto", "Tipo Venta", "Estado", "Artículos", "Total Venta"};
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- DATOS ---
            int rowIdx = 1;
            double sumaTotal = 0.0;

            for (Pedido pedido : pedidos) {
                Row row = sheet.createRow(rowIdx++);

                // N° Pedido
                row.createCell(0).setCellValue(pedido.getIdPedido());
                
                // Fecha
                row.createCell(1).setCellValue(pedido.getFecha() != null ? pedido.getFecha().toString() : "");

                // Cliente y Email
                if (pedido.getUsuario() != null) {
                    row.createCell(2).setCellValue(pedido.getUsuario().getNombre());
                    row.createCell(3).setCellValue(pedido.getUsuario().getEmail());
                } else {
                    row.createCell(2).setCellValue("N/A");
                    row.createCell(3).setCellValue("N/A");
                }
                
                // Repartidor y Zona Reparto
                if (pedido.getRepartidor() != null) {
                    row.createCell(4).setCellValue(pedido.getRepartidor().getNombre());
                    row.createCell(5).setCellValue(pedido.getRepartidor().getZona() != null ? pedido.getRepartidor().getZona() : "Sin asignar");
                } else {
                    row.createCell(4).setCellValue("N/A");
                    row.createCell(5).setCellValue("N/A");
                }

                // Tipo Venta y Estado
                row.createCell(6).setCellValue(pedido.getTipoPedido() != null ? pedido.getTipoPedido() : "WEB");
                row.createCell(7).setCellValue(pedido.getEstado() != null ? pedido.getEstado() : "PENDIENTE");

                // Cantidad de Artículos (Suma de las cantidades de los detalles)
                int cantidadArticulos = 0;
                if (pedido.getDetalles() != null) {
                    cantidadArticulos = pedido.getDetalles().stream().mapToInt(d -> d.getCantidad()).sum();
                }
                row.createCell(8).setCellValue(cantidadArticulos);

                // Total Venta (con formato de moneda)
                Cell totalCell = row.createCell(9);
                double total = pedido.getTotal() != null ? pedido.getTotal() : 0.0;
                totalCell.setCellValue(total);
                totalCell.setCellStyle(currencyStyle);
                
                sumaTotal += total;
            }

            // --- FILA DE RESUMEN CONTABLE ---
            Row totalRow = sheet.createRow(rowIdx + 1); // Dejamos una fila en blanco por elegancia
            Cell labelCell = totalRow.createCell(8);
            labelCell.setCellValue("TOTAL GENERAL:");
            labelCell.setCellStyle(totalLabelStyle);

            Cell sumCell = totalRow.createCell(9);
            sumCell.setCellValue(sumaTotal);
            sumCell.setCellStyle(totalStyle);

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir archivo al stream
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
            
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el reporte de Excel: " + e.getMessage());
        }
    }
}
