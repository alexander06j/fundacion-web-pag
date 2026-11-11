package com.fundacionunidosporpinias.fundacion_web.config;

import com.fundacionunidosporpinias.fundacion_web.model.Usuario;
import com.fundacionunidosporpinias.fundacion_web.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Autentica a los usuarios contra la tabla "usuario" de la base de datos.
 * Reemplaza al usuario único en memoria definido por SS_USER / SS_PASSWORD.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name())   // Genera ROLE_ADMIN / ROLE_OPERADOR
                .disabled(!usuario.isActivo())    // Los usuarios desactivados no pueden ingresar
                .build();
    }


}
