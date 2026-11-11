package com.fundacionunidosporpinias.fundacion_web.model;

/**
 * Roles disponibles en el sistema.
 * ADMIN: control total (incluye eliminación de registros y gestión de usuarios).
 * OPERADOR: operación diaria (crear/editar productos, beneficiarios y entregas).
 * LECTOR: solo lectura (puede consultar dashboard, listados y detalles sin editar ni borrar).
 */
public enum Rol {

    ADMIN("Administrador"),
    OPERADOR("Operador"),
    LECTOR("Lector");

    private final String descripcion;

    Rol(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
