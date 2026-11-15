package com.fundacionunidosporpinias.fundacion_web.model;

/**
 * Sexo biológico del beneficiario registrado para clasificación y filtrado estadístico.
 */
public enum Sexo {

    MASCULINO("Masculino"),
    FEMENINO("Femenino");

    private final String descripcion;

    Sexo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
