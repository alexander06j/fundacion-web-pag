package com.fundacionunidosporpinias.fundacion_web.dto.entrega;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntregaResumenDTO {

    private Long id;

    private LocalDate fechaEntrega;

    private String beneficiarioNombre;

    private String parroquiaNombre;

}
