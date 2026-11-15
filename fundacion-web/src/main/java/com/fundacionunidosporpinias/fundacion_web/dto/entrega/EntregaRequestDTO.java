package com.fundacionunidosporpinias.fundacion_web.dto.entrega;

import jakarta.validation.Valid;
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

    @NotNull(message = "Debe seleccionar un beneficiario.")
    private Long beneficiarioId;

    private String observacion;

    @Valid
    @NotEmpty(message = "Debe agregar al menos un producto a la entrega.")
    private List<DetalleEntregaRequestDTO> detalles;

}
