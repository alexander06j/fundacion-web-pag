package com.fundacionunidosporpinias.fundacion_web.service.imp;

import com.fundacionunidosporpinias.fundacion_web.model.Beneficiario;
import com.fundacionunidosporpinias.fundacion_web.model.DetalleEntrega;
import com.fundacionunidosporpinias.fundacion_web.model.Entrega;
import com.fundacionunidosporpinias.fundacion_web.model.Parroquia;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * Generador de libros Excel (.xlsx) institucionales mediante Apache POI.
 */
@Component
public class ExcelReporteGenerator {

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarBeneficiariosXlsx(List<Beneficiario> beneficiarios,
                                           Map<Long, Long> entregasPorBeneficiario,
                                           String filtro) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            EstilosExcel estilos = new EstilosExcel(wb);
            Sheet sheet = wb.createSheet("Beneficiarios");

            String[] columnas = {
                    "ID", "Cédula", "Nombres", "Apellidos", "Teléfono", "Dirección",
                    "Parroquia", "Cantón", "Entregas Recibidas",
                    "Creado Por", "Fecha Creación", "Modificado Por", "Última Modificación"
            };

            String detalleFiltro = (filtro != null && !filtro.isBlank())
                    ? "Búsqueda: \"" + filtro + "\""
                    : "Todos los registros";
            crearEncabezadoHoja(
                    sheet,
                    "FUNDACIÓN UNIDOS POR PIÑAS — PADRÓN DE BENEFICIARIOS",
                    "Generado: " + LocalDateTime.now().format(FMT_FECHA_HORA)
                            + "  |  " + detalleFiltro
                            + "  |  Total: " + beneficiarios.size() + " beneficiario(s)",
                    columnas,
                    estilos
            );

            int filaIdx = 4;
            long sumaEntregas = 0;

            for (int i = 0; i < beneficiarios.size(); i++) {
                Beneficiario b = beneficiarios.get(i);
                Parroquia p = b.getParroquia();
                CellStyle estiloTexto = (i % 2 == 0) ? estilos.celdaNormal : estilos.celdaPar;
                CellStyle estiloCentro = (i % 2 == 0) ? estilos.celdaCentro : estilos.celdaCentroPar;

                long numEntregas = entregasPorBeneficiario.getOrDefault(b.getId(), 0L);
                sumaEntregas += numEntregas;

                Row row = sheet.createRow(filaIdx++);
                setCeldaNumero(row, 0, b.getId() != null ? b.getId() : 0L, estiloCentro);
                setCeldaTexto(row, 1, b.getCedula(), estiloCentro);
                setCeldaTexto(row, 2, b.getNombre(), estiloTexto);
                setCeldaTexto(row, 3, b.getApellido(), estiloTexto);
                setCeldaTexto(row, 4, valorOGuion(b.getTelefono()), estiloCentro);
                setCeldaTexto(row, 5, valorOGuion(b.getDireccion()), estiloTexto);
                setCeldaTexto(row, 6, p != null ? p.getNombre() : "-", estiloTexto);
                setCeldaTexto(row, 7, p != null ? p.getCanton() : "-", estiloTexto);
                setCeldaNumero(row, 8, numEntregas, estiloCentro);
                setCeldaTexto(row, 9, valorOGuion(b.getCreadoPor()), estiloCentro);
                setCeldaTexto(row, 10, formatearFechaHora(b.getFechaCreacion()), estiloCentro);
                setCeldaTexto(row, 11, valorOGuion(b.getModificadoPor()), estiloCentro);
                setCeldaTexto(row, 12, formatearFechaHora(b.getFechaModificacion()), estiloCentro);
            }

            // Fila resumen
            Row totalRow = sheet.createRow(filaIdx);
            for (int c = 0; c < columnas.length; c++) {
                setCeldaTexto(totalRow, c, "", estilos.resumen);
            }
            setCeldaTexto(totalRow, 0, "TOTAL BENEFICIARIOS: " + beneficiarios.size(), estilos.resumen);
            sheet.addMergedRegion(new CellRangeAddress(filaIdx, filaIdx, 0, 7));
            setCeldaNumero(totalRow, 8, sumaEntregas, estilos.resumenCentro);

            finalizarFormatoHoja(sheet, columnas.length, filaIdx - 1);

            wb.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Error al generar el archivo Excel de beneficiarios", e);
        }
    }

    public byte[] generarEntregasXlsx(List<Entrega> entregas,
                                      String filtro,
                                      LocalDate desde,
                                      LocalDate hasta) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            EstilosExcel estilos = new EstilosExcel(wb);
            String descripcionFiltros = construirDescripcionFiltroEntregas(filtro, desde, hasta);

            // ================= Hoja 1: Resumen de Entregas =================
            Sheet hojaResumen = wb.createSheet("Resumen de Entregas");
            String[] colResumen = {
                    "N.° Acta", "Fecha Entrega", "Cédula", "Beneficiario", "Parroquia",
                    "Productos Entregados", "Total Unidades", "Observaciones",
                    "Registrado Por", "Fecha Registro", "Modificado Por", "Última Modificación"
            };

            crearEncabezadoHoja(
                    hojaResumen,
                    "FUNDACIÓN UNIDOS POR PIÑAS — HISTORIAL DE ENTREGAS",
                    "Generado: " + LocalDateTime.now().format(FMT_FECHA_HORA)
                            + "  |  " + descripcionFiltros
                            + "  |  Total entregas: " + entregas.size(),
                    colResumen,
                    estilos
            );

            int filaRes = 4;
            long totalUnidadesGeneral = 0;

            for (int i = 0; i < entregas.size(); i++) {
                Entrega e = entregas.get(i);
                Beneficiario b = e.getBeneficiario();
                Parroquia p = b != null ? b.getParroquia() : null;
                CellStyle estTxt = (i % 2 == 0) ? estilos.celdaNormal : estilos.celdaPar;
                CellStyle estCtr = (i % 2 == 0) ? estilos.celdaCentro : estilos.celdaCentroPar;

                String resumenProductos = (e.getDetalles() == null || e.getDetalles().isEmpty())
                        ? "-"
                        : e.getDetalles().stream()
                        .map(d -> (d.getProducto() != null ? d.getProducto().getNombre() : "Producto")
                                + " (x" + (d.getCantidad() != null ? d.getCantidad() : 0) + ")")
                        .collect(Collectors.joining(", "));

                int unidadesEntrega = (e.getDetalles() == null) ? 0
                        : e.getDetalles().stream()
                        .mapToInt(d -> d.getCantidad() != null ? d.getCantidad() : 0)
                        .sum();
                totalUnidadesGeneral += unidadesEntrega;

                Row row = hojaResumen.createRow(filaRes++);
                setCeldaTexto(row, 0, String.format("#%06d", e.getId()), estCtr);
                setCeldaTexto(row, 1, e.getFechaEntrega() != null ? e.getFechaEntrega().format(FMT_FECHA) : "-", estCtr);
                setCeldaTexto(row, 2, b != null ? valorOGuion(b.getCedula()) : "-", estCtr);
                setCeldaTexto(row, 3, b != null ? (b.getNombre() + " " + b.getApellido()).trim() : "-", estTxt);
                setCeldaTexto(row, 4, p != null ? p.getNombre() : "-", estTxt);
                setCeldaTexto(row, 5, resumenProductos, estTxt);
                setCeldaNumero(row, 6, unidadesEntrega, estCtr);
                setCeldaTexto(row, 7, valorOGuion(e.getObservacion()), estTxt);
                setCeldaTexto(row, 8, valorOGuion(e.getCreadoPor()), estCtr);
                setCeldaTexto(row, 9, formatearFechaHora(e.getFechaCreacion()), estCtr);
                setCeldaTexto(row, 10, valorOGuion(e.getModificadoPor()), estCtr);
                setCeldaTexto(row, 11, formatearFechaHora(e.getFechaModificacion()), estCtr);
            }

            Row totalResRow = hojaResumen.createRow(filaRes);
            for (int c = 0; c < colResumen.length; c++) {
                setCeldaTexto(totalResRow, c, "", estilos.resumen);
            }
            setCeldaTexto(totalResRow, 0, "TOTAL ENTREGAS: " + entregas.size(), estilos.resumen);
            hojaResumen.addMergedRegion(new CellRangeAddress(filaRes, filaRes, 0, 5));
            setCeldaNumero(totalResRow, 6, totalUnidadesGeneral, estilos.resumenCentro);

            finalizarFormatoHoja(hojaResumen, colResumen.length, filaRes - 1);

            // ================= Hoja 2: Detalle por Producto =================
            Sheet hojaDetalle = wb.createSheet("Detalle por Producto");
            String[] colDetalle = {
                    "N.° Acta", "Fecha Entrega", "Cédula", "Beneficiario",
                    "Parroquia", "Producto", "Categoría", "Cantidad"
            };

            crearEncabezadoHoja(
                    hojaDetalle,
                    "FUNDACIÓN UNIDOS POR PIÑAS — DESGLOSE DE PRODUCTOS ENTREGADOS",
                    "Generado: " + LocalDateTime.now().format(FMT_FECHA_HORA) + "  |  " + descripcionFiltros,
                    colDetalle,
                    estilos
            );

            int filaDet = 4;
            int idxPar = 0;
            for (Entrega e : entregas) {
                if (e.getDetalles() == null) continue;
                Beneficiario b = e.getBeneficiario();
                Parroquia p = b != null ? b.getParroquia() : null;
                for (DetalleEntrega d : e.getDetalles()) {
                    CellStyle estTxt = (idxPar % 2 == 0) ? estilos.celdaNormal : estilos.celdaPar;
                    CellStyle estCtr = (idxPar % 2 == 0) ? estilos.celdaCentro : estilos.celdaCentroPar;

                    Row row = hojaDetalle.createRow(filaDet++);
                    setCeldaTexto(row, 0, String.format("#%06d", e.getId()), estCtr);
                    setCeldaTexto(row, 1, e.getFechaEntrega() != null ? e.getFechaEntrega().format(FMT_FECHA) : "-", estCtr);
                    setCeldaTexto(row, 2, b != null ? valorOGuion(b.getCedula()) : "-", estCtr);
                    setCeldaTexto(row, 3, b != null ? (b.getNombre() + " " + b.getApellido()).trim() : "-", estTxt);
                    setCeldaTexto(row, 4, p != null ? p.getNombre() : "-", estTxt);
                    setCeldaTexto(row, 5, d.getProducto() != null ? d.getProducto().getNombre() : "-", estTxt);
                    setCeldaTexto(row, 6, d.getProducto() != null ? valorOGuion(d.getProducto().getCategoria()) : "-", estTxt);
                    setCeldaNumero(row, 7, d.getCantidad() != null ? d.getCantidad() : 0, estCtr);
                    idxPar++;
                }
            }

            Row totalDetRow = hojaDetalle.createRow(filaDet);
            for (int c = 0; c < colDetalle.length; c++) {
                setCeldaTexto(totalDetRow, c, "", estilos.resumen);
            }
            setCeldaTexto(totalDetRow, 0, "TOTAL DE UNIDADES DESPACHADAS", estilos.resumen);
            hojaDetalle.addMergedRegion(new CellRangeAddress(filaDet, filaDet, 0, 6));
            setCeldaNumero(totalDetRow, 7, totalUnidadesGeneral, estilos.resumenCentro);

            finalizarFormatoHoja(hojaDetalle, colDetalle.length, filaDet - 1);

            wb.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Error al generar el archivo Excel de entregas", e);
        }
    }

    private void crearEncabezadoHoja(Sheet sheet,
                                     String titulo,
                                     String subtitulo,
                                     String[] columnas,
                                     EstilosExcel estilos) {
        Row rowTitulo = sheet.createRow(0);
        rowTitulo.setHeightInPoints(28f);
        for (int c = 0; c < columnas.length; c++) {
            Cell cell = rowTitulo.createCell(c);
            cell.setCellStyle(estilos.tituloBanner);
            if (c == 0) cell.setCellValue(titulo);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnas.length - 1));

        Row rowSub = sheet.createRow(1);
        rowSub.setHeightInPoints(20f);
        for (int c = 0; c < columnas.length; c++) {
            Cell cell = rowSub.createCell(c);
            cell.setCellStyle(estilos.subtitulo);
            if (c == 0) cell.setCellValue(subtitulo);
        }
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnas.length - 1));

        // Fila 2 en blanco como separador visual
        sheet.createRow(2).setHeightInPoints(8f);

        Row rowHeader = sheet.createRow(3);
        rowHeader.setHeightInPoints(24f);
        for (int c = 0; c < columnas.length; c++) {
            Cell cell = rowHeader.createCell(c);
            cell.setCellValue(columnas[c]);
            cell.setCellStyle(estilos.cabeceraTabla);
        }
    }

    private void finalizarFormatoHoja(Sheet sheet, int numColumnas, int ultimaFilaDatos) {
        sheet.createFreezePane(0, 4);
        if (ultimaFilaDatos >= 3) {
            sheet.setAutoFilter(new CellRangeAddress(3, Math.max(3, ultimaFilaDatos), 0, numColumnas - 1));
        }
        for (int c = 0; c < numColumnas; c++) {
            sheet.autoSizeColumn(c);
            int anchoActual = sheet.getColumnWidth(c);
            // Margen extra y tope razonable (máx ~55 caracteres)
            int ajustado = Math.min(Math.max(anchoActual + 900, 3200), 14500);
            sheet.setColumnWidth(c, ajustado);
        }
    }

    private String construirDescripcionFiltroEntregas(String filtro, LocalDate desde, LocalDate hasta) {
        StringBuilder sb = new StringBuilder();
        if (filtro != null && !filtro.isBlank()) {
            sb.append("Búsqueda: \"").append(filtro.trim()).append("\"");
        }
        if (desde != null || hasta != null) {
            if (!sb.isEmpty()) sb.append(" · ");
            sb.append("Periodo: ")
                    .append(desde != null ? desde.format(FMT_FECHA) : "inicio")
                    .append(" a ")
                    .append(hasta != null ? hasta.format(FMT_FECHA) : "hoy");
        }
        return sb.isEmpty() ? "Todos los registros" : sb.toString();
    }

    private void setCeldaTexto(Row row, int col, String valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor : "-");
        cell.setCellStyle(style);
    }

    private void setCeldaNumero(Row row, int col, long valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(style);
    }

    private String valorOGuion(String s) {
        return (s != null && !s.isBlank()) ? s : "-";
    }

    private String formatearFechaHora(LocalDateTime dt) {
        return dt != null ? dt.format(FMT_FECHA_HORA) : "-";
    }

    /**
     * Paleta de estilos corporativos reutilizable dentro de un mismo libro XLSX.
     */
    private static class EstilosExcel {
        final XSSFCellStyle tituloBanner;
        final XSSFCellStyle subtitulo;
        final XSSFCellStyle cabeceraTabla;
        final XSSFCellStyle celdaNormal;
        final XSSFCellStyle celdaPar;
        final XSSFCellStyle celdaCentro;
        final XSSFCellStyle celdaCentroPar;
        final XSSFCellStyle resumen;
        final XSSFCellStyle resumenCentro;

        EstilosExcel(XSSFWorkbook wb) {
            XSSFColor azulInst = new XSSFColor(new byte[]{(byte) 30, (byte) 58, (byte) 138}, null);
            XSSFColor azulSub = new XSSFColor(new byte[]{(byte) 239, (byte) 246, (byte) 255}, null);
            XSSFColor azulTabla = new XSSFColor(new byte[]{(byte) 15, (byte) 23, (byte) 42}, null);
            XSSFColor grisPar = new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null);
            XSSFColor grisResumen = new XSSFColor(new byte[]{(byte) 226, (byte) 232, (byte) 240}, null);
            XSSFColor grisBorde = new XSSFColor(new byte[]{(byte) 203, (byte) 213, (byte) 225}, null);

            XSSFFont fontTitulo = wb.createFont();
            fontTitulo.setBold(true);
            fontTitulo.setFontHeightInPoints((short) 13);
            fontTitulo.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));

            XSSFFont fontSub = wb.createFont();
            fontSub.setItalic(true);
            fontSub.setFontHeightInPoints((short) 9);
            fontSub.setColor(azulInst);

            XSSFFont fontCabecera = wb.createFont();
            fontCabecera.setBold(true);
            fontCabecera.setFontHeightInPoints((short) 10);
            fontCabecera.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));

            XSSFFont fontCuerpo = wb.createFont();
            fontCuerpo.setFontHeightInPoints((short) 10);

            XSSFFont fontNegrita = wb.createFont();
            fontNegrita.setBold(true);
            fontNegrita.setFontHeightInPoints((short) 10);
            fontNegrita.setColor(azulInst);

            tituloBanner = wb.createCellStyle();
            tituloBanner.setFillForegroundColor(azulInst);
            tituloBanner.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            tituloBanner.setAlignment(HorizontalAlignment.LEFT);
            tituloBanner.setVerticalAlignment(VerticalAlignment.CENTER);
            tituloBanner.setFont(fontTitulo);

            subtitulo = wb.createCellStyle();
            subtitulo.setFillForegroundColor(azulSub);
            subtitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            subtitulo.setAlignment(HorizontalAlignment.LEFT);
            subtitulo.setVerticalAlignment(VerticalAlignment.CENTER);
            subtitulo.setFont(fontSub);

            cabeceraTabla = wb.createCellStyle();
            cabeceraTabla.setFillForegroundColor(azulTabla);
            cabeceraTabla.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cabeceraTabla.setAlignment(HorizontalAlignment.CENTER);
            cabeceraTabla.setVerticalAlignment(VerticalAlignment.CENTER);
            cabeceraTabla.setFont(fontCabecera);
            aplicarBordes(cabeceraTabla, grisBorde);

            celdaNormal = wb.createCellStyle();
            celdaNormal.setVerticalAlignment(VerticalAlignment.CENTER);
            celdaNormal.setFont(fontCuerpo);
            aplicarBordes(celdaNormal, grisBorde);

            celdaPar = wb.createCellStyle();
            celdaPar.cloneStyleFrom(celdaNormal);
            celdaPar.setFillForegroundColor(grisPar);
            celdaPar.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            celdaCentro = wb.createCellStyle();
            celdaCentro.cloneStyleFrom(celdaNormal);
            celdaCentro.setAlignment(HorizontalAlignment.CENTER);

            celdaCentroPar = wb.createCellStyle();
            celdaCentroPar.cloneStyleFrom(celdaPar);
            celdaCentroPar.setAlignment(HorizontalAlignment.CENTER);

            resumen = wb.createCellStyle();
            resumen.setFillForegroundColor(grisResumen);
            resumen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            resumen.setVerticalAlignment(VerticalAlignment.CENTER);
            resumen.setFont(fontNegrita);
            aplicarBordes(resumen, grisBorde);

            resumenCentro = wb.createCellStyle();
            resumenCentro.cloneStyleFrom(resumen);
            resumenCentro.setAlignment(HorizontalAlignment.CENTER);
        }

        private static void aplicarBordes(XSSFCellStyle style, XSSFColor color) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setTopBorderColor(color);
            style.setBottomBorderColor(color);
            style.setLeftBorderColor(color);
            style.setRightBorderColor(color);
        }
    }




}
