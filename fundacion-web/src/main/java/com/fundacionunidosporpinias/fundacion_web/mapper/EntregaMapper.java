package com.fundacionunidosporpinias.fundacion_web.mapper;

import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.model.Entrega;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        config = MapStructConfig.class,
        uses = {
                DetalleEntregaMapper.class
        }
)
public interface EntregaMapper {

    @Mapping(
            target = "beneficiarioId",
            source = "beneficiario.id"
    )
    @Mapping(
            target = "beneficiarioNombre",
            expression =
                    "java(entity.getBeneficiario().getNombre() + \" \" + entity.getBeneficiario().getApellido())"
    )
    @Mapping(
            target = "parroquiaNombre",
            source = "beneficiario.parroquia.nombre"
    )
    @Mapping(
            target = "detalles",
            source = "detalles"
    )
    EntregaResponseDTO toResponseDTO(
            Entrega entity
    );

    List<EntregaResponseDTO> toResponseDTOList(
            List<Entrega> entities
    );

}
