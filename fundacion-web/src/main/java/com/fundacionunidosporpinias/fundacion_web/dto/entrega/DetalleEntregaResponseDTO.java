package com.fundacionunidosporpinias.fundacion_web.dto.entrega;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DetalleEntregaResponseDTO {

    private Long productoId;
    private String productoNombre;
    private Integer cantidad;

}
