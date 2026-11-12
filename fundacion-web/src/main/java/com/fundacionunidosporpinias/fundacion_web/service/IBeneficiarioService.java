package com.fundacionunidosporpinias.fundacion_web.service;

import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.model.Sexo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IBeneficiarioService {

    List<BeneficiarioResponseDTO> findAll();

    // Búsqueda paginada en servidor (texto, sexo y rango de fecha de nacimiento)
    Page<BeneficiarioResponseDTO> buscarPaginado(String q, Sexo sexo, LocalDate desde, LocalDate hasta, Pageable pageable);

    default Page<BeneficiarioResponseDTO> buscarPaginado(String q, Pageable pageable) {
        return buscarPaginado(q, null, null, null, pageable);
    }

    BeneficiarioResponseDTO findById(Long id);

    BeneficiarioResponseDTO save(BeneficiarioRequestDTO dto);

    BeneficiarioResponseDTO update(Long id, BeneficiarioRequestDTO dto);

    void delete(Long id);

    List<BeneficiarioResponseDTO> findByParroquia(Long parroquiaId);

    BeneficiarioResponseDTO findByCedula(String cedula);

    List<EntregaResumenDTO> obtenerHistorial(Long beneficiarioId);
}
