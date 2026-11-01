package com.fundacionunidosporpinias.fundacion_web.dto.beneficiario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BeneficiarioResumenDTO {

    private Long id;

    private String cedula;

    private String nombreCompleto;


}
