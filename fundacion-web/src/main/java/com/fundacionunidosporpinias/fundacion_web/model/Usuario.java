package com.fundacionunidosporpinias.fundacion_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    // Contraseña cifrada con BCrypt (nunca en texto plano)
    @Column(nullable = false)
    @NotBlank
    private String password;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private Rol rol;

    @Column(nullable = false)
    private boolean activo = true;


}
