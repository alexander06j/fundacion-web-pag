package com.fundacionunidosporpinias.fundacion_web.dto.parroquia;

import com.fundacionunidosporpinias.fundacion_web.dto.AuditoriaDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParroquiaResponseDTO extends AuditoriaDTO {

    private Long id;
    private String nombre;
    private String canton;

}
