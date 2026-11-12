package com.fundacionunidosporpinias.fundacion_web.service.imp;

import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioRequestDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.beneficiario.BeneficiarioResponseDTO;
import com.fundacionunidosporpinias.fundacion_web.dto.entrega.EntregaResumenDTO;
import com.fundacionunidosporpinias.fundacion_web.exception.NotFoundException;
import com.fundacionunidosporpinias.fundacion_web.mapper.BeneficiarioMapper;
import com.fundacionunidosporpinias.fundacion_web.mapper.EntregaResumenMapper;
import com.fundacionunidosporpinias.fundacion_web.model.Beneficiario;
import com.fundacionunidosporpinias.fundacion_web.model.Parroquia;
import com.fundacionunidosporpinias.fundacion_web.model.Sexo;
import com.fundacionunidosporpinias.fundacion_web.repository.BeneficiarioRepository;
import com.fundacionunidosporpinias.fundacion_web.repository.ParroquiaRepository;
import com.fundacionunidosporpinias.fundacion_web.service.IBeneficiarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BeneficiarioService implements IBeneficiarioService {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private BeneficiarioMapper mapper;

    @Autowired
    private ParroquiaRepository parroquiaRepository;

    @Autowired
    private EntregaResumenMapper entregaResumenMapper;

    @Override
    public List<BeneficiarioResponseDTO> findAll() {
        return mapper.toResponseDTOList(beneficiarioRepository.findAll());
    }

    @Override
    public Page<BeneficiarioResponseDTO> buscarPaginado(String q, Sexo sexo, LocalDate desde, LocalDate hasta, Pageable pageable) {
        String filtro = q == null ? "" : q.trim();
        return beneficiarioRepository.buscar(filtro, sexo, desde, hasta, pageable).map(mapper::toResponseDTO);
    }

    @Override
    public BeneficiarioResponseDTO findById(Long id) {
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Beneficiario no encontrado"));
        return mapper.toResponseDTO(beneficiario);
    }

    @Override
    public BeneficiarioResponseDTO save(BeneficiarioRequestDTO dto) {
        Parroquia parroquia = parroquiaRepository.findById(dto.getParroquiaId())
                .orElseThrow(() -> new NotFoundException("Parroquia no encontrada"));

        Beneficiario beneficiario = new Beneficiario();
        aplicarDatos(beneficiario, dto, parroquia);

        return mapper.toResponseDTO(beneficiarioRepository.save(beneficiario));
    }

    @Override
    public BeneficiarioResponseDTO update(Long id, BeneficiarioRequestDTO dto) {
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Beneficiario no encontrado"));

        Parroquia parroquia = parroquiaRepository.findById(dto.getParroquiaId())
                .orElseThrow(() -> new NotFoundException("Parroquia no encontrada"));

        aplicarDatos(beneficiario, dto, parroquia);

        return mapper.toResponseDTO(beneficiarioRepository.save(beneficiario));
    }

    private void aplicarDatos(Beneficiario beneficiario, BeneficiarioRequestDTO dto, Parroquia parroquia) {
        beneficiario.setCedula(dto.getCedula());
        beneficiario.setNombre(dto.getNombre());
        beneficiario.setApellido(dto.getApellido());
        beneficiario.setTelefono(dto.getTelefono());
        beneficiario.setDireccion(dto.getDireccion());
        beneficiario.setFechaNacimiento(dto.getFechaNacimiento());
        beneficiario.setSexo(dto.getSexo());
        beneficiario.setDetalles(dto.getDetalles() != null ? dto.getDetalles().trim() : null);
        beneficiario.setParroquia(parroquia);
    }

    @Override
    public void delete(Long id) {
        beneficiarioRepository.deleteById(id);
    }

    @Override
    public List<BeneficiarioResponseDTO> findByParroquia(Long parroquiaId) {
        return mapper.toResponseDTOList(beneficiarioRepository.findByParroquiaId(parroquiaId));
    }

    @Override
    public BeneficiarioResponseDTO findByCedula(String cedula) {
        Beneficiario beneficiario = beneficiarioRepository.findByCedula(cedula)
                .orElseThrow(() -> new NotFoundException("Beneficiario no encontrado"));
        return mapper.toResponseDTO(beneficiario);
    }

    @Override
    public List<EntregaResumenDTO> obtenerHistorial(Long beneficiarioId) {
        Beneficiario beneficiario = beneficiarioRepository.findById(beneficiarioId)
                .orElseThrow(() -> new NotFoundException("Beneficiario no encontrado"));
        return entregaResumenMapper.toResumenDTOList(beneficiario.getEntregas());
    }
}

