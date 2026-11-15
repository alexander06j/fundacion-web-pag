package com.fundacionunidosporpinias.fundacion_web.service;

import com.fundacionunidosporpinias.fundacion_web.model.Sexo;

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
     * Exporta el listado de beneficiarios (respetando filtros activos) a Excel (.xlsx).
     */
    byte[] exportarBeneficiariosExcel(String q, Sexo sexo, LocalDate desde, LocalDate hasta);

    default byte[] exportarBeneficiariosExcel(String q) {
        return exportarBeneficiariosExcel(q, null, null, null);
    }

    /**
     * Exporta el historial de entregas y su desglose de productos a Excel (.xlsx).
     */
    byte[] exportarEntregasExcel(String q, LocalDate desde, LocalDate hasta);
}
