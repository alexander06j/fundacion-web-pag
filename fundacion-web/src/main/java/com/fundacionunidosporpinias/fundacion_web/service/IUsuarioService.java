package com.fundacionunidosporpinias.fundacion_web.service;

import com.fundacionunidosporpinias.fundacion_web.dto.usuario.UsuarioRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.usuario.UsuarioResponseDTO;

import java.util.List;

public interface IUsuarioService {

    List<UsuarioResponseDTO> findAll();

    UsuarioResponseDTO findById(Long id);

    UsuarioResponseDTO save(UsuarioRequestDTO dto);

    UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto);

    UsuarioResponseDTO cambiarEstado(Long id);

    boolean existsByUsername(String username);

}
