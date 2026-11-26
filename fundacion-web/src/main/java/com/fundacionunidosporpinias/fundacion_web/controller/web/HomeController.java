package com.fundacionunidosporpinias.fundacion_web.controller.web;

import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.producto.ProductoResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.model.OrigenProducto;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private static final DateTimeFormatter FECHA_PUBLICA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired(required = false)
    private IParroquiaService parroquiaService;

    @Autowired(required = false)
    private IBeneficiarioService beneficiarioService;

    @Autowired(required = false)
    private IProductoService productoService;

    @Autowired(required = false)
    private IEntregaService entregaService;

    @GetMapping("/")
    public String home(Model model) {
        List<ParroquiaResponseDTO> parroquias = parroquiaService != null
                ? parroquiaService.findAll()
                : Collections.emptyList();
        List<BeneficiarioResponseDTO> beneficiarios = beneficiarioService != null
                ? beneficiarioService.findAll()
                : Collections.emptyList();
        List<ProductoResponseDTO> productos = productoService != null
                ? productoService.findAll()
                : Collections.emptyList();
        List<EntregaResumenDTO> entregas = entregaService != null
                ? entregaService.findAll()
                : Collections.emptyList();

        // 1. Conteo de beneficiarios y entregas por parroquia
        Map<String, Long> beneficiariosPorParroquia = beneficiarios.stream()
                .filter(b -> b.getParroquiaNombre() != null && !b.getParroquiaNombre().isBlank())
                .collect(Collectors.groupingBy(
                        b -> b.getParroquiaNombre().trim(),
                        Collectors.counting()
                ));

        Map<String, Long> entregasPorParroquia = entregas.stream()
                .filter(e -> e.getParroquiaNombre() != null && !e.getParroquiaNombre().isBlank())
                .collect(Collectors.groupingBy(
                        e -> e.getParroquiaNombre().trim(),
                        Collectors.counting()
                ));

        // 2. Agrupación territorial por Cantón (priorizando Piñas al inicio)
        Map<String, List<ParroquiaPublicaItem>> parroquiasPorCanton = new LinkedHashMap<>();
        List<ParroquiaPublicaItem> catalogoParroquias = new ArrayList<>();

        List<ParroquiaResponseDTO> parroquiasOrdenadas = new ArrayList<>(parroquias);
        parroquiasOrdenadas.sort((a, b) -> {
            String cantonA = a.getCanton() != null ? a.getCanton().trim() : "Piñas";
            String cantonB = b.getCanton() != null ? b.getCanton().trim() : "Piñas";
            boolean aEsPinias = cantonA.equalsIgnoreCase("Piñas");
            boolean bEsPinias = cantonB.equalsIgnoreCase("Piñas");
            if (aEsPinias != bEsPinias) {
                return aEsPinias ? -1 : 1;
            }
            int cmpCanton = cantonA.compareToIgnoreCase(cantonB);
            if (cmpCanton != 0) {
                return cmpCanton;
            }
            String nomA = a.getNombre() != null ? a.getNombre() : "";
            String nomB = b.getNombre() != null ? b.getNombre() : "";
            return nomA.compareToIgnoreCase(nomB);
        });

        for (ParroquiaResponseDTO p : parroquiasOrdenadas) {
            String nombreParroquia = p.getNombre() != null ? p.getNombre().trim() : "Sin nombre";
            String canton = (p.getCanton() != null && !p.getCanton().isBlank()) ? p.getCanton().trim() : "Piñas";
            long familias = beneficiariosPorParroquia.getOrDefault(nombreParroquia, 0L);
            long despachos = entregasPorParroquia.getOrDefault(nombreParroquia, 0L);
            boolean esMatriz = nombreParroquia.toLowerCase(Locale.ROOT).contains("matriz");

            ParroquiaPublicaItem item = new ParroquiaPublicaItem(
                    nombreParroquia,
                    canton,
                    familias,
                    despachos,
                    esMatriz
            );
            catalogoParroquias.add(item);
            parroquiasPorCanton.computeIfAbsent(canton, k -> new ArrayList<>()).add(item);
        }

        // 3. Indicadores globales (KPIs de impacto público)
        long totalParroquias = catalogoParroquias.size();
        long totalCantones = parroquiasPorCanton.size();
        long totalBeneficiarios = beneficiarios.size();
        long totalEntregas = entregas.size();

        LocalDate haceTreintaDias = LocalDate.now().minusDays(30);
        long entregasUltimoMes = entregas.stream()
                .filter(e -> e.getFechaEntrega() != null && !e.getFechaEntrega().isBefore(haceTreintaDias))
                .count();

        long totalStockDisponible = productos.stream()
                .filter(p -> p.getStock() != null && p.getStock() > 0)
                .mapToLong(ProductoResponseDTO::getStock)
                .sum();

        long productosDonados = productos.stream()
                .filter(p -> p.getOrigen() == OrigenProducto.DONADO)
                .count();

        long productosComprados = productos.stream()
                .filter(p -> p.getOrigen() == OrigenProducto.COMPRADO)
                .count();

        // 4. Resumen por categorías de insumos para transparencia
        Map<String, Long> stockPorCategoriaMap = productos.stream()
                .filter(p -> p.getCategoria() != null && !p.getCategoria().isBlank())
                .collect(Collectors.groupingBy(
                        p -> p.getCategoria().trim(),
                        LinkedHashMap::new,
                        Collectors.summingLong(p -> p.getStock() != null ? Math.max(p.getStock(), 0) : 0L)
                ));

        List<CategoriaPublicaItem> categoriasResumen = stockPorCategoriaMap.entrySet().stream()
                .map(entry -> new CategoriaPublicaItem(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(CategoriaPublicaItem::getUnidadesDisponibles).reversed())
                .limit(6)
                .collect(Collectors.toList());

        // 5. Bitácora pública de entregas recientes (anonimizadas para proteger datos personales)
        List<EntregaPublicaItem> entregasRecientesPublicas = entregas.stream()
                .sorted((a, b) -> {
                    if (a.getFechaEntrega() == null && b.getFechaEntrega() == null) {
                        return Long.compare(b.getId() != null ? b.getId() : 0L, a.getId() != null ? a.getId() : 0L);
                    }
                    if (a.getFechaEntrega() == null) return 1;
                    if (b.getFechaEntrega() == null) return -1;
                    int cmp = b.getFechaEntrega().compareTo(a.getFechaEntrega());
                    if (cmp != 0) return cmp;
                    return Long.compare(b.getId() != null ? b.getId() : 0L, a.getId() != null ? a.getId() : 0L);
                })
                .limit(6)
                .map(e -> new EntregaPublicaItem(
                        String.format("ACTA-%05d", e.getId() != null ? e.getId() : 0L),
                        e.getFechaEntrega() != null ? e.getFechaEntrega().format(FECHA_PUBLICA_FMT) : "Reciente",
                        anonimizarNombreBeneficiario(e.getBeneficiarioNombre()),
                        e.getParroquiaNombre() != null ? e.getParroquiaNombre() : "Piñas (Matriz)"
                ))
                .collect(Collectors.toList());

        model.addAttribute("totalParroquias", totalParroquias);
        model.addAttribute("totalCantones", totalCantones > 0 ? totalCantones : 1L);
        model.addAttribute("totalBeneficiarios", totalBeneficiarios);
        model.addAttribute("totalEntregas", totalEntregas);
        model.addAttribute("entregasUltimoMes", entregasUltimoMes);
        model.addAttribute("totalStockDisponible", totalStockDisponible);
        model.addAttribute("productosDonados", productosDonados);
        model.addAttribute("productosComprados", productosComprados);
        model.addAttribute("catalogoParroquias", catalogoParroquias);
        model.addAttribute("parroquiasPorCanton", parroquiasPorCanton);
        model.addAttribute("cantonesDisponibles", new ArrayList<>(parroquiasPorCanton.keySet()));
        model.addAttribute("categoriasResumen", categoriasResumen);
        model.addAttribute("entregasRecientesPublicas", entregasRecientesPublicas);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Sin método HTTP fijo: Spring Security reenvía aquí tanto GET como POST denegados
    @RequestMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }

    /**
     * Convierte un nombre completo como "María Elena Romero" en iniciales "Familia M. R."
     * para garantizar transparencia pública sin exponer datos personales sensibles.
     */
    private String anonimizarNombreBeneficiario(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            return "Hogar Beneficiario";
        }
        String[] partes = nombreCompleto.trim().split("\\s+");
        StringBuilder iniciales = new StringBuilder("Hogar ");
        int tomadas = 0;
        for (String parte : partes) {
            if (!parte.isEmpty() && Character.isLetter(parte.charAt(0))) {
                iniciales.append(Character.toUpperCase(parte.charAt(0))).append(". ");
                tomadas++;
                if (tomadas >= 2) {
                    break;
                }
            }
        }
        return tomadas > 0 ? iniciales.toString().trim() : "Hogar Beneficiario";
    }

    public static class ParroquiaPublicaItem {
        private final String nombre;
        private final String canton;
        private final long totalBeneficiarios;
        private final long totalEntregas;
        private final boolean matriz;

        public ParroquiaPublicaItem(String nombre, String canton, long totalBeneficiarios, long totalEntregas, boolean matriz) {
            this.nombre = nombre;
            this.canton = canton;
            this.totalBeneficiarios = totalBeneficiarios;
            this.totalEntregas = totalEntregas;
            this.matriz = matriz;
        }

        public String getNombre() {
            return nombre;
        }

        public String getCanton() {
            return canton;
        }

        public long getTotalBeneficiarios() {
            return totalBeneficiarios;
        }

        public long getTotalEntregas() {
            return totalEntregas;
        }

        public boolean isMatriz() {
            return matriz;
        }
    }

    public static class CategoriaPublicaItem {
        private final String categoria;
        private final long unidadesDisponibles;

        public CategoriaPublicaItem(String categoria, long unidadesDisponibles) {
            this.categoria = categoria;
            this.unidadesDisponibles = unidadesDisponibles;
        }

        public String getCategoria() {
            return categoria;
        }

        public long getUnidadesDisponibles() {
            return unidadesDisponibles;
        }
    }

    public static class EntregaPublicaItem {
        private final String codigoActa;
        private final String fechaFormateada;
        private final String beneficiarioAnonimizado;
        private final String parroquiaNombre;

        public EntregaPublicaItem(String codigoActa, String fechaFormateada, String beneficiarioAnonimizado, String parroquiaNombre) {
            this.codigoActa = codigoActa;
            this.fechaFormateada = fechaFormateada;
            this.beneficiarioAnonimizado = beneficiarioAnonimizado;
            this.parroquiaNombre = parroquiaNombre;
        }

        public String getCodigoActa() {
            return codigoActa;
        }

        public String getFechaFormateada() {
            return fechaFormateada;
        }

        public String getBeneficiarioAnonimizado() {
            return beneficiarioAnonimizado;
        }

        public String getParroquiaNombre() {
            return parroquiaNombre;
        }
    }
}
