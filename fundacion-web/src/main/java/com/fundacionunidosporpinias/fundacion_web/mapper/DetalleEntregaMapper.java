package com.fundacionunidosporpinias.fundacion_web.mapper;

import com.fundacionunidosporpinias.fundacion_web.dto.entrega.DetalleEntregaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.model.DetalleEntrega;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface DetalleEntregaMapper {

    @Mapping(
            target = "productoId",
            source = "producto.id"
    )
    @Mapping(
            target = "productoNombre",
            source = "producto.nombre"
    )
    DetalleEntregaResponseDTO toResponseDTO(
            DetalleEntrega entity
    );

    List<DetalleEntregaResponseDTO> toResponseDTOList(
            List<DetalleEntrega> entities
    );
}
