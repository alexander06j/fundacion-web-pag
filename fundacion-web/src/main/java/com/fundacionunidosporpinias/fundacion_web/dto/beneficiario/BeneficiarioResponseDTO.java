package com.fundacionunidosporpinias.fundacion_web.dto.beneficiario;

import com.fundacionunidosporpinias.fundacion_web.dto.AuditoriaDTO;
import com.fundacionunidosporpinias.fundacion_web.model.Sexo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
    private LocalDate fechaNacimiento;
    private Sexo sexo;
    private String detalles;

    private Long parroquiaId;

    private String parroquiaNombre;

}
