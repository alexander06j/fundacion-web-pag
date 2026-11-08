package com.fundacionunidosporpinias.fundacion_web.dto.usuario;

import com.fundacionunidosporpinias.fundacion_web.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "Debe tener entre 4 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "Solo se permiten letras, números, punto, guion y guion bajo")
    private String username;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombreCompleto;

    // Se valida en el controlador: obligatoria al crear, opcional al editar
    private String password;

    private String confirmarPassword;

    @NotNull(message = "Seleccione un rol")
    private Rol rol;


}
