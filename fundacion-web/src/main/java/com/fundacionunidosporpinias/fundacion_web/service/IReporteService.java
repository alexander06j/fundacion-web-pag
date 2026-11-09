package com.fundacionunidosporpinias.fundacion_web.service;

import java.time.LocalDate;

/**
 * Servicio de generación de reportes institucionales en PDF y Excel (XLSX).
 */
public interface IReporteService {

    /**
     * Genera el Acta de Entrega formal en PDF lista para imprimir y firmar.
     */
    byte[] generarActaEntregaPdf(Long entregaId);

    /**
     * Exporta el listado de beneficiarios (respetando el filtro de búsqueda opcional) a Excel (.xlsx).
     */
    byte[] exportarBeneficiariosExcel(String q);

    /**
     * Exporta el historial de entregas y su desglose de productos a Excel (.xlsx).
     */
    byte[] exportarEntregasExcel(String q, LocalDate desde, LocalDate hasta);

}
