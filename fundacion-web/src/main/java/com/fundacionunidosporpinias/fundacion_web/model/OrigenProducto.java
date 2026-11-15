package com.fundacionunidosporpinias.fundacion_web.model;

/**
 * Origen de adquisición del producto dentro del inventario de la fundación.
 */
public enum OrigenProducto {

    DONADO("Donado"),
    COMPRADO("Comprado");

    private final String descripcion;

    OrigenProducto(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
