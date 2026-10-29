package com.fundacionunidosporpinias.fundacion_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Parroquia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @NotBlank(message = "El canton es obligatorio.")
    private String canton;

    @OneToMany(mappedBy = "parroquia", cascade = CascadeType.ALL)
    private List<Beneficiario> beneficiarios = new ArrayList<>();

}
