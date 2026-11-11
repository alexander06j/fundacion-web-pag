package com.fundacionunidosporpinias.fundacion_web.dto.usuario;

import com.fundacionunidosporpinias.fundacion_web.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String nombreCompleto;
    private Rol rol;
    private boolean activo;

}
