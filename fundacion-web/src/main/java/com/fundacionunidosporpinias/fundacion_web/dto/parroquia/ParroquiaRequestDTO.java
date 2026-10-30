package com.fundacionunidosporpinias.fundacion_web.dto.parroquia;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParroquiaRequestDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String canton;

}
