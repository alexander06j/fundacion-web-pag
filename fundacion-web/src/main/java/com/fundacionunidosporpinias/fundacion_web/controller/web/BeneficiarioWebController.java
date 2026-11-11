package com.fundacionunidosporpinias.fundacion_web.controller.web;

import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.service.IBeneficiarioService;
import com.fundacionunidosporpinias.fundacion_web.service.IParroquiaService;
import com.fundacionunidosporpinias.fundacion_web.service.IReporteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/dashboard/beneficiarios")
public class BeneficiarioWebController {

    private static final MediaType MEDIA_XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    @Autowired
    private IBeneficiarioService beneficiarioService;

    @Autowired
    private IParroquiaService parroquiaService;

    @Autowired
    private IReporteService reporteService;

    // Lista de Beneficiarios con búsqueda y paginación en servidor
    @GetMapping
    public String listar(
            @RequestParam(name = "q", defaultValue = "") String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        int tamano = (size < 1 || size > 100) ? 10 : size;
        int paginaIdx = Math.max(page, 0);
        String filtro = q == null ? "" : q.trim();

        Sort orden = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(paginaIdx, tamano, orden);
        Page<BeneficiarioResponseDTO> pagina = beneficiarioService.buscarPaginado(filtro, pageable);

        // Si se eliminó el último registro de una página alta, retrocede a la última
        // disponible
        if (paginaIdx > 0 && pagina.getTotalPages() > 0 && paginaIdx >= pagina.getTotalPages()) {
            paginaIdx = pagina.getTotalPages() - 1;
            pagina = beneficiarioService.buscarPaginado(filtro, PageRequest.of(paginaIdx, tamano, orden));
        }

        model.addAttribute("pagina", pagina);
        model.addAttribute("beneficiarios", pagina.getContent());
        model.addAttribute("q", filtro);
        model.addAttribute("size", tamano);
        model.addAttribute("numerosPagina", calcularVentanaPaginas(pagina.getNumber(), pagina.getTotalPages()));

        return "private/beneficiarios/lista";
    }


    // Exportación a Excel (.xlsx) respetando el filtro de búsqueda activo
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(name = "q", defaultValue = "") String q
    ) {
        byte[] archivo = reporteService.exportarBeneficiariosExcel(q);
        String nombreArchivo = "beneficiarios_" + LocalDate.now() + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MEDIA_XLSX);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(nombreArchivo, StandardCharsets.UTF_8)
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(archivo);
    }

    // Formulario para un nuevo beneficiario
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("beneficiario", new BeneficiarioRequestDTO());
        model.addAttribute("parroquias", parroquiaService.findAll());
        model.addAttribute("titulo", "Registrar beneficiario");
        model.addAttribute("modoEdicion", false);

        return "private/beneficiarios/formulario";
    }

    // Guardar beneficiario
    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("beneficiario") BeneficiarioRequestDTO beneficiarioDTO,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Registrar beneficiario");
            model.addAttribute("modoEdicion", false);
            model.addAttribute("parroquias", parroquiaService.findAll());
            return "private/beneficiarios/formulario";
        }

        beneficiarioService.save(beneficiarioDTO);
        return "redirect:/dashboard/beneficiarios";
    }

    // Formulario editar Beneficiario
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable("id") Long id,
            Model model
    ) {
        BeneficiarioResponseDTO response = beneficiarioService.findById(id);
        if (response == null) {
            return "redirect:/dashboard/beneficiarios";
        }

        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO();
        dto.setCedula(response.getCedula());
        dto.setNombre(response.getNombre());
        dto.setApellido(response.getApellido());
        dto.setTelefono(response.getTelefono());
        dto.setDireccion(response.getDireccion());
        dto.setParroquiaId(response.getParroquiaId());

        model.addAttribute("beneficiario", dto);
        model.addAttribute("auditoria", response);
        model.addAttribute("beneficiarioId", id);
        model.addAttribute("parroquias", parroquiaService.findAll());
        model.addAttribute("titulo", "Editar beneficiario");
        model.addAttribute("modoEdicion", true);

        return "private/beneficiarios/formulario";
    }

    // Actualizar beneficiario
    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("beneficiario") BeneficiarioRequestDTO beneficiarioDTO,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("beneficiarioId", id);
            model.addAttribute("auditoria", beneficiarioService.findById(id));
            model.addAttribute("titulo", "Editar beneficiario");
            model.addAttribute("modoEdicion", true);
            model.addAttribute("parroquias", parroquiaService.findAll());
            return "private/beneficiarios/formulario";
        }

        beneficiarioService.update(id, beneficiarioDTO);
        return "redirect:/dashboard/beneficiarios";
    }

    // Eliminar beneficiario
    @PostMapping("/{id}/eliminar")

    public String eliminar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            beneficiarioService.delete(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMensaje",
                    "No se puede eliminar el beneficiario porque tiene entregas registradas.");
        }

        return "redirect:/dashboard/beneficiarios";
    }

    static List<Integer> calcularVentanaPaginas(int paginaActual, int totalPaginas) {
        if (totalPaginas <= 0) {
            return List.of();
        }
        int ventana = 5;
        int inicio = Math.max(0, Math.min(paginaActual - 2, totalPaginas - ventana));
        int fin = Math.min(totalPaginas - 1, inicio + ventana - 1);
        return IntStream.rangeClosed(inicio, fin).boxed().toList();
    }



}
