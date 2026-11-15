package com.fundacionunidosporpinias.fundacion_web.config;


import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaResponseDTO;

import com.fundacionunidosporpinias.fundacion_web.service.IParroquiaService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


import java.util.*;

@Configuration
public class DataInitializer {

    @Bean
    @Order(10)
    public CommandLineRunner initData(IParroquiaService parroquiaService) {
        return args -> {
            try {
                // Cargar catálogo territorial de Piñas y cantones cercanos sin duplicar existentes
                Set<String> parroquiasExistentes = new HashSet<>();
                for (ParroquiaResponseDTO existente : parroquiaService.findAll()) {
                    if (existente.getNombre() != null) {
                        parroquiasExistentes.add(normalizarNombre(existente.getNombre()));
                    }
                }

                int nuevasCreadas = 0;
                for (ParroquiaRequestDTO req : construirCatalogoParroquias()) {
                    String clave = normalizarNombre(req.getNombre());
                    if (parroquiasExistentes.add(clave)) {
                        parroquiaService.save(req);
                        nuevasCreadas++;
                    }
                }

                if (nuevasCreadas > 0) {
                    System.out.println(">>> DataInitializer: " + nuevasCreadas + " parroquia(s) inicializada(s).");
                }
            } catch (Exception e) {
                System.out.println(">>> DataInitializer: Aviso durante inicialización de parroquias: " + e.getMessage());
            }
        };
    }

    private List<ParroquiaRequestDTO> construirCatalogoParroquias() {
        return List.of(
                // Cantón Piñas (parroquias urbanas y rurales)
                new ParroquiaRequestDTO("Piñas (Matriz)", "Piñas"),
                new ParroquiaRequestDTO("La Susaya", "Piñas"),
                new ParroquiaRequestDTO("Piñas Grande", "Piñas"),
                new ParroquiaRequestDTO("Capiro", "Piñas"),
                new ParroquiaRequestDTO("La Bocana", "Piñas"),
                new ParroquiaRequestDTO("Moromoro", "Piñas"),
                new ParroquiaRequestDTO("Piedras", "Piñas"),
                new ParroquiaRequestDTO("San Roque", "Piñas"),
                new ParroquiaRequestDTO("Saracay", "Piñas"),

                // Cantón Zaruma (colindante al noreste)
                new ParroquiaRequestDTO("Zaruma (Matriz)", "Zaruma"),
                new ParroquiaRequestDTO("Abañín", "Zaruma"),
                new ParroquiaRequestDTO("Arcapamba", "Zaruma"),
                new ParroquiaRequestDTO("Guanazán", "Zaruma"),
                new ParroquiaRequestDTO("Güizhagüiña", "Zaruma"),
                new ParroquiaRequestDTO("Huertas", "Zaruma"),
                new ParroquiaRequestDTO("Malvas", "Zaruma"),
                new ParroquiaRequestDTO("Muluncay", "Zaruma"),
                new ParroquiaRequestDTO("Salvias", "Zaruma"),
                new ParroquiaRequestDTO("Sinsao", "Zaruma"),

                // Cantón Portovelo (colindante al este)
                new ParroquiaRequestDTO("Portovelo (Matriz)", "Portovelo"),
                new ParroquiaRequestDTO("Curtincápac", "Portovelo"),
                new ParroquiaRequestDTO("Morales", "Portovelo"),
                new ParroquiaRequestDTO("Salatí", "Portovelo"),

                // Cantón Atahualpa (colindante al norte)
                new ParroquiaRequestDTO("Paccha (Matriz)", "Atahualpa"),
                new ParroquiaRequestDTO("Ayapamba", "Atahualpa"),
                new ParroquiaRequestDTO("Cordoncillo", "Atahualpa"),
                new ParroquiaRequestDTO("Milagro", "Atahualpa"),
                new ParroquiaRequestDTO("San José", "Atahualpa"),
                new ParroquiaRequestDTO("San Juan de Cerro Azul", "Atahualpa"),

                // Cantón Balsas (colindante al oeste)
                new ParroquiaRequestDTO("Balsas (Matriz)", "Balsas"),
                new ParroquiaRequestDTO("Bellamaría", "Balsas"),

                // Cantón Marcabelí (colindante al oeste)
                new ParroquiaRequestDTO("Marcabelí (Matriz)", "Marcabelí"),
                new ParroquiaRequestDTO("El Ingenio", "Marcabelí"),

                // Cantón Santa Rosa (colindante al noroeste)
                new ParroquiaRequestDTO("Santa Rosa (Matriz)", "Santa Rosa"),
                new ParroquiaRequestDTO("Nuevo Santa Rosa", "Santa Rosa"),
                new ParroquiaRequestDTO("Puerto Jelí", "Santa Rosa"),
                new ParroquiaRequestDTO("Bellamaría - Santa Rosa", "Santa Rosa"),
                new ParroquiaRequestDTO("Bellavista", "Santa Rosa"),
                new ParroquiaRequestDTO("Jambelí", "Santa Rosa"),
                new ParroquiaRequestDTO("La Avanzada", "Santa Rosa"),
                new ParroquiaRequestDTO("San Antonio", "Santa Rosa"),
                new ParroquiaRequestDTO("Torata", "Santa Rosa"),
                new ParroquiaRequestDTO("Victoria", "Santa Rosa"),

                // Cantón Arenillas (colindante al oeste)
                new ParroquiaRequestDTO("Arenillas (Matriz)", "Arenillas"),
                new ParroquiaRequestDTO("Carcabón", "Arenillas"),
                new ParroquiaRequestDTO("Chacras", "Arenillas"),
                new ParroquiaRequestDTO("Palmales", "Arenillas")
        );
    }

    private String normalizarNombre(String nombre) {
        return nombre.trim().toLowerCase(Locale.ROOT);
    }
}

