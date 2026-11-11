package com.fundacionunidosporpinias.fundacion_web.config;

import com.fundacionunidosporpinias.fundacion_web.model.Rol;
import com.fundacionunidosporpinias.fundacion_web.model.Usuario;
import com.fundacionunidosporpinias.fundacion_web.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UsuarioInitializer {

    @Bean
    @Order(5)
    public CommandLineRunner crearUsuarioInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${spring.security.user.name}") String adminUsername,
            @Value("${spring.security.user.password}") String adminPassword
    ) {
        return args -> {
            //Crear Administrador si no existe
            if (usuarioRepository.findByUsername(adminUsername).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setNombreCompleto("Administrador General");
                admin.setRol(Rol.ADMIN);
                admin.setActivo(true);
                usuarioRepository.save(admin);
                System.out.println(">>> UsuarioInitializer: Administrador creado -> " + adminUsername);
            }
        };
    }
}
