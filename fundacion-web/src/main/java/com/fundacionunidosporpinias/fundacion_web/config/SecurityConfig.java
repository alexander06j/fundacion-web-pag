package com.fundacionunidosporpinias.fundacion_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth

                        // ===== Zona pública =====
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**",
                                "/favicon.ico", "/error", "/acceso-denegado").permitAll()

                        // ===== Gestión de usuarios: solo ADMIN =====
                        .requestMatchers("/dashboard/usuarios/**").hasRole("ADMIN")

                        // ===== Eliminaciones: solo ADMIN =====
                        .requestMatchers(HttpMethod.POST,
                                "/dashboard/parroquias/*/eliminar",
                                "/dashboard/productos/*/eliminar",
                                "/dashboard/beneficiarios/*/eliminar",
                                "/dashboard/entregas/*/eliminar").hasRole("ADMIN")

                        // ===== Parroquias (configuración territorial): crear/editar solo ADMIN =====
                        .requestMatchers("/dashboard/parroquias/nuevo",
                                "/dashboard/parroquias/*/editar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/dashboard/parroquias",
                                "/dashboard/parroquias/*").hasRole("ADMIN")

                        // ===== Creación y edición (Productos, Beneficiarios, Entregas): ADMIN y OPERADOR (LECTOR denegado) =====
                        .requestMatchers(
                                "/dashboard/productos/nuevo",
                                "/dashboard/productos/*/editar",
                                "/dashboard/beneficiarios/nuevo",
                                "/dashboard/beneficiarios/*/editar",
                                "/dashboard/entregas/nuevo").hasAnyRole("ADMIN", "OPERADOR")

                        .requestMatchers(HttpMethod.POST,
                                "/dashboard/productos",
                                "/dashboard/productos/*",
                                "/dashboard/beneficiarios",
                                "/dashboard/beneficiarios/*",
                                "/dashboard/entregas").hasAnyRole("ADMIN", "OPERADOR")

                        // ===== API REST =====
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/parroquias").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/parroquias/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/**").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "OPERADOR", "LECTOR")

                        // ===== Resto del panel (Lectura: Dashboard, listados, detalle): ADMIN, OPERADOR y LECTOR =====
                        .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "OPERADOR", "LECTOR")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                // Autenticación básica para clientes de la API (Postman, etc.)
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/acceso-denegado")
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                );

        return http.build();
    }



}
