package com.fundacionunidosporpinias.fundacion_web.mapper;

import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.model.Beneficiario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface BeneficiarioMapper {

    @Mapping(target = "parroquiaId",
            source = "parroquia.id")
    @Mapping(target = "parroquiaNombre",
            source = "parroquia.nombre")
    BeneficiarioResponseDTO toResponseDTO(
            Beneficiario entity
    );

    List<BeneficiarioResponseDTO> toResponseDTOList(
            List<Beneficiario> entities
    );

    @Mapping(
            target = "nombreCompleto",
            expression =
                    "java(entity.getNombre() + \" \" + entity.getApellido())")
    BeneficiarioResumenDTO toResumenDTO(
            Beneficiario entity
    );

    List<BeneficiarioResumenDTO> toResumenDTOList(
            List<Beneficiario> entities
    );

}
