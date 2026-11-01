package com.fundacionunidosporpinias.fundacion_web.dto.parroquia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParroquiaResponseDTO {

    private Long id;
    private String nombre;
    private String canton;

}
