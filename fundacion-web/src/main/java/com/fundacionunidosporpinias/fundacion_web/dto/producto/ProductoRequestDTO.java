package com.fundacionunidosporpinias.fundacion_web.dto.producto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductoRequestDTO {

    @NotBlank
    private String nombre;

    @NotNull
    @Min(0)
    private Integer stock;

    private String descripcion;

    @NotBlank
    private String categoria;
}
