package com.fundacionunidosporpinias.fundacion_web.service;

import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;

import java.util.List;

public interface IEntregaService {

    List<EntregaResumenDTO> findAll();

    EntregaResponseDTO findById(Long id);

    EntregaResponseDTO registrarEntrega(EntregaRequestDTO dto);

    void delete(Long id);
}
