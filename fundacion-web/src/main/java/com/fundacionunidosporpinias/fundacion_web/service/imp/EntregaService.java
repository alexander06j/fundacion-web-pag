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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

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
    public Page<EntregaResumenDTO> buscarPaginado(String q, LocalDate desde, LocalDate hasta, Pageable pageable) {
        String filtro = q == null ? "" : q.trim();
        return entregaRepository.buscar(filtro, desde, hasta, pageable).map(resumenMapper::toResumenDTO);
    }

    @Override
    public EntregaResponseDTO findById(Long id) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Entrega no encontrada"));
        return entregaMapper.toResponseDTO(entrega);
    }

    // Transaccional: valida todos los productos antes de guardar y revierte si falla alguno.
    @Override
    @Transactional
    public EntregaResponseDTO registrarEntrega(EntregaRequestDTO dto) {
        if (dto == null || dto.getBeneficiarioId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un beneficiario.");
        }

        Beneficiario beneficiario = beneficiarioRepository.findById(dto.getBeneficiarioId())
                .orElseThrow(() -> new NotFoundException("Beneficiario no encontrado"));

        List<ProductoCantidad> itemsValidados = validarDetalles(dto.getDetalles());

        Entrega entrega = new Entrega();
        entrega.setFechaEntrega(LocalDate.now());
        entrega.setObservacion(dto.getObservacion());
        entrega.setBeneficiario(beneficiario);
        entrega = entregaRepository.save(entrega);

        List<DetalleEntrega> detalles = new ArrayList<>();
        for (ProductoCantidad item : itemsValidados) {
            Producto producto = item.producto();
            producto.setStock(producto.getStock() - item.cantidad());
            productoRepository.save(producto);

            DetalleEntrega detalle = new DetalleEntrega();
            detalle.setEntrega(entrega);
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            detalleRepository.save(detalle);

            detalles.add(detalle);
        }

        entrega.setDetalles(detalles);
        return entregaMapper.toResponseDTO(entrega);
    }

    private List<ProductoCantidad> validarDetalles(List<DetalleEntregaRequestDTO> detallesDto) {
        List<DetalleEntregaRequestDTO> detallesValidos = detallesDto == null
                ? List.of()
                : detallesDto.stream().filter(Objects::nonNull).toList();

        if (detallesValidos.isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto a la entrega.");
        }

        Set<Long> productosVistos = new HashSet<>();
        List<ProductoCantidad> resultado = new ArrayList<>(detallesValidos.size());

        for (DetalleEntregaRequestDTO detalleDto : detallesValidos) {
            if (detalleDto.getProductoId() == null) {
                throw new IllegalArgumentException("Todos los ítems deben tener un producto seleccionado.");
            }
            if (detalleDto.getCantidad() == null || detalleDto.getCantidad() < 1) {
                throw new IllegalArgumentException("La cantidad de cada producto debe ser al menos 1.");
            }

            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

            if (!productosVistos.add(producto.getId())) {
                throw new IllegalArgumentException(
                        "El producto \"" + producto.getNombre()
                                + "\" está repetido en la entrega. Ajuste la cantidad en una sola línea.");
            }

            int stockActual = producto.getStock() != null ? producto.getStock() : 0;
            if (stockActual < detalleDto.getCantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente para \"" + producto.getNombre()
                                + "\" (disponible: " + stockActual + ", solicitado: " + detalleDto.getCantidad() + ").");
            }

            resultado.add(new ProductoCantidad(producto, detalleDto.getCantidad()));
        }

        return resultado;
    }

    private record ProductoCantidad(Producto producto, int cantidad) {
    }

    @Override
    public void delete(Long id) {
        entregaRepository.deleteById(id);
    }
}
