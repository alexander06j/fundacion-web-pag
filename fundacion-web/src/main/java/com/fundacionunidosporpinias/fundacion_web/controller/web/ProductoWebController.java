package com.fundacionunidosporpinias.fundacion_web.controller.web;


import com.fundacionunidosporpinias.fundacion_web.dto.producto.ProductoRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.producto.ProductoResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.service.IProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/productos")
public class ProductoWebController {

    @Autowired
    private IProductoService productoService;

    //Lista de Productos
    @GetMapping
    public String listar(Model model){

        model.addAttribute(
                "productos",
                productoService.findAll()
        );
        return "private/productos/lista";
    }

    //Formulario para un nuevo producto
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model){

        model.addAttribute(
                "producto",
                new ProductoRequestDTO()
        );

        model.addAttribute("titulo", "Registrar producto");
        model.addAttribute("modoEdicion",false);

        return "private/productos/formulario";
    }

    //Guardar producto
    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("producto")
            ProductoRequestDTO productoDTO,
            BindingResult result,
            Model model
    ){
        if(result.hasErrors()){
            model.addAttribute(
                    "titulo",
                    "Registrar producto"
            );

            model.addAttribute(
                    "modoEdicion",
                    false
            );

            return "private/productos/formulario";

        }

        productoService.save(productoDTO);

        return "redirect:/dashboard/productos";
    }

    //Formulario editar Producto
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable("id") Long id,
            Model model
    ){

        ProductoResponseDTO response = productoService.findById(id);

        if(response == null){
            return "redirect:/dashboard/productos";
        }

        ProductoRequestDTO dto = new ProductoRequestDTO();

        dto.setNombre(response.getNombre());
        dto.setStock(response.getStock());
        dto.setDescripcion(response.getDescripcion());
        dto.setCategoria(response.getCategoria());

        model.addAttribute("producto", dto);
        model.addAttribute("auditoria", response);
        model.addAttribute("productoId", id);
        model.addAttribute("titulo", "Editar producto");
        model.addAttribute("modoEdicion", true);

        return "private/productos/formulario";
    }

    //Actualizar producto
    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("producto")
            ProductoRequestDTO productoDTO,
            BindingResult result,
            Model model
    ){
        if(result.hasErrors()){
            model.addAttribute(
                    "productoId",
                    id
            );
            model.addAttribute("auditoria", productoService.findById(id));

            model.addAttribute(
                    "titulo",
                    "Editar producto"
            );

            model.addAttribute(
                    "modoEdicion",
                    true
            );

            return "private/productos/formulario";

        }

        productoService.update(id, productoDTO);

        return "redirect:/dashboard/productos";

    }

    //Eliminar producto
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        try {
            productoService.delete(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMensaje", "No se puede eliminar el producto porque está vinculado a entregas.");
        }

        return "redirect:/dashboard/productos";
    }

}
