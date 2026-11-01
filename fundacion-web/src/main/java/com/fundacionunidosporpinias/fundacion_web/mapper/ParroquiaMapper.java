package com.fundacionunidosporpinias.fundacion_web.mapper;

import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.parroquia.ParroquiaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.model.Parroquia;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ParroquiaMapper {

    Parroquia toEntity(ParroquiaRequestDTO dto);

    ParroquiaResponseDTO toResponseDTO(Parroquia entity);

    List<ParroquiaResponseDTO> toResponseDTOList(List<Parroquia> entities);
}
