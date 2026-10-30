package com.fundacionunidosporpinias.fundacion_web.dto.entrega;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntregaResponseDTO {

    private Long id;

    private LocalDate fechaEntrega;
    private String observacion;

    private Long beneficiarioId;

    private String beneficiarioNombre;
    private String parroquiaNombre;

    private List<DetalleEntregaResponseDTO> detalles;

}
