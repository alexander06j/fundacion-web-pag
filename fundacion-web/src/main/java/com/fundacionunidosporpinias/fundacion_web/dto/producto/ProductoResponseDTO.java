package com.fundacionunidosporpinias.fundacion_web.dto.producto;

import com.fundacionunidosporpinias.fundacion_web.dto.AuditoriaDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductoResponseDTO extends AuditoriaDTO {

    private Long id;

    private String nombre;
    private Integer stock;
    private String descripcion;
    private String categoria;
}
