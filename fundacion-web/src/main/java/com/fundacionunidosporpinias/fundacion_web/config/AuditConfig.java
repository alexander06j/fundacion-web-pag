package com.fundacionunidosporpinias.fundacion_web.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


/**
 * Activa la auditoría de Spring Data JPA (@CreatedBy, @CreatedDate,
 * @LastModifiedBy, @LastModifiedDate).
 *
 * El "auditor" es el username del usuario autenticado. Las operaciones
 * ejecutadas sin sesión (por ejemplo, el sembrado inicial de datos al
 * arrancar la aplicación) se registran como "sistema".
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditConfig {

    public static final String auditorSistema = "sistema";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            boolean autenticado = auth != null
                    && auth.isAuthenticated()
                    && !(auth instanceof AnonymousAuthenticationToken);

            return Optional.of(autenticado ? auth.getName() : auditorSistema);
        };
    }


}
