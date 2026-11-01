package com.fundacionunidosporpinias.fundacion_web.mapper;

import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.model.Entrega;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface EntregaResumenMapper {

    @Mapping(
            target = "beneficiarioNombre",
            expression =
                    "java(entity.getBeneficiario().getNombre() + \" \" + entity.getBeneficiario().getApellido())"
    )
    @Mapping(
            target = "parroquiaNombre",
            source = "beneficiario.parroquia.nombre"
    )
    EntregaResumenDTO toResumenDTO(
            Entrega entity
    );

    List<EntregaResumenDTO> toResumenDTOList(
            List<Entrega> entities
    );
}
