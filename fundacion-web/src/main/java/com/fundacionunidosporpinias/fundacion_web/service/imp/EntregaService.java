package com.fundacionunidosporpinias.fundacion_web.service.imp;

import com.fundacionunidosporpinias.fundacion_web.dto.entrega.DetalleEntregaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.exception.NotFoundException;
import com.fundacionunidosporpinias.fundacion_web.mapper.EntregaMapper;
import com.fundacionunidosporpinias.fundacion_web.mapper.EntregaResumenMapper;
import com.fundacionunidosporpinias.fundacion_web.model.Beneficiario;
import com.fundacionunidosporpinias.fundacion_web.model.DetalleEntrega;
import com.fundacionunidosporpinias.fundacion_web.model.Entrega;
import com.fundacionunidosporpinias.fundacion_web.model.Producto;
import com.fundacionunidosporpinias.fundacion_web.repository.BeneficiarioRepository;
import com.fundacionunidosporpinias.fundacion_web.repository.DetalleEntregaRepository;
import com.fundacionunidosporpinias.fundacion_web.repository.EntregaRepository;
import com.fundacionunidosporpinias.fundacion_web.repository.ProductoRepository;
import com.fundacionunidosporpinias.fundacion_web.service.IEntregaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntregaService implements IEntregaService {

    @Autowired
    private EntregaRepository entregaRepository;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleEntregaRepository detalleRepository;

    @Autowired
    private EntregaMapper entregaMapper;

    @Autowired
    private EntregaResumenMapper resumenMapper;

    @Override
    public List<EntregaResumenDTO> findAll() {
        return resumenMapper.toResumenDTOList(entregaRepository.findAll());
    }

    @Override
    public EntregaResponseDTO findById(Long id) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Entrega no encontrada"));
        return entregaMapper.toResponseDTO(entrega);
    }

    @Override
    public EntregaResponseDTO registrarEntrega(EntregaRequestDTO dto) {
        Beneficiario beneficiario = beneficiarioRepository.findById(dto.getBeneficiarioId())
                .orElseThrow(()-> new NotFoundException("Beneficiario no encontrado"));

        Entrega entrega = new Entrega();

        entrega.setFechaEntrega(LocalDate.now());
        entrega.setObservacion(dto.getObservacion());
        entrega.setBeneficiario(beneficiario);

        entrega = entregaRepository.save(entrega);

        List<DetalleEntrega> detalles = new ArrayList<>();

        for (DetalleEntregaRequestDTO detalleDto : dto.getDetalles()){
            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(()-> new NotFoundException("Producto no encontrado"));

            if(producto.getStock() < detalleDto.getCantidad()){
                throw new NotFoundException("Stock insuficiente para: " + producto.getNombre());
            }
            producto.setStock(producto.getStock() - detalleDto.getCantidad());

            productoRepository.save(producto);

            DetalleEntrega detalle = new DetalleEntrega();

            detalle.setEntrega(entrega);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDto.getCantidad());

            detalleRepository.save(detalle);

            detalles.add(detalle);
        }

        entrega.setDetalles(detalles);
        return entregaMapper.toResponseDTO(entrega);
    }

    @Override
    public void delete(Long id) {
        entregaRepository.deleteById(id);
    }
}
