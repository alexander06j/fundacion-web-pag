package com.fundacionunidosporpinias.fundacion_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull
    @Min(value = 0)
    @Column(nullable = false)
    private Integer stock;

    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @OneToMany(mappedBy = "producto",cascade = CascadeType.ALL)
    private List<DetalleEntrega> detallesEntrega = new ArrayList<>();

}
