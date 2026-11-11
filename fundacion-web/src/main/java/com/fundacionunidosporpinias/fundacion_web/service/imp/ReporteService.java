package com.fundacionunidosporpinias.fundacion_web.service.imp;


import com.fundacionunidosporpinias.fundacion_web.exception.NotFoundException;
import com.fundacionunidosporpinias.fundacion_web.model.Beneficiario;
import com.fundacionunidosporpinias.fundacion_web.model.Entrega;
import com.fundacionunidosporpinias.fundacion_web.repository.BeneficiarioRepository;
import com.fundacionunidosporpinias.fundacion_web.repository.EntregaRepository;
import com.fundacionunidosporpinias.fundacion_web.repository.UsuarioRepository;
import com.fundacionunidosporpinias.fundacion_web.service.IReporteService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService implements IReporteService {


    private final BeneficiarioRepository beneficiarioRepository;
    private final EntregaRepository entregaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActaEntregaPdfGenerator pdfGenerator;
    private final ExcelReporteGenerator excelGenerator;

    public ReporteService(BeneficiarioRepository beneficiarioRepository,
                          EntregaRepository entregaRepository,
                          UsuarioRepository usuarioRepository,
                          ActaEntregaPdfGenerator pdfGenerator,
                          ExcelReporteGenerator excelGenerator) {
        this.beneficiarioRepository = beneficiarioRepository;
        this.entregaRepository = entregaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pdfGenerator = pdfGenerator;
        this.excelGenerator = excelGenerator;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarActaEntregaPdf(Long entregaId) {
        Entrega entrega = entregaRepository.buscarConDetallePorId(entregaId)
                .orElseThrow(() -> new NotFoundException("Entrega no encontrada con ID: " + entregaId));

        String nombreResponsable = resolverNombreUsuario(entrega.getCreadoPor());
        return pdfGenerator.generar(entrega, nombreResponsable);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarBeneficiariosExcel(String q) {
        String filtro = q == null ? "" : q.trim();
        List<Beneficiario> beneficiarios = beneficiarioRepository.buscarParaReporte(filtro);

        Map<Long, Long> entregasPorBeneficiario = new HashMap<>();
        for (Object[] fila : entregaRepository.contarPorBeneficiario()) {
            Long benId = (Long) fila[0];
            Long total = (Long) fila[1];
            entregasPorBeneficiario.put(benId, total);
        }

        return excelGenerator.generarBeneficiariosXlsx(beneficiarios, entregasPorBeneficiario, filtro);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarEntregasExcel(String q, LocalDate desde, LocalDate hasta) {
        String filtro = q == null ? "" : q.trim();
        List<Entrega> entregas = entregaRepository.buscarParaReporte(filtro, desde, hasta);
        return excelGenerator.generarEntregasXlsx(entregas, filtro, desde, hasta);
    }

    private String resolverNombreUsuario(String username) {
        if (username == null || username.isBlank()) {
            return "Administración General";
        }
        return usuarioRepository.findByUsername(username)
                .map(u -> u.getNombreCompleto() + " (" + u.getUsername() + ")")
                .orElse(username);
    }

}
