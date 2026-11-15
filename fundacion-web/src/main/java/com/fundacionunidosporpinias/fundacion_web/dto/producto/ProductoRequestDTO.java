package com.fundacionunidosporpinias.fundacion_web.dto.producto;

import com.fundacionunidosporpinias.fundacion_web.model.OrigenProducto;
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

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @NotNull(message = "El stock es obligatorio.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    private Integer stock;

    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria.")
    private String categoria;

    @NotNull(message = "Debe seleccionar el origen del producto.")
    private OrigenProducto origen;
}
