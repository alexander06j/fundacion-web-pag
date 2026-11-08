package com.fundacionunidosporpinias.fundacion_web.controller.web;


import com.fundacionunidosporpinias.fundacion_web.dto.usuario.UsuarioRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.usuario.UsuarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.model.Rol;
import com.fundacionunidosporpinias.fundacion_web.service.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/dashboard/usuarios")
public class UsuarioWebController {


    private static final int longitudMinimaPassword = 8;

    @Autowired
    private IUsuarioService usuarioService;

    //Lista de Usuarios
    @GetMapping
    public String listar(Model model){
        model.addAttribute("usuarios", usuarioService.findAll());
        return "private/usuarios/lista";
    }

    //Formulario para un nuevo usuario
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model){
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setRol(Rol.OPERADOR);

        model.addAttribute("usuario", dto);
        prepararFormulario(model, false, null);

        return "private/usuarios/formulario";
    }

    //Guardar usuario
    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("usuario")
            UsuarioRequestDTO usuarioDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        validarPassword(usuarioDTO, result, true);

        if(!result.hasFieldErrors("username")
                && usuarioService.existsByUsername(usuarioDTO.getUsername().trim())){
            result.rejectValue("username", "duplicado",
                    "Ya existe un usuario con ese nombre de usuario");
        }

        if(result.hasErrors()){
            prepararFormulario(model, false, null);
            return "private/usuarios/formulario";
        }

        usuarioService.save(usuarioDTO);
        redirectAttributes.addFlashAttribute("mensajeExito", "Usuario creado correctamente.");

        return "redirect:/dashboard/usuarios";
    }

    //Formulario editar Usuario
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable("id") Long id,
            Model model
    ){
        UsuarioResponseDTO response = usuarioService.findById(id);

        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setUsername(response.getUsername());
        dto.setNombreCompleto(response.getNombreCompleto());
        dto.setRol(response.getRol());

        model.addAttribute("usuario", dto);
        prepararFormulario(model, true, id);

        return "private/usuarios/formulario";
    }

    //Actualizar usuario
    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("usuario")
            UsuarioRequestDTO usuarioDTO,
            BindingResult result,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes
    ){
        UsuarioResponseDTO actual = usuarioService.findById(id);

        // El nombre de usuario no es editable
        usuarioDTO.setUsername(actual.getUsername());

        validarPassword(usuarioDTO, result, false);

        // Evita que el administrador conectado se quite su propio rol
        boolean esElMismo = actual.getUsername().equals(principal.getName());
        if(esElMismo && usuarioDTO.getRol() != Rol.ADMIN){
            result.rejectValue("rol", "autodegradacion",
                    "No puede quitarse a sí mismo el rol de Administrador");
        }

        if(result.hasErrors()){
            prepararFormulario(model, true, id);
            return "private/usuarios/formulario";
        }

        usuarioService.update(id, usuarioDTO);
        redirectAttributes.addFlashAttribute("mensajeExito", "Usuario actualizado correctamente.");

        return "redirect:/dashboard/usuarios";
    }


    //Activar / Desactivar usuario (no se eliminan usuarios para conservar la trazabilidad)
    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable("id") Long id,
            Principal principal,
            RedirectAttributes redirectAttributes
    ){
        UsuarioResponseDTO actual = usuarioService.findById(id);

        if(actual.getUsername().equals(principal.getName())){
            redirectAttributes.addFlashAttribute("errorMensaje",
                    "No puede desactivar su propia cuenta.");
            return "redirect:/dashboard/usuarios";
        }

        UsuarioResponseDTO actualizado = usuarioService.cambiarEstado(id);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Usuario " + (actualizado.isActivo() ? "activado" : "desactivado") + " correctamente.");

        return "redirect:/dashboard/usuarios";
    }

    private void prepararFormulario(Model model, boolean modoEdicion, Long id){
        model.addAttribute("titulo", modoEdicion ? "Editar usuario" : "Registrar usuario");
        model.addAttribute("modoEdicion", modoEdicion);
        model.addAttribute("usuarioId", id);
        model.addAttribute("roles", Rol.values());
    }

    private void validarPassword(UsuarioRequestDTO dto, BindingResult result, boolean obligatoria){
        String password = dto.getPassword();
        boolean vacia = password == null || password.isBlank();

        if(vacia){
            if(obligatoria){
                result.rejectValue("password", "obligatoria", "La contraseña es obligatoria");
            }
            return;
        }

        if(password.length() < longitudMinimaPassword){
            result.rejectValue("password", "longitud",
                    "La contraseña debe tener al menos " + longitudMinimaPassword + " caracteres");
        }

        if(!password.equals(dto.getConfirmarPassword())){
            result.rejectValue("confirmarPassword", "noCoincide", "Las contraseñas no coinciden");
        }
    }





}
