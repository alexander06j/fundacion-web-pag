package com.fundacionunidosporpinias.fundacion_web.service.imp;

import com.fundacionunidosporpinias.fundacion_web.dto.producto.ProductoRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.producto.ProductoResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.exception.NotFoundException;
import com.fundacionunidosporpinias.fundacion_web.mapper.ProductoMapper;
import com.fundacionunidosporpinias.fundacion_web.model.Producto;
import com.fundacionunidosporpinias.fundacion_web.repository.ProductoRepository;
import com.fundacionunidosporpinias.fundacion_web.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private ProductoRepository repository;

    @Autowired
    private ProductoMapper mapper;

    @Override
    public List<ProductoResponseDTO> findAll() {
        return mapper.toResponseDTOList(repository.findAll());
    }

    @Override
    public ProductoResponseDTO findById(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(()-> new NotFoundException("Producto no encontrado"));
        return mapper.toResponseDTO(producto);
    }

    @Override
    public ProductoResponseDTO save(ProductoRequestDTO dto) {
        Producto producto = mapper.toEntity(dto);
        return mapper.toResponseDTO(repository.save(producto));
    }

    @Override
    public ProductoResponseDTO update(Long id,
                                      ProductoRequestDTO dto) {

        Producto producto = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Producto no encontrado"));

        producto.setNombre(dto.getNombre());
        producto.setStock(dto.getStock());
        producto.setDescripcion(dto.getDescripcion());
        producto.setCategoria(dto.getCategoria());

        return mapper.toResponseDTO(
                repository.save(producto)
        );
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
