package com.fundacionunidosporpinias.fundacion_web.service;

import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;

import java.util.List;

public interface IBeneficiarioService {

    List<BeneficiarioResponseDTO> findAll();

    BeneficiarioResponseDTO findById(Long id);

    BeneficiarioResponseDTO save(BeneficiarioRequestDTO dto);

    BeneficiarioResponseDTO update(Long id, BeneficiarioRequestDTO dto);

    void delete(Long id);

    List<BeneficiarioResponseDTO> findByParroquia(Long parroquiaId);

    BeneficiarioResponseDTO findByCedula(String cedula);

    List<EntregaResumenDTO> obtenerHistorial(Long beneficiarioId);
}
