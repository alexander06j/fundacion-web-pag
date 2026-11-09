package com.fundacionunidosporpinias.fundacion_web.service.imp;

import com.fundacionunidosporpinias.fundacion_web.model.Beneficiario;
import com.fundacionunidosporpinias.fundacion_web.model.DetalleEntrega;
import com.fundacionunidosporpinias.fundacion_web.model.Entrega;
import com.fundacionunidosporpinias.fundacion_web.model.Parroquia;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generador del documento PDF "Acta de Entrega de Ayuda Social" con formato
 * institucional listo para impresión y firma física del beneficiario.
 */
@Component
public class ActaEntregaPdfGenerator {

    private static final Color AZUL_INSTITUCIONAL = new Color(30, 58, 138);   // #1e3a8a
    private static final Color VERDE_ESMERALDA = new Color(16, 185, 129);     // #10b981
    private static final Color GRIS_CABECERA = new Color(241, 245, 249);      // #f1f5f9
    private static final Color GRIS_FILA_PAR = new Color(248, 250, 252);      // #f8fafc
    private static final Color GRIS_BORDE = new Color(203, 213, 225);         // #cbd5e1
    private static final Color TEXTO_OSCURO = new Color(15, 23, 42);          // #0f172a
    private static final Color TEXTO_SECUNDARIO = new Color(100, 116, 139);   // #64748b

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(Entrega entrega, String nombreResponsable) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 42f, 42f, 38f, 42f);

        PdfWriter.getInstance(document, baos);
        document.addTitle("Acta de Entrega #" + entrega.getId() + " - Fundación Unidos por Piñas");
        document.addAuthor("Fundación Unidos por Piñas");
        document.open();

        Font fontTituloBlanco = new Font(Font.HELVETICA, 15, Font.BOLD, Color.WHITE);
        Font fontSubBlanco = new Font(Font.HELVETICA, 9.5f, Font.NORMAL, new Color(219, 234, 254));
        Font fontActaNumero = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        Font fontSeccion = new Font(Font.HELVETICA, 10.5f, Font.BOLD, AZUL_INSTITUCIONAL);
        Font fontLabel = new Font(Font.HELVETICA, 9f, Font.BOLD, TEXTO_OSCURO);
        Font fontValor = new Font(Font.HELVETICA, 9.5f, Font.NORMAL, TEXTO_OSCURO);
        Font fontTablaHeader = new Font(Font.HELVETICA, 9.5f, Font.BOLD, Color.WHITE);
        Font fontTotal = new Font(Font.HELVETICA, 10f, Font.BOLD, AZUL_INSTITUCIONAL);
        Font fontClausula = new Font(Font.HELVETICA, 9.5f, Font.NORMAL, TEXTO_OSCURO);
        Font fontPie = new Font(Font.HELVETICA, 8f, Font.ITALIC, TEXTO_SECUNDARIO);

        // 1. Encabezado institucional
        PdfPTable header = new PdfPTable(new float[]{68f, 32f});
        header.setWidthPercentage(100f);

        PdfPCell celdaInst = new PdfPCell();
        celdaInst.setBackgroundColor(AZUL_INSTITUCIONAL);
        celdaInst.setBorder(Rectangle.NO_BORDER);
        celdaInst.setPadding(14f);
        Paragraph pInst = new Paragraph("FUNDACIÓN UNIDOS POR PIÑAS", fontTituloBlanco);
        Paragraph pSub = new Paragraph("Sistema de Gestión Social y Comunitaria — Cantón Piñas, El Oro", fontSubBlanco);
        pSub.setSpacingBefore(3f);
        celdaInst.addElement(pInst);
        celdaInst.addElement(pSub);

        String numeroActa = String.format("ACTA N.° %06d", entrega.getId());
        String fechaEntregaStr = entrega.getFechaEntrega() != null
                ? entrega.getFechaEntrega().format(FMT_FECHA)
                : "-";

        PdfPCell celdaActa = new PdfPCell();
        celdaActa.setBackgroundColor(AZUL_INSTITUCIONAL);
        celdaActa.setBorder(Rectangle.NO_BORDER);
        celdaActa.setPadding(14f);
        celdaActa.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph pActa = new Paragraph(numeroActa, fontActaNumero);
        pActa.setAlignment(Element.ALIGN_RIGHT);
        Paragraph pFecha = new Paragraph("Fecha: " + fechaEntregaStr, fontSubBlanco);
        pFecha.setAlignment(Element.ALIGN_RIGHT);
        pFecha.setSpacingBefore(4f);
        celdaActa.addElement(pActa);
        celdaActa.addElement(pFecha);

        header.addCell(celdaInst);
        header.addCell(celdaActa);
        document.add(header);

        // Franja verde institucional
        PdfPTable franja = new PdfPTable(1);
        franja.setWidthPercentage(100f);
        franja.setSpacingAfter(14f);
        PdfPCell celdaFranja = new PdfPCell(new Phrase(" "));
        celdaFranja.setFixedHeight(4f);
        celdaFranja.setBackgroundColor(VERDE_ESMERALDA);
        celdaFranja.setBorder(Rectangle.NO_BORDER);
        franja.addCell(celdaFranja);
        document.add(franja);

        // 2. Datos del beneficiario
        Beneficiario ben = entrega.getBeneficiario();
        Parroquia par = ben != null ? ben.getParroquia() : null;
        String nombreBeneficiario = ben != null
                ? (valorSeguro(ben.getNombre()) + " " + valorSeguro(ben.getApellido())).trim()
                : "-";
        String cedulaBeneficiario = ben != null ? valorSeguro(ben.getCedula()) : "-";
        String telefonoBeneficiario = ben != null && ben.getTelefono() != null && !ben.getTelefono().isBlank()
                ? ben.getTelefono() : "No registrado";
        String direccionBeneficiario = ben != null && ben.getDireccion() != null && !ben.getDireccion().isBlank()
                ? ben.getDireccion() : "No registrada";
        String parroquiaTexto = par != null
                ? valorSeguro(par.getNombre()) + " (" + valorSeguro(par.getCanton()) + ")"
                : "-";

        agregarTituloSeccion(document, "1. DATOS DE LA PERSONA BENEFICIARIA", fontSeccion);

        PdfPTable tablaBen = new PdfPTable(new float[]{22f, 34f, 18f, 26f});
        tablaBen.setWidthPercentage(100f);
        tablaBen.setSpacingAfter(14f);

        agregarParCampo(tablaBen, "Beneficiario/a:", nombreBeneficiario, fontLabel, fontValor);
        agregarParCampo(tablaBen, "Cédula / ID:", cedulaBeneficiario, fontLabel, fontValor);
        agregarParCampo(tablaBen, "Parroquia:", parroquiaTexto, fontLabel, fontValor);
        agregarParCampo(tablaBen, "Teléfono:", telefonoBeneficiario, fontLabel, fontValor);

        PdfPCell lblDir = crearCeldaEtiqueta("Dirección / Sector:", fontLabel);
        PdfPCell valDir = crearCeldaValor(direccionBeneficiario, fontValor);
        valDir.setColspan(3);
        tablaBen.addCell(lblDir);
        tablaBen.addCell(valDir);

        document.add(tablaBen);

        // 3. Información del despacho y trazabilidad
        agregarTituloSeccion(document, "2. INFORMACIÓN DEL DESPACHO", fontSeccion);

        PdfPTable tablaInfo = new PdfPTable(new float[]{22f, 28f, 22f, 28f});
        tablaInfo.setWidthPercentage(100f);
        tablaInfo.setSpacingAfter(14f);

        String registradoPor = nombreResponsable != null && !nombreResponsable.isBlank()
                ? nombreResponsable
                : (entrega.getCreadoPor() != null ? entrega.getCreadoPor() : " Administración");
        String fechaRegistro = entrega.getFechaCreacion() != null
                ? entrega.getFechaCreacion().format(FMT_FECHA_HORA)
                : fechaEntregaStr;

        agregarParCampo(tablaInfo, "Fecha de Entrega:", fechaEntregaStr, fontLabel, fontValor);
        agregarParCampo(tablaInfo, "Fecha de Registro:", fechaRegistro, fontLabel, fontValor);

        PdfPCell lblResp = crearCeldaEtiqueta("Registrado por:", fontLabel);
        PdfPCell valResp = crearCeldaValor(registradoPor, fontValor);
        valResp.setColspan(3);
        tablaInfo.addCell(lblResp);
        tablaInfo.addCell(valResp);

        String obs = entrega.getObservacion() != null && !entrega.getObservacion().isBlank()
                ? entrega.getObservacion()
                : "Sin observaciones adicionales.";
        PdfPCell lblObs = crearCeldaEtiqueta("Observaciones:", fontLabel);
        PdfPCell valObs = crearCeldaValor(obs, fontValor);
        valObs.setColspan(3);
        tablaInfo.addCell(lblObs);
        tablaInfo.addCell(valObs);

        document.add(tablaInfo);

        // 4. Tabla de productos entregados
        agregarTituloSeccion(document, "3. DETALLE DE INSUMOS Y PRODUCTOS ENTREGADOS", fontSeccion);

        PdfPTable tablaProd = new PdfPTable(new float[]{10f, 48f, 24f, 18f});
        tablaProd.setWidthPercentage(100f);
        tablaProd.setSpacingAfter(16f);

        agregarHeaderTabla(tablaProd, "#", Element.ALIGN_CENTER, fontTablaHeader);
        agregarHeaderTabla(tablaProd, "Producto / Insumo", Element.ALIGN_LEFT, fontTablaHeader);
        agregarHeaderTabla(tablaProd, "Categoría", Element.ALIGN_LEFT, fontTablaHeader);
        agregarHeaderTabla(tablaProd, "Cantidad", Element.ALIGN_CENTER, fontTablaHeader);

        List<DetalleEntrega> detalles = entrega.getDetalles();
        int totalUnidades = 0;
        int indice = 1;

        if (detalles != null && !detalles.isEmpty()) {
            for (DetalleEntrega det : detalles) {
                Color fondo = (indice % 2 == 0) ? GRIS_FILA_PAR : Color.WHITE;
                String prodNombre = det.getProducto() != null ? valorSeguro(det.getProducto().getNombre()) : "-";
                String prodCat = det.getProducto() != null ? valorSeguro(det.getProducto().getCategoria()) : "-";
                int cant = det.getCantidad() != null ? det.getCantidad() : 0;
                totalUnidades += cant;

                tablaProd.addCell(crearCeldaTabla(String.valueOf(indice), Element.ALIGN_CENTER, fondo, fontValor));
                tablaProd.addCell(crearCeldaTabla(prodNombre, Element.ALIGN_LEFT, fondo, fontValor));
                tablaProd.addCell(crearCeldaTabla(prodCat, Element.ALIGN_LEFT, fondo, fontValor));
                tablaProd.addCell(crearCeldaTabla(cant + " uds.", Element.ALIGN_CENTER, fondo, fontValor));
                indice++;
            }
        } else {
            PdfPCell vacia = crearCeldaTabla("No se registraron productos en esta entrega.", Element.ALIGN_CENTER, Color.WHITE, fontValor);
            vacia.setColspan(4);
            tablaProd.addCell(vacia);
        }

        // Fila resumen de totales
        PdfPCell celdaTotalLbl = new PdfPCell(new Phrase("TOTAL DE UNIDADES ENTREGADAS", fontTotal));
        celdaTotalLbl.setColspan(3);
        celdaTotalLbl.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celdaTotalLbl.setBackgroundColor(GRIS_CABECERA);
        celdaTotalLbl.setBorderColor(GRIS_BORDE);
        celdaTotalLbl.setPadding(8f);

        PdfPCell celdaTotalVal = new PdfPCell(new Phrase(totalUnidades + " uds.", fontTotal));
        celdaTotalVal.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTotalVal.setBackgroundColor(GRIS_CABECERA);
        celdaTotalVal.setBorderColor(GRIS_BORDE);
        celdaTotalVal.setPadding(8f);

        tablaProd.addCell(celdaTotalLbl);
        tablaProd.addCell(celdaTotalVal);
        document.add(tablaProd);

        // 5. Cláusula de constancia física
        agregarTituloSeccion(document, "4. CONSTANCIA DE RECEPCIÓN CONFORME", fontSeccion);

        Paragraph clausula = new Paragraph(
                "Por medio de la presente acta, yo, " + nombreBeneficiario
                        + ", portador/a de la cédula de identidad N.° " + cedulaBeneficiario
                        + ", dejo constancia de haber recibido de manera totalmente gratuita y a entera satisfacción "
                        + "por parte de la Fundación Unidos por Piñas los insumos detallados en el numeral 3 de este documento, "
                        + "destinados exclusivamente al apoyo social comunitario y bienestar familiar.",
                fontClausula
        );
        clausula.setAlignment(Element.ALIGN_JUSTIFIED);
        clausula.setLeading(14f);
        clausula.setSpacingAfter(46f);
        document.add(clausula);

        // 6. Bloque de firmas para constancia física
        PdfPTable firmas = new PdfPTable(new float[]{45f, 10f, 45f});
        firmas.setWidthPercentage(100f);
        firmas.setSpacingAfter(24f);

        PdfPCell firmaBen = crearCeldaFirma(
                "RECIBÍ CONFORME (Beneficiario/a)",
                nombreBeneficiario,
                "C.I.: " + cedulaBeneficiario,
                fontLabel,
                fontValor
        );

        PdfPCell espacio = new PdfPCell(new Phrase(" "));
        espacio.setBorder(Rectangle.NO_BORDER);

        PdfPCell firmaFundacion = crearCeldaFirma(
                "ENTREGUÉ CONFORME",
                "Fundación Unidos por Piñas",
                "Responsable: " + registradoPor,
                fontLabel,
                fontValor
        );

        firmas.addCell(firmaBen);
        firmas.addCell(espacio);
        firmas.addCell(firmaFundacion);
        document.add(firmas);

        // Pie de página con marca de tiempo
        Paragraph pie = new Paragraph(
                "Documento generado por el Sistema de Gestión Social — Fundación Unidos por Piñas el "
                        + LocalDateTime.now().format(FMT_FECHA_HORA) + ".",
                fontPie
        );
        pie.setAlignment(Element.ALIGN_CENTER);
        document.add(pie);

        document.close();
        return baos.toByteArray();
    }

    private void agregarTituloSeccion(Document doc, String texto, Font font) {
        Paragraph p = new Paragraph(texto, font);
        p.setSpacingBefore(2f);
        p.setSpacingAfter(6f);
        doc.add(p);
    }

    private void agregarParCampo(PdfPTable tabla, String etiqueta, String valor, Font fLabel, Font fValor) {
        tabla.addCell(crearCeldaEtiqueta(etiqueta, fLabel));
        tabla.addCell(crearCeldaValor(valor, fValor));
    }

    private PdfPCell crearCeldaEtiqueta(String texto, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setBackgroundColor(GRIS_CABECERA);
        c.setBorderColor(GRIS_BORDE);
        c.setPadding(7f);
        return c;
    }

    private PdfPCell crearCeldaValor(String texto, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "-", font));
        c.setBackgroundColor(Color.WHITE);
        c.setBorderColor(GRIS_BORDE);
        c.setPadding(7f);
        return c;
    }

    private void agregarHeaderTabla(PdfPTable tabla, String texto, int align, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setBackgroundColor(AZUL_INSTITUCIONAL);
        c.setBorderColor(AZUL_INSTITUCIONAL);
        c.setHorizontalAlignment(align);
        c.setPadding(8f);
        tabla.addCell(c);
    }

    private PdfPCell crearCeldaTabla(String texto, int align, Color bg, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setBackgroundColor(bg);
        c.setBorderColor(GRIS_BORDE);
        c.setHorizontalAlignment(align);
        c.setPadding(7f);
        return c;
    }

    private PdfPCell crearCeldaFirma(String cargo, String linea1, String linea2, Font fBold, Font fNormal) {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.TOP);
        c.setBorderColor(TEXTO_OSCURO);
        c.setBorderWidthTop(1f);
        c.setPaddingTop(6f);

        Paragraph pCargo = new Paragraph(cargo, fBold);
        pCargo.setAlignment(Element.ALIGN_CENTER);

        Paragraph p1 = new Paragraph(linea1, fNormal);
        p1.setAlignment(Element.ALIGN_CENTER);
        p1.setSpacingBefore(2f);

        Paragraph p2 = new Paragraph(linea2, fNormal);
        p2.setAlignment(Element.ALIGN_CENTER);

        c.addElement(pCargo);
        c.addElement(p1);
        c.addElement(p2);
        return c;
    }

    private String valorSeguro(String s) {
        return s != null ? s : "";
    }


}
