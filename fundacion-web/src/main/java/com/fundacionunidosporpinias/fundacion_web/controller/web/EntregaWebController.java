package com.fundacionunidosporpinias.fundacion_web.controller.web;

import com.fundacionunidosporpinias.fundacion_web.dto.entrega.DetalleEntregaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.service.IBeneficiarioService;
import com.fundacionunidosporpinias.fundacion_web.service.IEntregaService;
import com.fundacionunidosporpinias.fundacion_web.service.IProductoService;
import com.fundacionunidosporpinias.fundacion_web.service.IReporteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/dashboard/entregas")
public class EntregaWebController {

    private static final MediaType MEDIA_XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    @Autowired
    private IEntregaService entregaService;

    @Autowired
    private IBeneficiarioService beneficiarioService;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IReporteService reporteService;

    // Lista / Historial de Entregas con búsqueda, rango de fechas y paginación en
    // servidor
    @GetMapping

    public String listar(
            @RequestParam(name = "q", defaultValue = "") String q,
            @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        int tamano = (size < 1 || size > 100) ? 10 : size;
        int paginaIdx = Math.max(page, 0);
        String filtro = q == null ? "" : q.trim();

        Sort orden = Sort.by(Sort.Direction.DESC, "fechaEntrega")
                .and(Sort.by(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(paginaIdx, tamano, orden);
        Page<EntregaResumenDTO> pagina = entregaService.buscarPaginado(filtro, desde, hasta, pageable);

        if (paginaIdx > 0 && pagina.getTotalPages() > 0 && paginaIdx >= pagina.getTotalPages()) {
            paginaIdx = pagina.getTotalPages() - 1;
            pagina = entregaService.buscarPaginado(filtro, desde, hasta, PageRequest.of(paginaIdx, tamano, orden));
        }

        model.addAttribute("pagina", pagina);
        model.addAttribute("entregas", pagina.getContent());
        model.addAttribute("q", filtro);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("size", tamano);
        model.addAttribute("numerosPagina",
                BeneficiarioWebController.calcularVentanaPaginas(pagina.getNumber(), pagina.getTotalPages()));

        return "private/entregas/lista";
    }

    // Exportación a Excel (.xlsx) respetando la búsqueda y el rango de fechas
    // activos
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(name = "q", defaultValue = "") String q,
            @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        byte[] archivo = reporteService.exportarEntregasExcel(q, desde, hasta);
        String nombreArchivo = "entregas_" + LocalDate.now() + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MEDIA_XLSX);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(nombreArchivo, StandardCharsets.UTF_8)
                        .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(archivo);
    }

    // Detalle de una entrega
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable("id") Long id, Model model) {
        EntregaResponseDTO entrega = entregaService.findById(id);
        if (entrega == null) {
            return "redirect:/dashboard/entregas";
        }
        model.addAttribute("entrega", entrega);
        return "private/entregas/detalle";
    }

    // Descarga del Acta de Entrega en PDF lista para firma física
    @GetMapping("/{id}/acta-pdf")
    public ResponseEntity<byte[]> descargarActaPdf(@PathVariable("id") Long id) {
        byte[] pdf = reporteService.generarActaEntregaPdf(id);
        String nombreArchivo = String.format("acta_entrega_%06d.pdf", id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(nombreArchivo, StandardCharsets.UTF_8)
                        .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    // Formulario para registrar nueva entrega
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        EntregaRequestDTO entregaDTO = new EntregaRequestDTO();
        List<DetalleEntregaRequestDTO> detalles = new ArrayList<>();
        detalles.add(new DetalleEntregaRequestDTO());
        entregaDTO.setDetalles(detalles);

        model.addAttribute("entrega", entregaDTO);
        model.addAttribute("beneficiarios", beneficiarioService.findAll());
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("titulo", "Registrar Entrega");

        return "private/entregas/formulario";
    }

    // Guardar entrega
    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("entrega") EntregaRequestDTO entregaDTO,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Registrar Entrega");
            model.addAttribute("beneficiarios", beneficiarioService.findAll());
            model.addAttribute("productos", productoService.findAll());
            return "private/entregas/formulario";
        }

        try {
            entregaService.registrarEntrega(entregaDTO);
        } catch (Exception e) {
            model.addAttribute("titulo", "Registrar Entrega");
            model.addAttribute("beneficiarios", beneficiarioService.findAll());
            model.addAttribute("productos", productoService.findAll());
            model.addAttribute("errorMensaje", e.getMessage());
            return "private/entregas/formulario";
        }

        return "redirect:/dashboard/entregas";
    }

    // Eliminar entrega
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable("id") Long id) {
        entregaService.delete(id);
        return "redirect:/dashboard/entregas";
    }

}
