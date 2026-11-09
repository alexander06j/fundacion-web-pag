package com.fundacionunidosporpinias.fundacion_web.service;

import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IEntregaService {

    List<EntregaResumenDTO> findAll();

    // Búsqueda paginada en servidor (beneficiario, cédula, parroquia y rango de fechas)
    Page<EntregaResumenDTO> buscarPaginado(String q, LocalDate desde, LocalDate hasta, Pageable pageable);

    EntregaResponseDTO findById(Long id);

    EntregaResponseDTO registrarEntrega(EntregaRequestDTO dto);

    void delete(Long id);
}
