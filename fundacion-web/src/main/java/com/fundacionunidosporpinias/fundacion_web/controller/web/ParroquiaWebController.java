package com.fundacionunidosporpinias.fundacion_web.controller.web;

import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.service.IParroquiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/parroquias")
public class ParroquiaWebController {

    @Autowired
    private IParroquiaService parroquiaService;

    //Lista de Parroquias
    @GetMapping
    public String listar(Model model){

        model.addAttribute(
                "parroquias",
                parroquiaService.findAll()
        );
        return "private/parroquias/lista";
    }

    //Formulario para una nueva parroquia
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model){

        model.addAttribute(
                "parroquia",
                new ParroquiaRequestDTO()
        );

        model.addAttribute("titulo", "Registrar parroquia");
        model.addAttribute("modoEdicion",false);

        return "private/parroquias/formulario";
    }

    //Guardar parroquia
    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("parroquia")
            ParroquiaRequestDTO parroquiaDTO,
            BindingResult result,
            Model model
    ){
        if(result.hasErrors()){
            model.addAttribute(
                    "titulo",
                    "Registrar parroquia"
            );

            model.addAttribute(
                    "modoEdicion",
                    false
            );

            return "private/parroquias/formulario";

        }

        parroquiaService.save(parroquiaDTO);

        return "redirect:/dashboard/parroquias";
    }

    //Formulario editar Parroquia
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable("id") Long id,
            Model model
    ){

        ParroquiaResponseDTO response = parroquiaService.findById(id);

        if(response == null){
            return "redirect:/dashboard/parroquias";
        }

        ParroquiaRequestDTO dto = new ParroquiaRequestDTO();

        dto.setNombre(response.getNombre());
        dto.setCanton(response.getCanton());

        model.addAttribute("parroquia", dto);
        model.addAttribute("auditoria", response);
        model.addAttribute("parroquiaId", id);
        model.addAttribute("titulo", "Editar parroquia");
        model.addAttribute("modoEdicion", true);

        return "private/parroquias/formulario";
    }

    //Actualizar parroquia
    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("parroquia")
            ParroquiaRequestDTO parroquiaDTO,
            BindingResult result,
            Model model
    ){
        if(result.hasErrors()){
            model.addAttribute(
                    "parroquiaId",
                    id
            );
            model.addAttribute("auditoria", parroquiaService.findById(id));

            model.addAttribute(
                    "titulo",
                    "Editar parroquia"
            );

            model.addAttribute(
                    "modoEdicion",
                    true
            );

            return "private/parroquias/formulario";

        }

        parroquiaService.update(id, parroquiaDTO);

        return "redirect:/dashboard/parroquias";

    }

    //Eliminar parroquia
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        try {
            parroquiaService.delete(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMensaje", "No se puede eliminar la parroquia porque tiene beneficiarios asociados.");
        }

        return "redirect:/dashboard/parroquias";
    }


}
