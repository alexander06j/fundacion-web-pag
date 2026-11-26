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

    private static final Set<String> CANTONES_EXCLUIDOS_AUTO = Set.of(
            "atahualpa",
            "balsas",
            "marcabelí",
            "marcabeli",
            "santa rosa",
            "arenillas"
    );

    @Bean
    @Order(10)
    public CommandLineRunner initData(IParroquiaService parroquiaService) {
        return args -> {
            try {
                // 1. Limpiar parroquias sembradas automáticamente de cantones descartados y recolectar las activas
                Set<String> parroquiasExistentes = new HashSet<>();
                for (ParroquiaResponseDTO existente : parroquiaService.findAll()) {
                    String cantonNorm = existente.getCanton() != null ? normalizarNombre(existente.getCanton()) : "";
                    boolean creadaPorSistema = existente.getCreadoPor() == null
                            || "sistema".equalsIgnoreCase(existente.getCreadoPor().trim());

                    if (CANTONES_EXCLUIDOS_AUTO.contains(cantonNorm) && creadaPorSistema && existente.getId() != null) {
                        try {
                            parroquiaService.delete(existente.getId());
                            continue;
                        } catch (Exception ignored) {
                            // Si tiene beneficiarios asociados, se conserva por integridad referencial
                        }
                    }

                    if (existente.getNombre() != null) {
                        parroquiasExistentes.add(normalizarNombre(existente.getNombre()));
                    }
                }

                // 2. Cargar catálogo territorial de Piñas, Zaruma y Portovelo sin duplicar existentes
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
                new ParroquiaRequestDTO("Salatí", "Portovelo")
        );
    }

    private String normalizarNombre(String nombre) {
        return nombre.trim().toLowerCase(Locale.ROOT);
    }
}

