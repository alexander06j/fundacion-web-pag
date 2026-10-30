package com.fundacionunidosporpinias.fundacion_web.dto.entrega;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DetalleEntregaRequestDTO {
    @NotNull
    private Long productoId;

    @NotNull
    @Min(1)
    private Integer cantidad;

}
