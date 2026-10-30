package com.fundacionunidosporpinias.fundacion_web.service;

import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaResponseDTO;

import java.util.List;

public interface IParroquiaService {

    List<ParroquiaResponseDTO> findAll();

    ParroquiaResponseDTO findById(Long id);

    ParroquiaResponseDTO save(ParroquiaRequestDTO dto);

    ParroquiaResponseDTO update(Long id, ParroquiaRequestDTO dto);

    void delete(Long id);

}
