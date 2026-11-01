package com.fundacionunidosporpinias.fundacion_web.dto.entrega;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntregaRequestDTO {

    @NotNull
    private Long beneficiarioId;

    private String observacion;

    @NotEmpty
    private List<DetalleEntregaRequestDTO> detalles;

}
