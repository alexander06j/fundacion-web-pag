package com.fundacionunidosporpinias.fundacion_web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Campos de auditoría expuestos en los DTO de respuesta.
 * MapStruct los completa automáticamente desde la entidad (Auditable)
 * porque los nombres coinciden.
 */
@Getter @Setter
public abstract class AuditoriaDTO {

    private static final DateTimeFormatter FMT_CORTO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private String modificadoPor;
    private LocalDateTime fechaModificacion;

    @JsonIgnore
    public String getFechaCreacionFormateada() {
        return fechaCreacion != null ? fechaCreacion.format(FMT_CORTO) : null;
    }

    @JsonIgnore
    public String getFechaModificacionFormateada() {
        return fechaModificacion != null ? fechaModificacion.format(FMT_CORTO) : null;
    }

    @JsonIgnore
    public boolean isTieneAuditoria() {
        return creadoPor != null || modificadoPor != null || fechaCreacion != null || fechaModificacion != null;
    }

    @JsonIgnore
    public boolean isFueEditado() {
        return fechaCreacion != null
                && fechaModificacion != null
                && fechaModificacion.isAfter(fechaCreacion.plusSeconds(1));
    }

}
