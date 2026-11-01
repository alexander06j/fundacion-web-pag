package com.fundacionunidosporpinias.fundacion_web.mapper;

import com.fundacionunidosporpinias.fundacion_web.dto.producto.ProductoRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.producto.ProductoResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.model.Producto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ProductoMapper {

    Producto toEntity(ProductoRequestDTO dto);

    ProductoResponseDTO toResponseDTO(Producto entity);

    List<ProductoResponseDTO> toResponseDTOList(List<Producto> entities);
}
