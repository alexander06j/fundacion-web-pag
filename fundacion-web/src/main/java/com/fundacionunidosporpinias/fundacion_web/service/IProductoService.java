package com.fundacionunidosporpinias.fundacion_web.service;

import com.fundacionunidosporpinias.fundacion_web.dto.producto.ProductoRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.producto.ProductoResponseDTO;

import java.util.List;

public interface IProductoService {

    List<ProductoResponseDTO> findAll();

    ProductoResponseDTO findById(Long id);

    ProductoResponseDTO save(ProductoRequestDTO dto);

    ProductoResponseDTO update(Long id, ProductoRequestDTO dto);

    void delete(Long id);

}
