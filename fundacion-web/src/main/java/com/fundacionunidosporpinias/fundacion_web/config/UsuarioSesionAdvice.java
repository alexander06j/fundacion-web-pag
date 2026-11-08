package com.fundacionunidosporpinias.fundacion_web.config;

import com.fundacionunidosporpinias.fundacion_web.model.Usuario;
import com.fundacionunidosporpinias.fundacion_web.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Agrega a todas las vistas web los datos del usuario conectado:
 * esAdmin, puedeEditar, esLector, usuarioUsername, usuarioNombre y usuarioRol.
 * Permite mostrar u ocultar botones en Thymeleaf con th:if="${esAdmin}" o th:if="${puedeEditar}".
 */
@ControllerAdvice(basePackages = "com.fundacionunidosporpinias.fundacion_web.controller.web")
public class UsuarioSesionAdvice {


    private final UsuarioRepository usuarioRepository;

    public UsuarioSesionAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute
    public void agregarDatosSesion(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean autenticado = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        if (!autenticado) {
            model.addAttribute("esAdmin", false);
            model.addAttribute("puedeEditar", false);
            model.addAttribute("esLector", true);
            return;
        }

        String username = auth.getName();

        boolean esAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        boolean esOperador = auth.getAuthorities()
                .stream()
                .anyMatch(a -> "ROLE_OPERADOR".equals(a.getAuthority()));

        boolean puedeEditar = esAdmin || esOperador;

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        String nombre = usuario != null ? usuario.getNombreCompleto() : username;
        String rolDescripcion = usuario != null && usuario.getRol() != null
                ? usuario.getRol().getDescripcion()
                : (esAdmin ? "Administrador" : (esOperador ? "Operador" : "Lector"));

        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("puedeEditar", puedeEditar);
        model.addAttribute("esLector", !puedeEditar);
        model.addAttribute("usuarioUsername", username);
        model.addAttribute("usuarioNombre", nombre);
        model.addAttribute("usuarioRol", rolDescripcion);
    }


}
