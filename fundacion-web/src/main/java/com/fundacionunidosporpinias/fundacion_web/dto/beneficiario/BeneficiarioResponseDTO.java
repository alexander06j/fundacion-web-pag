package com.fundacionunidosporpinias.fundacion_web.dto.beneficiario;

import com.fundacionunidosporpinias.fundacion_web.dto.AuditoriaDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BeneficiarioResponseDTO extends AuditoriaDTO {

    private Long id;

    private String cedula;
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;

    private Long parroquiaId;

    private String parroquiaNombre;

}
