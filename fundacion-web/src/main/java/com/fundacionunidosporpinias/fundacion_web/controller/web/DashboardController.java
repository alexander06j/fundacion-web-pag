package com.fundacionunidosporpinias.fundacion_web.controller.web;

import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.producto.ProductoResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.service.IBeneficiarioService;
import com.fundacionunidosporpinias.fundacion_web.service.IEntregaService;
import com.fundacionunidosporpinias.fundacion_web.service.IParroquiaService;
import com.fundacionunidosporpinias.fundacion_web.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("dashboard")
public class DashboardController {

    @Autowired
    private IParroquiaService parroquiaService;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IBeneficiarioService beneficiarioService;

    @Autowired
    private IEntregaService entregaService;

    @GetMapping
    public String dashboard(Model model) {

        List<ParroquiaResponseDTO> parroquias = parroquiaService.findAll();
        List<BeneficiarioResponseDTO> beneficiarios = beneficiarioService.findAll();
        List<ProductoResponseDTO> productos = productoService.findAll();
        List<EntregaResumenDTO> entregas = entregaService.findAll();

        // 1. KPIs
        long totalParroquias = parroquias.size();
        long totalBeneficiarios = beneficiarios.size();

        long productosBajoStock = productos.stream()
                .filter(p -> p.getStock() != null && p.getStock() < 10)
                .count();

        LocalDate haceUnMes = LocalDate.now().minusDays(30);
        long entregasUltimoMes = entregas.stream()
                .filter(e -> e.getFechaEntrega() != null && !e.getFechaEntrega().isBefore(haceUnMes))
                .count();

        model.addAttribute("totalParroquias", totalParroquias);
        model.addAttribute("totalBeneficiarios", totalBeneficiarios);
        model.addAttribute("productosBajoStock", productosBajoStock);
        model.addAttribute("entregasUltimoMes", entregasUltimoMes > 0 ? entregasUltimoMes : entregas.size());

        // 2. Gráfica 1: Historial de entregas mes a mes
        Map<String, Long> entregasPorMesMap = new LinkedHashMap<>();
        LocalDate ahora = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate mes = ahora.minusMonths(i);
            String nombreMes = mes.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
            entregasPorMesMap.put(nombreMes, 0L);
        }

        for (EntregaResumenDTO entrega : entregas) {
            if (entrega.getFechaEntrega() != null) {
                String mes = entrega.getFechaEntrega().getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
                if (entregasPorMesMap.containsKey(mes)) {
                    entregasPorMesMap.put(mes, entregasPorMesMap.get(mes) + 1);
                }
            }
        }

        model.addAttribute("mesesLabels", new ArrayList<>(entregasPorMesMap.keySet()));
        model.addAttribute("mesesData", new ArrayList<>(entregasPorMesMap.values()));

        // 3. Gráfica 2: Distribución de beneficiarios por Parroquia
        Map<String, Long> beneficiariosPorParroquia = beneficiarios.stream()
                .collect(Collectors.groupingBy(
                        b -> (b.getParroquiaNombre() != null ? b.getParroquiaNombre() : "Sin parroquia"),
                        Collectors.counting()
                ));

        // Si no hay beneficiarios registrados aún, populate con nombres de parroquias con 0
        if (beneficiariosPorParroquia.isEmpty() && !parroquias.isEmpty()) {
            for (ParroquiaResponseDTO p : parroquias) {
                beneficiariosPorParroquia.put(p.getNombre(), 0L);
            }
        }

        model.addAttribute("parroquiasLabels", new ArrayList<>(beneficiariosPorParroquia.keySet()));
        model.addAttribute("parroquiasData", new ArrayList<>(beneficiariosPorParroquia.values()));

        // 4. Últimas 5 entregas registradas en tiempo real
        List<EntregaResumenDTO> ultimasEntregas = entregas.stream()
                .sorted((a, b) -> {
                    if (a.getFechaEntrega() == null && b.getFechaEntrega() == null) return 0;
                    if (a.getFechaEntrega() == null) return 1;
                    if (b.getFechaEntrega() == null) return -1;
                    return b.getFechaEntrega().compareTo(a.getFechaEntrega());
                })
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("ultimasEntregas", ultimasEntregas);

        return "private/dashboard";
    }


}
