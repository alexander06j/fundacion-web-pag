package com.fundacionunidosporpinias.fundacion_web.dto.beneficiario;

import com.fundacionunidosporpinias.fundacion_web.model.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BeneficiarioRequestDTO {

    @NotBlank(message = "La cédula es obligatoria.")
    private String cedula;

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    private String apellido;

    private String telefono;
    private String direccion;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @NotNull(message = "Debe seleccionar el sexo.")
    private Sexo sexo;

    private String detalles;

    @NotNull(message = "Debe seleccionar una parroquia.")
    private Long parroquiaId;

}
